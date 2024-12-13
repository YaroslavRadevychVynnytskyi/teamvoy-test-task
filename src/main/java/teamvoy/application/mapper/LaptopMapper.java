package teamvoy.application.mapper;

import org.mapstruct.Mapper;

import teamvoy.application.config.MapperConfig;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;
import teamvoy.application.entity.Laptop;

@Mapper(config = MapperConfig.class)
public interface LaptopMapper {
    LaptopResponseDto toDto(Laptop laptop);
}
