package teamvoy.application.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamvoy.application.dto.order.request.OrderItemDto;
import teamvoy.application.dto.order.request.OrderRequestDto;
import teamvoy.application.dto.order.response.OrderResponseDto;
import teamvoy.application.entity.Laptop;
import teamvoy.application.entity.Order;
import teamvoy.application.entity.OrderItem;
import teamvoy.application.entity.enums.OrderStatus;
import teamvoy.application.mapper.OrderMapper;
import teamvoy.application.repo.LaptopRepository;
import teamvoy.application.repo.OrderRepository;
import teamvoy.application.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final LaptopRepository laptopRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * Places an order for laptops, validating stock quantities and updating the stock.
     * Builds an order and saves it to the database.
     *
     * @param requestDto the details of the order, including user ID and ordered items
     * @return a response DTO representing the placed order
     * @throws IllegalArgumentException if the requested quantity exceeds the available stock
     */
    @Override
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        List<UUID> laptopsIds = requestDto.orderItemsDtoSet().stream()
                .map(OrderItemDto::laptopId)
                .toList();
        List<Laptop> laptops = laptopRepository.findAllById(laptopsIds);

        Map<UUID, Laptop> laptopMap = laptops.stream()
                .collect(Collectors.toMap(Laptop::getLaptopId, Function.identity()));

        Map<UUID, Integer> requestedQuantities = requestDto.orderItemsDtoSet().stream()
                .collect(Collectors.toMap(OrderItemDto::laptopId, OrderItemDto::quantity));

        validateLaptopsStockQuantity(requestedQuantities, laptops);

        Set<OrderItem> orderItems = buildOrderItems(requestDto.orderItemsDtoSet(), laptopMap);

        Order order = Order.builder()
                .userId(requestDto.userId())
                .orderItems(orderItems)
                .totalAmount(calculateTotalAmount(orderItems))
                .timestamp(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        updateLaptopsStockQuantity(requestedQuantities, laptops, (initial, requested) -> initial - requested);

        return orderMapper.toDto(orderRepository.save(order));
    }

    /**
     * Deletes unpaid orders that have been pending for more than 10 minutes.
     * Restores the stock quantities of laptops from the deleted orders.
     * Scheduled to run every minute.
     */
    @Override
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deleteNotPaidOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Order> unpaidOrders = orderRepository.findAllByOrderStatusAndTimestampBefore(OrderStatus.PENDING, currentTime.minusMinutes(10));

        if (unpaidOrders.isEmpty()) {
            return;
        }

        unpaidOrders.forEach(order -> {
            Map<UUID, Integer> orderedQuantities = order.getOrderItems().stream()
                    .collect(Collectors.toMap(
                            orderItem -> orderItem.getLaptop().getLaptopId(),
                            OrderItem::getQuantity));

            List<UUID> laptopIds = orderedQuantities.keySet().stream().toList();
            List<Laptop> laptops = laptopRepository.findAllById(laptopIds);

            updateLaptopsStockQuantity(orderedQuantities, laptops, Integer::sum);
        });

        orderRepository.deleteAll(unpaidOrders);
    }

    /**
     * Marks the specified order as paid.
     *
     * @param orderId the unique identifier of the order to mark as paid
     * @throws EntityNotFoundException if no order with the specified ID is found
     * @throws IllegalStateException   if the order is already marked as paid
     */
    @Override
    public void markOrderAsPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with ID: " + orderId));

        if (order.getOrderStatus().equals(OrderStatus.PAID)) {
            throw new IllegalStateException("Order with ID: " + orderId + " is already marked as paid");
        }

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    /**
     * Validates that the requested quantities of laptops are available in stock.
     *
     * @param requestedQuantities a map of laptop IDs to requested quantities
     * @param laptops the list of laptops to validate
     * @throws IllegalArgumentException if any requested quantity exceeds the available stock
     */
    private void validateLaptopsStockQuantity(Map<UUID, Integer> requestedQuantities, List<Laptop> laptops) {
        laptops.forEach(laptop -> {
            Integer requestedQuantity = requestedQuantities.get(laptop.getLaptopId());

            if (requestedQuantity > laptop.getQuantity()) {
                throw new IllegalArgumentException("Requested quantity for laptop with ID: "
                        + laptop.getLaptopId() + " is greater than available in stock");
            }
        });
    }

    /**
     * Builds a set of order items from a DTO set and a map of laptops.
     *
     * @param orderItemDtoSet the DTOs representing the ordered items
     * @param laptopMap a map of laptop IDs to Laptop entities
     * @return a set of constructed {@code OrderItem} entities
     */
    private Set<OrderItem> buildOrderItems(Set<OrderItemDto> orderItemDtoSet, Map<UUID, Laptop> laptopMap) {
        return orderItemDtoSet.stream()
                .map(orderItemDto -> {
                    Laptop laptop = laptopMap.get(orderItemDto.laptopId());

                    OrderItem orderItem = new OrderItem();
                    orderItem.setLaptop(laptop);
                    orderItem.setQuantity(orderItemDto.quantity());
                    orderItem.setTotalPrice(laptop.getPrice()
                            .multiply(BigDecimal.valueOf(orderItem.getQuantity())));

                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Calculates the total amount of an order based on the prices and quantities of its items.
     *
     * @param orderItems the set of order items
     * @return the total amount as a {@code BigDecimal}
     */
    private BigDecimal calculateTotalAmount(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Updates the stock quantities of laptops using a provided operation (e.g., addition or subtraction).
     *
     * @param requestedQuantities a map of laptop IDs to quantities to update
     * @param laptops the list of laptops to update
     * @param operation a {@code BiFunction} defining the operation for updating stock (e.g., add or subtract)
     */
    private void updateLaptopsStockQuantity(Map<UUID, Integer> requestedQuantities,
                                            List<Laptop> laptops,
                                            BiFunction<Integer, Integer, Integer> operation) {
        laptops.forEach(laptop -> {
            Integer initialQuantity = laptop.getQuantity();
            Integer requestedQuantity = requestedQuantities.get(laptop.getLaptopId());

            laptop.setQuantity(operation.apply(initialQuantity, requestedQuantity));
        });

        laptopRepository.saveAll(laptops);
    }
}
