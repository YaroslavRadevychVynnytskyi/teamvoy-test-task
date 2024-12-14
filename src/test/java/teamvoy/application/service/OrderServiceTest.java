package teamvoy.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamvoy.application.dto.order.request.OrderItemDto;
import teamvoy.application.dto.order.request.OrderRequestDto;
import teamvoy.application.dto.order.response.OrderItemResponseDto;
import teamvoy.application.dto.order.response.OrderResponseDto;
import teamvoy.application.entity.Laptop;
import teamvoy.application.entity.Order;
import teamvoy.application.entity.OrderItem;
import teamvoy.application.entity.enums.OrderStatus;
import teamvoy.application.mapper.OrderMapper;
import teamvoy.application.repo.LaptopRepository;
import teamvoy.application.repo.OrderRepository;
import teamvoy.application.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private LaptopRepository laptopRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private UUID laptopId1;
    private UUID laptopId2;
    private Laptop laptop1;
    private Laptop laptop2;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto expectedOrderResponseDto;
    private Order order;

    private UUID uuid1;
    private UUID uuid2;
    private UUID uuid3;
    private Order order1;
    private Order order2;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        laptopId1 = UUID.randomUUID();
        laptopId2 = UUID.randomUUID();

        laptop1 = new Laptop();
        laptop1.setLaptopId(laptopId1);
        laptop1.setBrand("Dell");
        laptop1.setModel("XPS 13");
        laptop1.setPrice(new BigDecimal("1200.00"));
        laptop1.setQuantity(10);

        laptop2 = new Laptop();
        laptop2.setLaptopId(laptopId2);
        laptop2.setBrand("HP");
        laptop2.setModel("Spectre x360");
        laptop2.setPrice(new BigDecimal("1400.00"));
        laptop2.setQuantity(5);

        OrderItemDto orderItemDto1 = new OrderItemDto(laptopId1, 2);
        OrderItemDto orderItemDto2 = new OrderItemDto(laptopId2, 1);
        orderRequestDto = new OrderRequestDto(userId, Set.of(orderItemDto1, orderItemDto2));

        OrderItemResponseDto orderItemResponseDto1 = new OrderItemResponseDto(
                laptopId1, "Dell", "XPS 13",
                new BigDecimal("1200.00"), 2, new BigDecimal("2400.00"));

        OrderItemResponseDto orderItemResponseDto2 = new OrderItemResponseDto(
                laptopId2, "HP", "Spectre x360",
                new BigDecimal("1400.00"), 1, new BigDecimal("1400.00"));

        expectedOrderResponseDto = new OrderResponseDto(
                UUID.randomUUID(),
                userId,
                Set.of(orderItemResponseDto1, orderItemResponseDto2),
                new BigDecimal("3800.00"),
                LocalDateTime.now(), OrderStatus.PENDING);

        order = new Order();
        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTimestamp(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("3800.00"));

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setLaptop(laptop1);
        orderItem1.setQuantity(2);
        orderItem1.setTotalPrice(new BigDecimal("2400.00"));

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setLaptop(laptop2);
        orderItem2.setQuantity(1);
        orderItem2.setTotalPrice(new BigDecimal("1400.00"));

        order.setOrderItems(Set.of(orderItem1, orderItem2));

        uuid1 = UUID.randomUUID();
        uuid2 = UUID.randomUUID();
        uuid3 = UUID.randomUUID();

        order1 = new Order();
        order1.setOrderId(uuid1);
        order1.setOrderItems(Set.of(orderItem1, orderItem2));
        order1.setOrderStatus(OrderStatus.PENDING);

        order2 = new Order();
        order2.setOrderId(uuid2);
        order2.setOrderStatus(OrderStatus.PAID);
    }

    @Test
    public void placeOrder_AllOk_Success() {
        when(laptopRepository.findAllById(anyList()))
                .thenReturn(Stream.of(laptop1, laptop2).collect(Collectors.toList()));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(orderMapper.toDto(order))
                .thenReturn(expectedOrderResponseDto);

        OrderResponseDto result = orderService.placeOrder(orderRequestDto);

        assertNotNull(result);
        assertEquals(expectedOrderResponseDto, result);

        verify(laptopRepository, times(1)).findAllById(anyList());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(laptopRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void placeOrder_InsufficientStock_ShouldThrowIllegalArgumentException() {
        laptop1.setQuantity(1);
        when(laptopRepository.findAllById(anyList()))
                .thenReturn(Stream.of(laptop1, laptop2).collect(Collectors.toList()));

        assertThrows(IllegalArgumentException.class, () ->
                orderService.placeOrder(orderRequestDto));
    }

    @Test
    public void deleteNotPaidOrders_AllOk_Success() {
        when(orderRepository.findAllByOrderStatusAndTimestampBefore(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(order1));
        when(laptopRepository.findAllById(anyList()))
                .thenReturn(List.of(laptop1, laptop2));

        orderService.deleteNotPaidOrders();

        assertEquals(12, laptop1.getQuantity());
        assertEquals(6, laptop2.getQuantity());

        verify(orderRepository).findAllByOrderStatusAndTimestampBefore(eq(OrderStatus.PENDING), any(LocalDateTime.class));
        verify(laptopRepository).findAllById(anyList());
        verify(laptopRepository).saveAll(anyList());
        verify(orderRepository).deleteAll(List.of(order1));
    }

    @Test
    public void deleteNotPaidOrders_NoUnpaidOrders_ShouldDoNothing() {
        when(orderRepository.findAllByOrderStatusAndTimestampBefore(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of());

        orderService.deleteNotPaidOrders();

        verify(orderRepository).findAllByOrderStatusAndTimestampBefore(eq(OrderStatus.PENDING), any(LocalDateTime.class));
        verifyNoMoreInteractions(laptopRepository, orderRepository);
    }

    @Test
    public void markOrderAsPaid_AllOk_Success() {
        when(orderRepository.findById(uuid1)).thenReturn(Optional.of(order1));

        orderService.markOrderAsPaid(uuid1);

        assertEquals(OrderStatus.PAID, order1.getOrderStatus());
        verify(orderRepository).save(order1);
    }

    @Test
    public void markOrderAsPaid_OrderNotFound_ShouldThrowEntityNotFoundException() {
        when(orderRepository.findById(uuid3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.markOrderAsPaid(uuid3));

        verify(orderRepository, never()).save(any(Order.class));
    }
}
