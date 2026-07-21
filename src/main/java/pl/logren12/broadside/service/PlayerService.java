package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import pl.logren12.broadside.model.Captain;
import pl.logren12.broadside.model.Faction;
import pl.logren12.broadside.model.Ship;
import pl.logren12.broadside.model.ShipType;
import pl.logren12.broadside.repository.CaptainRepository;

@Service
public class PlayerService {
    private final CaptainRepository captainRepository;

    public PlayerService(CaptainRepository captainRepository) {
        this.captainRepository = captainRepository;
    }
    public Captain createPlayerCaptain(String name, Faction faction, ShipType shipType, int sailingModifier, int leadershipModifier){
        Captain newCaptain = new Captain(name, faction, new Ship(shipType), sailingModifier, leadershipModifier, false);
        return this.captainRepository.save(newCaptain);
    }
}
