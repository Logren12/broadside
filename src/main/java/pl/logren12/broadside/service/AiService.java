package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.CaptainAction;

@Service
public class AiService {
    /**
     * Simulates decision based on aiCaptain's Ship's state.
     * @param aiCaptain Captain whose decision is being simulated
     * @return CaptainAction BOARD or FIRE
     */
    public CaptainAction aiActionDecision(Captain aiCaptain){
        if (aiCaptain.getShip().getCanons() <=0) return CaptainAction.BOARD;
        return CaptainAction.FIRE;
        // Ai Captains never try to escape.
    }
}
