package teamvoy.application.dto.order.request;

import java.util.Set;
import java.util.UUID;

public record OrderRequestDto(
        UUID userId,
        Set<OrderItemDto> orderItemsDtoSet
) {
}
