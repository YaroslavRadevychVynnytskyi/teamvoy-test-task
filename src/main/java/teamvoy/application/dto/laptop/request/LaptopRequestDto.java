package teamvoy.application.dto.laptop.request;

import java.math.BigDecimal;

public record LaptopRequestDto(
        String brand,
        String model,
        String processor,
        Integer ram,
        BigDecimal price,
        Integer quantity
) {
}
