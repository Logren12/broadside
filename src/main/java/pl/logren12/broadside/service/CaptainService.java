package pl.logren12.broadside.service;

import lombok.NonNull;
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

    public Captain create(@NonNull String name, @NonNull Faction faction, @NonNull ShipType shipType, boolean bot) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        name = name.trim();
        if (captainRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That name is already taken");
        }
        Captain newCaptain;
        if (bot) {
            newCaptain = new Captain(name, faction, new Ship(shipType));

        } else {
            newCaptain = new Captain(name, faction, new Ship(shipType), false);
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
            return List.of(captainRepository.findByName(name));
        } else if (faction != null) {
            return captainRepository.findByFaction(faction);
        } else if (bot != null && bot) {
            return captainRepository.findByBot(true);
        } else return captainRepository.findAll();
    }

    public void levelUp(Captain captain, SkillType skillType) {
        // toDo Czy tutaj nie powinienem przypadkiem otworzyć transakcji?
        Assert.notNull(captain, "Captain cannot be null");
        Assert.notNull(skillType, "Skill type cannot be null");
        captain.levelUp(skillType);
    }
}
