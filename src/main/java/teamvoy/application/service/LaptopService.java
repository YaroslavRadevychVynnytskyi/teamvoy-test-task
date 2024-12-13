package teamvoy.application.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;

public interface LaptopService {
    LaptopResponseDto createLaptop(LaptopRequestDto requestDto);

    List<LaptopResponseDto> getAll(Pageable pageable);
}
