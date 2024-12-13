package teamvoy.application.repo;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import teamvoy.application.entity.Laptop;

public interface LaptopRepository extends JpaRepository<Laptop, UUID> {
}
