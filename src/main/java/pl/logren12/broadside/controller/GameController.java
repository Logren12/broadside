package pl.logren12.broadside.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.CaptainRepository;
import pl.logren12.broadside.service.AiService;
import pl.logren12.broadside.service.PlayerService;

import java.util.List;

@RestController
public class GameController {

    private final PlayerService playerService;
    private final AiService aiService;
    private final CaptainRepository captainRepository;

    public GameController(PlayerService playerService, AiService aiService, CaptainRepository captainRepository) {
        this.playerService = playerService;
        this.aiService = aiService;
        this.captainRepository = captainRepository;
    }

    @PostMapping("/create-captain")
    public Captain createCaptain(@RequestParam String name,
                                 @RequestParam Faction faction,
                                 @RequestParam ShipType shipType,
                                 @RequestParam(defaultValue = "true") boolean isBot) {
        if (isBot){
            return this.aiService.createAiCaptain(name, faction, shipType);
        }
        else{
            return this.playerService.createPlayerCaptain(name, faction, shipType,0,0);
        }
    }

    @GetMapping("/stat-check")
    public ResponseEntity<List<Captain>> statCheck( //todo Implement search logic for multiple arguments
            @RequestParam(required = false) String captainName,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Faction faction){

        if (captainName != null && !captainName.isBlank()) {
            return ResponseEntity.ok(captainRepository.findByName(captainName));
        }else if (id != null) {
            return captainRepository.findById(id)
                    .map(List::of)
                    .map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
        }else if (faction != null){
            return ResponseEntity.ok(captainRepository.findByFaction(faction));
        }
        return ResponseEntity.badRequest().build();
    }
}