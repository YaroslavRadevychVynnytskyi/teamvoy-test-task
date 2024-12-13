package teamvoy.application.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamvoy.application.dto.order.request.OrderRequestDto;
import teamvoy.application.dto.order.response.OrderResponseDto;
import teamvoy.application.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(orderService.placeOrder(requestDto));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> markOrderAsPaid(@PathVariable UUID orderId) {
        orderService.markOrderAsPaid(orderId);

        return ResponseEntity.ok().build();
    }
}
