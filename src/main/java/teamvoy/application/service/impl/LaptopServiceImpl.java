package teamvoy.application.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;
import teamvoy.application.entity.Laptop;
import teamvoy.application.repo.LaptopRepository;
import teamvoy.application.service.LaptopService;

@Service
@RequiredArgsConstructor
public class LaptopServiceImpl implements LaptopService {
    private final LaptopRepository laptopRepository;

    @Override
    public LaptopResponseDto createLaptop(LaptopRequestDto requestDto) {
        Laptop laptop = new Laptop();
        BeanUtils.copyProperties(requestDto, laptop);
        laptop.setCreatedAt(LocalDateTime.now());

        return new LaptopResponseDto(laptopRepository.save(laptop));
    }
}
