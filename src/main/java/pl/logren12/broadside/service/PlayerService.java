package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.logren12.broadside.model.*;
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

    public void levelUpCaptain(Captain captain, SkillType skillType){
        // toDo Czy tutaj nie powinienem przypadkiem otworzyć transakcji?
        Assert.notNull(captain, "Captain cannot be null");
        Assert.notNull(skillType, "Skill type cannot be null");
        captain.levelUp(skillType);
    }
}
