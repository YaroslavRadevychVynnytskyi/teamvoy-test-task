package teamvoy.application.dto.order.request;

import java.util.UUID;

public record OrderItemDto(
        UUID laptopId,
        Integer quantity
) {
}
