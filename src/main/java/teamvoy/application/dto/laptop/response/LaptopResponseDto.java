package teamvoy.application.dto.laptop.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import teamvoy.application.entity.Laptop;

@Data
public class LaptopResponseDto {
    private UUID laptopId;
    private String brand;
    private String model;
    private String processor;
    private Integer ram;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;

    public LaptopResponseDto(Laptop laptop) {
        laptopId = laptop.getLaptopId();
        brand = laptop.getBrand();
        model = laptop.getModel();
        processor = laptop.getProcessor();
        ram = laptop.getRam();
        price = laptop.getPrice();
        quantity = laptop.getQuantity();
        createdAt = laptop.getCreatedAt();
    }
}

