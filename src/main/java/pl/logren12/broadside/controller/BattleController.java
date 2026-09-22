package pl.logren12.broadside.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.service.BattleService;

@RestController
@RequestMapping("/api/battles")
public class BattleController {
    // services
    private final BattleService battleService;
    private final BattleRepository battleRepository;

    public BattleController(BattleService battleService, BattleRepository battleRepository) {
        this.battleService = battleService;
        this.battleRepository = battleRepository;
    }

    @GetMapping("/{battleId}")
    public Battle getBattle(@PathVariable long battleId){
        return this.battleRepository.findById(battleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Battle not found"));
    }
    @PostMapping
    public Battle createBattle(@RequestParam String captain1Name, @RequestParam String captain2Name){
        // todo Check whether captains exist in database
        return battleService.startABattle(captain1Name, captain2Name);
    }
}