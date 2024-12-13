package teamvoy.application.service;

import java.util.UUID;
import teamvoy.application.dto.order.request.OrderRequestDto;
import teamvoy.application.dto.order.response.OrderResponseDto;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto);

    void deleteNotPaidOrders();

    void markOrderAsPaid(UUID orderId);
}
