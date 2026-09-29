package pl.logren12.broadside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logren12.broadside.model.BattleTurn;

import java.util.List;

@Repository
public interface BattleTurnRepository extends JpaRepository<BattleTurn, Long> {
    List<BattleTurn> findByBattleId(Long battleId);

    BattleTurn findByBattleIdAndTurnNumber(Long battleId, Integer turnNumber);
}
