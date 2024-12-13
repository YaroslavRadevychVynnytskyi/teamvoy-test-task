package teamvoy.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamvoy.application.dto.laptop.request.LaptopRequestDto;
import teamvoy.application.dto.laptop.response.LaptopResponseDto;
import teamvoy.application.service.LaptopService;

@RestController
@RequestMapping("/laptops")
@RequiredArgsConstructor
public class LaptopController {
    private final LaptopService laptopService;

    @PostMapping
    public ResponseEntity<LaptopResponseDto> createLaptop(@RequestBody LaptopRequestDto requestDto) {
        return ResponseEntity.ok(laptopService.createLaptop(requestDto));
    }
}
