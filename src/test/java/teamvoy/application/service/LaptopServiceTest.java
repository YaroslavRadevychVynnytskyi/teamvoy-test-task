package teamvoy.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;
import teamvoy.application.entity.Laptop;
import teamvoy.application.mapper.LaptopMapper;
import teamvoy.application.repo.LaptopRepository;
import teamvoy.application.service.impl.LaptopServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LaptopServiceTest {
    @Mock
    private LaptopRepository laptopRepository;

    @Mock
    private LaptopMapper laptopMapper;

    @InjectMocks
    private LaptopServiceImpl laptopService;

    @Test
    void createLaptop_AllOk_ShouldReturnCorrectDto() {
        LaptopRequestDto laptopRequestDto = new LaptopRequestDto(
                "HP",
                "ZBook 14",
                "Intel Core i5-1145G7",
                16,
                BigDecimal.valueOf(1400),
                34
        );

        Laptop savedLaptop = new Laptop();
        savedLaptop.setLaptopId(UUID.randomUUID());
        savedLaptop.setBrand("HP");
        savedLaptop.setModel("ZBook 14");
        savedLaptop.setProcessor("Intel Core i5-1145G7");
        savedLaptop.setRam(16);
        savedLaptop.setPrice(BigDecimal.valueOf(1400));
        savedLaptop.setQuantity(34);
        savedLaptop.setCreatedAt(LocalDateTime.now());

        LaptopResponseDto expected = new LaptopResponseDto(
                savedLaptop.getLaptopId(),
                "HP",
                "ZBook 14",
                "Intel Core i5-1145G7",
                16,
                BigDecimal.valueOf(1400),
                34,
                savedLaptop.getCreatedAt()
        );


        when(laptopRepository.save(any(Laptop.class))).thenReturn(savedLaptop);
        when(laptopMapper.toDto(savedLaptop)).thenReturn(expected);

        LaptopResponseDto actual = laptopService.createLaptop(laptopRequestDto);

        assertEquals(expected, actual);

        verify(laptopRepository, times(1)).save(any(Laptop.class));
        verify(laptopMapper, times(1)).toDto(savedLaptop);
    }

    @Test
    void getAll_AllOk_ShouldReturnListOfLaptopResponseDtos() {
        Pageable pageable = mock(Pageable.class);

        Laptop laptop1 = new Laptop();
        laptop1.setLaptopId(UUID.randomUUID());
        laptop1.setBrand("Apple");
        laptop1.setModel("MacBook Air");
        laptop1.setProcessor("Apple M1");
        laptop1.setRam(16);
        laptop1.setPrice(new BigDecimal("1200.50"));
        laptop1.setQuantity(10);
        laptop1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Laptop laptop2 = new Laptop();
        laptop2.setLaptopId(UUID.randomUUID());
        laptop2.setBrand("Dell");
        laptop2.setModel("Latitude");
        laptop2.setProcessor("AMD Ryzen 3");
        laptop2.setRam(8);
        laptop2.setPrice(new BigDecimal("800.00"));
        laptop2.setQuantity(5);
        laptop2.setCreatedAt(LocalDateTime.now().minusDays(2));

        Page<Laptop> laptopPage = new PageImpl<>(List.of(laptop1, laptop2));

        LaptopResponseDto responseDto1 = new LaptopResponseDto(
                laptop1.getLaptopId(),
                laptop1.getBrand(),
                laptop1.getModel(),
                laptop1.getProcessor(),
                laptop1.getRam(),
                laptop1.getPrice(),
                laptop1.getQuantity(),
                laptop1.getCreatedAt()
        );

        LaptopResponseDto responseDto2 = new LaptopResponseDto(
                laptop2.getLaptopId(),
                laptop2.getBrand(),
                laptop2.getModel(),
                laptop2.getProcessor(),
                laptop2.getRam(),
                laptop2.getPrice(),
                laptop2.getQuantity(),
                laptop2.getCreatedAt()
        );

        when(laptopRepository.findAll(pageable)).thenReturn(laptopPage);
        when(laptopMapper.toDto(laptop1)).thenReturn(responseDto1);
        when(laptopMapper.toDto(laptop2)).thenReturn(responseDto2);

        List<LaptopResponseDto> result = laptopService.getAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(responseDto1, result.get(0));
        assertEquals(responseDto2, result.get(1));

        verify(laptopRepository, times(1)).findAll(pageable);
        verify(laptopMapper, times(1)).toDto(laptop1);
        verify(laptopMapper, times(1)).toDto(laptop2);
    }
}
