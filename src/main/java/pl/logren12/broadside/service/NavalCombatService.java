package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.CaptainAction;
import pl.logren12.broadside.model.TurnOutcome;

import java.util.Collections;
import java.util.List;

@Service
public class NavalCombatService {

    private int calculateScore(List<Integer> roll){
        int score = 0;
        for (int i : roll) {
            if( i >= 5) {
                score++;
            }
        }
        return score;
    }

    /**
     * Determines the outcome of navalPhase returning TurnOutcome object.
     * <p>Method determines the winner of the maneuvering phase and then returns either CREW_FIGHT_INITIATED, ESCAPE or
     * ONGOING. Calculates damage dealt to Ships.
     * @param captain1 First Captain participating in battle
     * @param action1 CaptainAction declared by captain1
     * @param captain2 Second Captain participating in battle
     * @param action2 CaptainAction declared by captain2
     * @return TurnOutcome object ONGOING, ESCAPE, or CREW_FIGHT_INITIATED
     */
    public TurnOutcome resolveNavalPhase(Captain captain1, CaptainAction action1, Captain captain2, CaptainAction action2){
        // 1. Determine who won maneuvering phase
        int score1 = calculateScore(captain1.maneuver());
        int score2 = calculateScore(captain2.maneuver());
        Captain winner;
        Captain loser;
        CaptainAction winningAction;
        CaptainAction losingAction;
        int loserScore;
        if (score1 > score2){
            winner = captain1;
            loser = captain2;
            winningAction = action1;
            losingAction = action2;
            loserScore = score2;
        } else if (score2 > score1) {
            winner = captain2;
            loser = captain1;
            winningAction = action2;
            losingAction = action1;
            loserScore = score1;
        }else {
            // draw
            List<Integer> damageToCaptain1 = Collections.emptyList();
            List<Integer> damageToCaptain2 = Collections.emptyList();
            if (action1 == CaptainAction.FIRE && score1 > 0){
                damageToCaptain2 = captain1.fireCanons(score1);
            }
            if (action2 == CaptainAction.FIRE && score2 > 0){
                damageToCaptain1 = captain2.fireCanons(score2);
            }
            captain1.getShip().receiveDamage(damageToCaptain1);
            captain2.getShip().receiveDamage(damageToCaptain2);
            return TurnOutcome.ONGOING; // let BattleService decide whether battle should go on
        }
        // Apply winning action
        if(winningAction == CaptainAction.ESCAPE){
            return TurnOutcome.CAPTAIN_ESCAPED;
        } else if (winningAction == CaptainAction.BOARD) {
            return TurnOutcome.CREW_FIGHT_INITIATED;
        } else if (winningAction == CaptainAction.FIRE) {
            List<Integer> damageToLoser = winner.fireCanons(winner.getShip().getCanons());
            if (losingAction == CaptainAction.FIRE && loserScore >= 1) {
                List<Integer> damageToWinner = loser.fireCanons(loserScore);
                loser.getShip().receiveDamage(damageToLoser);
                winner.getShip().receiveDamage(damageToWinner);
            } else loser.getShip().receiveDamage(damageToLoser);
            return TurnOutcome.ONGOING;
        }
        return TurnOutcome.ONGOING;
    }
}
