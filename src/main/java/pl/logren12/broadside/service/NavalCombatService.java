package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.CaptainAction;
import pl.logren12.broadside.model.TurnOutcome;
import java.util.Collections;
import java.util.List;

@Service
public class NavalCombatService {
    /**
     * Determines the outcome of navalPhase returning TurnOutcome object.
     * <p>Determines the winner of the maneuvering phase and then returns either CREW_FIGHT_INITIATED, ESCAPE or
     * ONGOING. Calculates and applies damage dealt to Ships.
     * @param captain1 First Captain participating in battle
     * @param action1 CaptainAction declared by captain1
     * @param captain2 Second Captain participating in battle
     * @param action2 CaptainAction declared by captain2
     * @return TurnOutcome object ONGOING, ESCAPE, or CREW_FIGHT_INITIATED
     */
    public TurnOutcome resolveNavalPhase(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2){
        Assert.notNull(captain1, "Captain 1 cannot be null");
        Assert.notNull(captain2, "Captain 2 cannot be null");
        Assert.notNull(action1, "Action 1 cannot be null");
        Assert.notNull(action2, "Action 2 cannot be null");

        // 1. Determine turn winner
        int score1 = calculateScore(captain1.maneuver());
        int score2 = calculateScore(captain2.maneuver());

        // 2. Handle draw
        if (score1 == score2) {
            handleDraw(captain1, action1, captain2, action2, score1);
            return TurnOutcome.ONGOING;
        }

        // 3. Appoint roles
        boolean captain1Won = score1 > score2;
        Captain winner = captain1Won ? captain1 : captain2;
        Captain loser = captain1Won ? captain2 : captain1;
        CaptainAction winningAction = captain1Won ? action1 :action2;
        CaptainAction losingAction = captain1Won ? action2 :action1;
        int loserScore = Math.min(score1, score2);

        // 4. Apply winning action
        switch (winningAction){
            case ESCAPE -> {
                return TurnOutcome.CAPTAIN_ESCAPED;
            }
            case BOARD -> {
                return TurnOutcome.CREW_FIGHT_INITIATED;
            }
            case FIRE -> {
                List<Integer> damageToLoser = winner.fireCanons(winner.getShip().getCanons());
                List<Integer> damageToWinner = Collections.emptyList();
                if (losingAction == CaptainAction.FIRE && loserScore >= 1) {
                    damageToWinner = loser.fireCanons(loserScore);
                }
                loser.getShip().receiveDamage(damageToLoser);
                winner.getShip().receiveDamage(damageToWinner);
                return TurnOutcome.ONGOING;
            }
            case null ->
                    throw new  IllegalArgumentException("Captain action cannot be null");
            default ->
                    throw new IllegalArgumentException("Unhandled action: " + winningAction);
        }
    }

    private void handleDraw(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2, int score) {
        if (score == 0) return;
        List<Integer> damageToCaptain1 = Collections.emptyList();
        List<Integer> damageToCaptain2 = Collections.emptyList();

        if (action1 == CaptainAction.FIRE){
            damageToCaptain2 = captain1.fireCanons(score);
        }
        if (action2 == CaptainAction.FIRE){
            damageToCaptain1 = captain2.fireCanons(score);
        }
        captain1.getShip().receiveDamage(damageToCaptain1);
        captain2.getShip().receiveDamage(damageToCaptain2);
    }

    private int calculateScore(List<Integer> roll){
        int score = 0;
        for (int i : roll) {
            if( i >= 5) {
                score++;
            }
        }
        return score;
    }
}
