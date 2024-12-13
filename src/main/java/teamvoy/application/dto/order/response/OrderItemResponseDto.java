package teamvoy.application.dto.order.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDto(
        UUID laptopId,
        String brand,
        String model,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice
) {
}
