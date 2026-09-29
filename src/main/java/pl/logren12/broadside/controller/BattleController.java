package pl.logren12.broadside.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.service.BattleService;

@RestController
@Validated
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
    public Battle getBattle(@PathVariable long battleId) {
        return this.battleRepository.findById(battleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Battle not found"));
    }

    @PostMapping
    public Battle createBattle(@RequestParam String captain1Name, @RequestParam String captain2Name) {
        // todo Check whether captains exist in database
        return battleService.startABattle(captain1Name, captain2Name);
    }

    @PostMapping("/{battleId}/turns")
    public TurnOutcome sendAction(@PathVariable @Positive long battleId, @RequestParam Long captainId, @RequestParam CaptainAction action, @RequestParam @Positive int turnNumber) {
        return battleService.registerTurn(battleId, captainId, action, turnNumber);
    }

    @GetMapping("/{battleId}/turns/{turnNumber}")
    public TurnOutcome geTurnOutcome(@RequestParam @NotNull Long captainId, @PathVariable @Positive long battleId, @PathVariable @Positive int turnNumber) {
        //todo and validation and verification
        return battleService.sendOutcome(captainId, battleId, turnNumber);
    }
}
//todo Odpowiedzieć na pytania:
/* Pytania:
    1. Czy dodawanie @NotNull po @Positive ma sens?
    2. Czy @NotNull i Long captainId ma sens? czy nie lepiej long captainId (long nie może być null)?
* */