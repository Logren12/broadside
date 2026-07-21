package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.repository.BattleRoundRepository;
import pl.logren12.broadside.repository.CaptainRepository;

import java.util.Collections;

@Service
public class BattleService {
    private final NavalCombatService navalCombatService;
    private final AiService aiService;
    private final CaptainRepository captainRepository;
    private final BattleRepository battleRepository;
    private final BattleRoundRepository battleRoundRepository;

    public BattleService(NavalCombatService navalCombatService, AiService aiService, CaptainRepository captainRepository, BattleRepository battleRepository, BattleRoundRepository battleRoundRepository) {
        this.navalCombatService = navalCombatService;
        this.aiService = aiService;
        this.captainRepository = captainRepository;
        this.battleRepository = battleRepository;
        this.battleRoundRepository = battleRoundRepository;
    }

    /**
     * Processes a full turn between two captains.
     * <p/>
     * First checks whether both captains are able to perform round. Then invokes NavalCombatService
     * to determine who won maneuvering phase and
     * (if needed) performs crew fight or pases on information about player's successful escape.
     */
    public Battle startABattle(String captain1Name, String captain2Name){
        Captain captain1 = captainRepository.findByName(captain1Name).getFirst();
        Captain captain2 = captainRepository.findByName(captain2Name).getFirst();
        Battle battle = new Battle(captain1, captain2);
        return battleRepository.save(battle);
    }

    public BattleStatus processRound(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2) {
        // check whether both captains are suitable to play round
        BattleStatus shipCondition = checkShipConditions(captain1, captain2);
        if (shipCondition != BattleStatus.ONGOING) return shipCondition;

        // check if any captain managed to escape or board enemy ship. If not calculate damage dealt by firing canons
        TurnOutcome navalPhaseOutcome = this.navalCombatService.resolveNavalPhase(captain1, action1, captain2, action2);
        if (navalPhaseOutcome == TurnOutcome.CREW_FIGHT_INITIATED) {
            return resolveCrewFight(captain1, captain2);
        } else if (navalPhaseOutcome == TurnOutcome.CAPTAIN_ESCAPED) {
            return BattleStatus.CAPTAIN_ESCAPED;
        }
        // TurnOutcome.ONGOING:
        else {
            BattleStatus shipConditions = checkShipConditions(captain1, captain2);
            if (shipConditions == BattleStatus.CAPTAIN1_DEFEATED) {
                captain2.getShip().repair();
            } else if (shipConditions == BattleStatus.CAPTAIN2_DEFEATED) {
                captain1.getShip().repair();
            }
            return shipConditions;
        }
    }

    // Player versus Bot
    public BattleStatus processRound(Captain captain1, CaptainAction action1, Captain captain2) {
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processRound(captain1, action1, captain2, action2);
    }

    // Bot versus Bot
    public BattleStatus processRound(Captain captain1, Captain captain2) {
        CaptainAction action1 = this.aiService.aiActionDecision(captain1);
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processRound(captain1, action1, captain2, action2);
    }

    private BattleStatus resolveCrewFight(Captain captain1, Captain captain2) {
        BattleStatus crewState = checkCrewState(captain1, captain2);
        while (crewState == BattleStatus.ONGOING) {
            int damage1 = captain1.crewAttack();
            int damage2 = captain2.crewAttack();
            captain1.getShip().receiveDamage(Collections.nCopies(damage2, 4)); // 4 is a crew code in receiveDamage
            captain2.getShip().receiveDamage(Collections.nCopies(damage1, 4));
            crewState = checkCrewState(captain1, captain2);
        }
        if (crewState == BattleStatus.CAPTAIN1_DEFEATED) {
            captain2.changeShip(captain1.getShip());
            captain2.getShip().repair();
        }
        if (crewState == BattleStatus.CAPTAIN2_DEFEATED) {
            captain1.changeShip(captain2.getShip());
            captain1.getShip().repair();
        }
        return crewState;
    }

    private BattleStatus checkCrewState(Captain captain1, Captain captain2) {
        if (captain1.getShip().getCrew() <= 0 && captain2.getShip().getCrew() <= 0) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().getCrew() <= 0) return BattleStatus.CAPTAIN1_DEFEATED;
        if (captain2.getShip().getCrew() <= 0) return BattleStatus.CAPTAIN2_DEFEATED;
        else return BattleStatus.ONGOING;
    }

    private BattleStatus checkShipConditions(Captain captain1, Captain captain2) {
        if (captain1.getShip().isDestroyed() && captain2.getShip().isDestroyed()) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().isDestroyed()) return BattleStatus.CAPTAIN1_DEFEATED;
        if (captain2.getShip().isDestroyed()) return BattleStatus.CAPTAIN2_DEFEATED;
        else return BattleStatus.ONGOING;
    }
}