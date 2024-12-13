package teamvoy.application.mapper;

import org.mapstruct.Mapper;
import teamvoy.application.config.MapperConfig;
import teamvoy.application.dto.order.response.OrderResponseDto;
import teamvoy.application.entity.Order;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderResponseDto toDto(Order order);
}
