package teamvoy.application.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import teamvoy.application.entity.Order;
import teamvoy.application.entity.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = "orderItems")
    List<Order> findAllByOrderStatusAndTimestampBefore(OrderStatus orderStatus, LocalDateTime timestamp);
}
