package pl.logren12.broadside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logren12.broadside.model.Ship;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {
}
