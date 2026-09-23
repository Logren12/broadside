package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.repository.BattleTurnRepository;
import pl.logren12.broadside.repository.CaptainRepository;

import java.util.Collections;

@Service
public class BattleService {
    private final NavalCombatService navalCombatService;
    private final AiService aiService;
    private final CaptainRepository captainRepository;
    private final BattleRepository battleRepository;
    private final BattleTurnRepository battleTurnRepository;

    public BattleService(NavalCombatService navalCombatService, AiService aiService, CaptainRepository captainRepository, BattleRepository battleRepository, BattleTurnRepository battleTurnRepository) {
        this.navalCombatService = navalCombatService;
        this.aiService = aiService;
        this.captainRepository = captainRepository;
        this.battleRepository = battleRepository;
        this.battleTurnRepository = battleTurnRepository;
    }

    /**
     * Processes a full turn between two captains.
     * <p/>
     * First checks whether both captains are able to perform turn. Then invokes NavalCombatService
     * to determine who won maneuvering phase and
     * (if needed) performs crew fight or pases on information about player's successful escape.
     */
    public Battle startABattle(String captain1Name, String captain2Name){
        // toDo i need to add validation
        Captain captain1 = captainRepository.findByName(captain1Name).getFirst();
        Captain captain2 = captainRepository.findByName(captain2Name).getFirst();
        Battle battle = new Battle(captain1, captain2);
        return battleRepository.save(battle);
    }

    public BattleStatus processTurn(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2) {
        // check whether both captains are suitable to play turn
        BattleStatus shipConditions = checkShipConditions(captain1, captain2);
        if (shipConditions != BattleStatus.ONGOING) return shipConditions;

        // check if any captain managed to escape or board enemy ship
        TurnOutcome navalPhaseOutcome = this.navalCombatService.resolveNavalPhase(captain1, action1, captain2, action2);
        switch (navalPhaseOutcome) {
            case CREW_FIGHT_INITIATED -> {
                return resolveCrewFight(captain1, captain2);
            }
            case CAPTAIN_ESCAPED -> {
                return BattleStatus.CAPTAIN_ESCAPED;
            }
            case ONGOING -> {
                // determine damage dealt by canon fire
                shipConditions = checkShipConditions(captain1, captain2);
                // if battle should end winner is repaired
                if ((shipConditions) == BattleStatus.CAPTAIN1_DEFEATED) {
                    captain2.getShip().repair();
                } else if (shipConditions == BattleStatus.CAPTAIN2_DEFEATED) {
                    captain1.getShip().repair();
                }
                // todo Log the battle to database?
                return shipConditions;
            }
            case null -> throw new IllegalArgumentException("Turn outcome cannot be null");
            default -> throw new IllegalStateException("Unexpected value: " + navalPhaseOutcome);
        }
    }

    // Player versus Bot
    public BattleStatus processTurn(Captain captain1, CaptainAction action1, Captain captain2) {
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processTurn(captain1, action1, captain2, action2);
    }

    // Bot versus Bot
    public BattleStatus processTurn(Captain captain1, Captain captain2) {
        CaptainAction action1 = this.aiService.aiActionDecision(captain1);
        CaptainAction action2 = this.aiService.aiActionDecision(captain2);
        return this.processTurn(captain1, action1, captain2, action2);
    }

    private BattleStatus resolveCrewFight(Captain captain1, Captain captain2) {
        // check whether ships are not destroyed
        BattleStatus shipCondition = checkShipConditions(captain1, captain2);
        if (shipCondition != BattleStatus.ONGOING) return shipCondition;

        BattleStatus crewState = checkCrewState(captain1, captain2);
        while (crewState == BattleStatus.ONGOING) {
            int damage1 = captain1.crewAttack();
            int damage2 = captain2.crewAttack();
            captain1.getShip().receiveDamage(Collections.nCopies(damage2, 4)); // 4 is a crew code in receiveDamage
            captain2.getShip().receiveDamage(Collections.nCopies(damage1, 4));
            crewState = checkCrewState(captain1, captain2);
        }

        switch (crewState) {
            case CAPTAIN1_DEFEATED -> takeOverShip(captain2, captain1);
            case CAPTAIN2_DEFEATED -> takeOverShip(captain1, captain2);
            default -> throw new IllegalArgumentException("Unexpected value: " + crewState);
        }
        return crewState;
    }

    private BattleStatus checkCrewState(Captain captain1, Captain captain2) {
        if (captain1.getShip().getCrew() <= 0 && captain2.getShip().getCrew() <= 0) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().getCrew() <= 0) return BattleStatus.CAPTAIN1_DEFEATED;
        if (captain2.getShip().getCrew() <= 0) return BattleStatus.CAPTAIN2_DEFEATED;
        return BattleStatus.ONGOING;
    }

    private BattleStatus checkShipConditions(Captain captain1, Captain captain2) {
        if (captain1.getShip().isDestroyed() && captain2.getShip().isDestroyed()) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().isDestroyed()) return BattleStatus.CAPTAIN1_DEFEATED;
        if (captain2.getShip().isDestroyed()) return BattleStatus.CAPTAIN2_DEFEATED;
        return BattleStatus.ONGOING;
    }
    private void takeOverShip(Captain winner, Captain loser){
        if (winner.getShip().getType().getTier() <= loser.getShip().getType().getTier()) {
            winner.changeShip(loser.getShip());
            winner.getShip().repair();
        }

    }
}