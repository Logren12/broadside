package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.CaptainAction;
import pl.logren12.broadside.model.TurnOutcome;

import java.util.Collections;

@Service
public class BattleService {
    private final NavalCombatService navalCombatService;
    private final AiService aiService;

    public BattleService(NavalCombatService navalCombatService, AiService aiService) {
        this.navalCombatService = navalCombatService;
        this.aiService = aiService;
    }
    public TurnOutcome processTurn(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2){
        TurnOutcome shipCondition = checkShipConditions(captain1, captain2);
        if (shipCondition != TurnOutcome.ONGOING) return shipCondition;

        TurnOutcome navalPhaseOutcome = this.navalCombatService.resolveNavalPhase(captain1, action1 ,captain2, action2);

        if (navalPhaseOutcome == TurnOutcome.CREW_FIGHT_INITIATED) {
            return resolveCrewFight(captain1, captain2);
        }
        else if (navalPhaseOutcome == TurnOutcome.ESCAPED) {
            return TurnOutcome.ESCAPED;
        }
        // TurnOutcome.ONGOING:
        else return checkShipConditions(captain1, captain2);
    }
    // Player versus Bot
    public TurnOutcome processTurn(Captain captain1, CaptainAction action1, Captain captain2){
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processTurn(captain1, action1, captain2, action2);
    }
    // Bot versus Bot
    public TurnOutcome processTurn(Captain captain1, Captain captain2) {
        CaptainAction action1 = this.aiService.aiActionDecision(captain1);
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processTurn(captain1, action1, captain2, action2);
    }

    private TurnOutcome resolveCrewFight(Captain captain1, Captain captain2){
        TurnOutcome result = checkCrewState(captain1, captain2);
        while (result == TurnOutcome.ONGOING){
            int damage1 = captain1.crewAttack();
            int damage2 = captain2.crewAttack();
            captain1.getShip().receiveDamage(Collections.nCopies(damage2, 4)); // 4 is a crew code in receiveDamage
            captain2.getShip().receiveDamage(Collections.nCopies(damage1, 4));
            result = checkCrewState(captain1, captain2);
        }
        return result;
    }
    private TurnOutcome checkCrewState(Captain captain1, Captain captain2){
        if (captain1.getShip().getCrew() == 0 && captain2.getShip().getCrew() == 0) return TurnOutcome.BOTH_DESTROYED;
        if (captain1.getShip().getCrew() == 0) return TurnOutcome.CAPTAIN1_DEFEATED;
        if (captain2.getShip().getCrew() == 0) return TurnOutcome.CAPTAIN2_DEFEATED;
        else return TurnOutcome.ONGOING;
    }
    private TurnOutcome checkShipConditions(Captain captain1, Captain captain2){
        if (captain1.getShip().isDestroyed() && captain2.getShip().isDestroyed()) return TurnOutcome.BOTH_DESTROYED;
        if (captain1.getShip().isDestroyed()) return TurnOutcome.CAPTAIN1_DEFEATED;
        if (captain2.getShip().isDestroyed()) return TurnOutcome.CAPTAIN2_DEFEATED;
        else return TurnOutcome.ONGOING;
    }
}
