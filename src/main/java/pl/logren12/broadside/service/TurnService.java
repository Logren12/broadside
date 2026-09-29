package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.logren12.broadside.model.BattleStatus;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.CaptainAction;

import java.util.Collections;
import java.util.List;

@Service
public class TurnService {
    /**todo: update
     * Determines the outcome of turn, returning TurnOutcome object.
     * <p>Determines the winner of the maneuvering phase and then returns either CREW_FIGHT_INITIATED, ESCAPE or
     * ONGOING. Calculates and applies damage dealt to Ships.
     *
     * @param captain1 First Captain participating in battle
     * @param action1  CaptainAction declared by captain1
     * @param captain2 Second Captain participating in battle
     * @param action2  CaptainAction declared by captain2
     * @return BattleStatus object ONGOING, ESCAPE, or CREW_FIGHT_INITIATED
     */
    public BattleStatus processTurn(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2) {
        // todo Add validation
        Assert.notNull(captain1, "Captain 1 cannot be null");
        Assert.notNull(captain2, "Captain 2 cannot be null");
        Assert.notNull(action1, "Action 1 cannot be null");
        Assert.notNull(action2, "Action 2 cannot be null");
        // FIXME now captain1 and 2 and their actions depend on the id of them (lower id is cap1)

        BattleStatus shipConditions = checkShipConditions(captain1, captain2);
        if (shipConditions != BattleStatus.ONGOING) return shipConditions;

        // 1. Determine turn winner
        int score1 = calculateScore(captain1.maneuver());
        int score2 = calculateScore(captain2.maneuver());

        // 2. Handle draw
        if (score1 == score2) {
            return handleDraw(captain1, action1, captain2, action2, score1);
        }

        // 3. Appoint roles
        boolean captain1Won = score1 > score2;
        Captain winner = captain1Won ? captain1 : captain2;
        Captain loser = captain1Won ? captain2 : captain1;
        CaptainAction winningAction = captain1Won ? action1 : action2;
        CaptainAction losingAction = captain1Won ? action2 : action1;
        int loserScore = Math.min(score1, score2);

        // 4. Apply winning action
        switch (winningAction) {
            case ESCAPE -> {
                return BattleStatus.CAPTAIN_ESCAPED;
            }
            case BOARD -> {
                return resolveCrewFight(captain1, captain2);
            }
            case FIRE -> {
                List<Integer> damageToLoser = winner.fireCanons(winner.getShip().getCanons());
                List<Integer> damageToWinner = Collections.emptyList();
                if (losingAction == CaptainAction.FIRE && loserScore >= 1) {
                    damageToWinner = loser.fireCanons(loserScore);
                }
                loser.getShip().receiveDamage(damageToLoser);
                winner.getShip().receiveDamage(damageToWinner);
                return checkShipConditions(captain1, captain2);
            }
            case null -> throw new IllegalArgumentException("Captain action cannot be null");
            default -> throw new IllegalArgumentException("Unhandled action: " + winningAction);
        }
    }
    // Player versus Bot
    public BattleStatus processTurn(Captain captain1, CaptainAction action1, Captain captain2) {
        CaptainAction action2 = captain2.decideAction();
        return this.processTurn(captain1, action1, captain2, action2);
    }

    // Bot versus Bot
    public BattleStatus processTurn(Captain captain1, Captain captain2) {
        CaptainAction action1 = captain1.decideAction();
        CaptainAction action2 = captain2.decideAction();
        return this.processTurn(captain1, action1, captain2, action2);
    }
    private BattleStatus handleDraw(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2, int score) {
        if (score == 0) return BattleStatus.ONGOING;
        List<Integer> damageToCaptain1 = Collections.emptyList();
        List<Integer> damageToCaptain2 = Collections.emptyList();

        if (action1 == CaptainAction.FIRE) {
            damageToCaptain2 = captain1.fireCanons(score);
        }
        if (action2 == CaptainAction.FIRE) {
            damageToCaptain1 = captain2.fireCanons(score);
        }
        captain1.getShip().receiveDamage(damageToCaptain1);
        captain2.getShip().receiveDamage(damageToCaptain2);

        return checkShipConditions(captain1, captain2);
    }

    private int calculateScore(List<Integer> roll) {
        int score = 0;
        for (int i : roll) {
            if (i >= 5) {
                score++;
            }
        }
        return score;
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
        if (captain1.getShip().crewIsDead() && captain2.getShip().crewIsDead()) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().crewIsDead()) return BattleStatus.CAPTAIN1_DEFEATED;
        if (captain2.getShip().crewIsDead()) return BattleStatus.CAPTAIN2_DEFEATED;
        return BattleStatus.ONGOING;
    }

    private BattleStatus checkShipConditions(Captain captain1, Captain captain2) {
        if (captain1.getShip().isDestroyed() && captain2.getShip().isDestroyed()) return BattleStatus.BOTH_DESTROYED;
        if (captain1.getShip().isDestroyed()) {
            captain2.getShip().repair();
            return BattleStatus.CAPTAIN1_DEFEATED;
        }
        if (captain2.getShip().isDestroyed()) {
            captain1.getShip().repair();
            return BattleStatus.CAPTAIN2_DEFEATED;
        }
        return BattleStatus.ONGOING;
    }

    private void takeOverShip(Captain winner, Captain loser) {
        if (winner.getShip().getType().getTier() <= loser.getShip().getType().getTier()) {
            winner.changeShip(loser.getShip());
            winner.getShip().repair();
        } else winner.getShip().repair();
    }
}
