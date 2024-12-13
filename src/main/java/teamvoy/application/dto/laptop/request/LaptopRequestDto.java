package teamvoy.application.dto.laptop.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record LaptopRequestDto(
        @NotBlank
        String brand,

        @NotBlank
        String model,

        @NotBlank
        String processor,

        @Min(1)
        Integer ram,

        @Min(1)
        BigDecimal price,

        @Min(1)
        Integer quantity
) {
}
