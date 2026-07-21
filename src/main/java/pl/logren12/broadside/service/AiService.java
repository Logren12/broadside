package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.CaptainRepository;

@Service
public class AiService {
    private final CaptainRepository captainRepository;

    public AiService(CaptainRepository captainRepository) {
        this.captainRepository = captainRepository;
    }

    public Captain createAiCaptain(String name, Faction faction, ShipType shiptype){
        Captain aiCaptain = new Captain(name, faction, new Ship(shiptype));
        return captainRepository.save(aiCaptain);
    }
    /**
     * Simulates decision based on aiCaptain's Ship's state.
     */
    public CaptainAction aiActionDecision(Captain aiCaptain){
        if (aiCaptain.getShip().getCanons() <=0) return CaptainAction.BOARD;
        return CaptainAction.FIRE;
        // Ai Captains never try to escape.
    }
}
