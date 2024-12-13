package teamvoy.application.service;

import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;

public interface LaptopService {
    LaptopResponseDto createLaptop(LaptopRequestDto requestDto);
}
