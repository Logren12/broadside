package pl.logren12.broadside.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.CaptainRepository;

import java.util.List;

@Service
public class CaptainService {

    private final CaptainRepository captainRepository;

    public CaptainService(CaptainRepository captainRepository) {
        this.captainRepository = captainRepository;
    }

    public Captain create(String name, Faction faction, ShipType shipType, int sailingModifier, int leadershipModifier, boolean bot){
        // todo Add a check for already taken name
        Captain newCaptain;
        if (bot){
            newCaptain = new Captain(name, faction, new Ship(shipType));

        }else {
            newCaptain = new Captain(name, faction, new Ship(shipType), sailingModifier, leadershipModifier, bot);
        }
        return this.captainRepository.save(newCaptain);
    }

    public Captain getById(long id) {
        return captainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Captain not found"));
    }

    public List<Captain> search(String name, Faction faction, Boolean bot) {
        //todo Implement search logic for multiple arguments (change them to proper filters)
        if (name != null && !name.isBlank()) {
            return captainRepository.findByName(name);
        } else if (faction != null) {
            return captainRepository.findByFaction(faction);
        } else if (bot != null && bot) {
            return captainRepository.findByBot(true);
        }
        else return captainRepository.findAll();
    }

    public void levelUp(Captain captain, SkillType skillType){
        // toDo Czy tutaj nie powinienem przypadkiem otworzyć transakcji?
        Assert.notNull(captain, "Captain cannot be null");
        Assert.notNull(skillType, "Skill type cannot be null");
        captain.levelUp(skillType);
    }
}
