package teamvoy.application.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;
import teamvoy.application.entity.Laptop;
import teamvoy.application.mapper.LaptopMapper;
import teamvoy.application.repo.LaptopRepository;
import teamvoy.application.service.LaptopService;

@Service
@RequiredArgsConstructor
public class LaptopServiceImpl implements LaptopService {
    private final LaptopRepository laptopRepository;
    private final LaptopMapper laptopMapper;

    @Override
    public LaptopResponseDto createLaptop(LaptopRequestDto requestDto) {
        Laptop laptop = new Laptop();
        BeanUtils.copyProperties(requestDto, laptop);
        laptop.setCreatedAt(LocalDateTime.now());

        return laptopMapper.toDto(laptopRepository.save(laptop));
    }

    @Override
    public List<LaptopResponseDto> getAll(Pageable pageable) {
        return laptopRepository.findAll(pageable)
                .stream()
                .map(laptopMapper::toDto)
                .toList();
    }
}
