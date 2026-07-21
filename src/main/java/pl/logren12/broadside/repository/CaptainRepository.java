package pl.logren12.broadside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.Faction;

import java.util.List;

@Repository
public interface CaptainRepository extends JpaRepository<Captain, Long> {
    List<Captain> findByName(String name);
    List<Captain> findByFaction(Faction faction);
}
