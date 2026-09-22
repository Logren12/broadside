package pl.logren12.broadside.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.CaptainRepository;
import pl.logren12.broadside.service.AiService;
import pl.logren12.broadside.service.PlayerService;

import java.util.List;
@Validated
@RestController
@RequestMapping("/api/captains")
public class CaptainController {

    private final PlayerService playerService;
    private final AiService aiService;
    private final CaptainRepository captainRepository;

    public CaptainController(PlayerService playerService, AiService aiService, CaptainRepository captainRepository) {
        this.playerService = playerService;
        this.aiService = aiService;
        this.captainRepository = captainRepository;
    }

    @PostMapping
    public Captain createCaptain(@RequestParam @NotBlank(message = "Name must not be blank") @Size(max=25) String name,
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
    @GetMapping
    public ResponseEntity<List<Captain>> searchCaptains(
            @RequestParam(required = false) String captainName,
            @RequestParam(required = false) Faction faction) {
        //todo Implement search logic for multiple arguments
        if (captainName != null && !captainName.isBlank()) {
            return ResponseEntity.ok(captainRepository.findByName(captainName));
        } else if (faction != null) {
            return ResponseEntity.ok(captainRepository.findByFaction(faction));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{captainId}")
    public ResponseEntity<Captain> getCaptain(@PathVariable Long captainId) {
        return captainRepository.findById(captainId).
                map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}