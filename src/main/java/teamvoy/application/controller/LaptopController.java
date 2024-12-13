package teamvoy.application.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<LaptopResponseDto> createLaptop(@RequestBody @Valid LaptopRequestDto requestDto) {
        return ResponseEntity.ok(laptopService.createLaptop(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<LaptopResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(laptopService.getAll(pageable));
    }
}
