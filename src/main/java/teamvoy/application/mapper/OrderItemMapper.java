package teamvoy.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import teamvoy.application.config.MapperConfig;
import teamvoy.application.dto.order.response.OrderItemResponseDto;
import teamvoy.application.entity.OrderItem;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "laptopId", source = "laptop.laptopId")
    @Mapping(target = "brand", source = "laptop.brand")
    @Mapping(target = "model", source = "laptop.model")
    @Mapping(target = "price", source = "laptop.price")
    OrderItemResponseDto toDto(OrderItem orderItem);
}
