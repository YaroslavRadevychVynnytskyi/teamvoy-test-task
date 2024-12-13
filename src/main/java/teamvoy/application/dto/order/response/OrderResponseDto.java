package teamvoy.application.dto.order.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import teamvoy.application.entity.enums.OrderStatus;

public record OrderResponseDto(
        UUID orderId,
        UUID userId,
        Set<OrderItemResponseDto> orderItems,
        BigDecimal totalAmount,
        LocalDateTime timestamp,
        OrderStatus orderStatus
) {
}
