package teamvoy.application.dto.laptop.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LaptopResponseDto(
        UUID laptopId,
        String brand,
        String model,
        String processor,
        Integer ram,
        BigDecimal price,
        Integer quantity,
        LocalDateTime createdAt
) {
}
