package pl.logren12.broadside.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logren12.broadside.model.BattleRound;

@Repository
public interface BattleRoundRepository extends JpaRepository<BattleRound, Long> { //todo przeczytać czemu tu jest BattleRound i LONG??
}
