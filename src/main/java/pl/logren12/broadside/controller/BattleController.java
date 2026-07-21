package pl.logren12.broadside.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.service.BattleService;

@RestController
public class BattleController {
    // services
    private final BattleService battleService;
    private final BattleRepository battleRepository;

    public BattleController(BattleService battleService, BattleRepository battleRepository) {
        this.battleService = battleService;
        this.battleRepository = battleRepository;
    }

    @GetMapping("/test-turn")
    public String testTurn(@RequestParam(defaultValue = "ESCAPE") CaptainAction action) {
        //
        Ship mockShip = new Ship(ShipType.SHIP_OF_THE_LINE);
        Ship mockShip2 = new Ship(ShipType.SLOOP);
        Captain mockCaptain = new Captain("TestBot", Faction.BRITISH, mockShip);
        Captain mockCaptain2 = new Captain("TestBot2", Faction.FRENCH, mockShip2);
        CaptainAction action2 = mockCaptain2.decideAction();

        StringBuilder response = new StringBuilder();
        response.append(String.format("Captain1: %s, Captain2: %s", mockCaptain.getName(), mockCaptain2.getName()));
        response.append(String.format("Action1: %s, Action2: %s", action, action2));
        response.append("BattleRound outcome:");
        response.append(battleService.processRound(mockCaptain, action, mockCaptain2, action2).toString());
        return response.toString();
    }
    @GetMapping("/get-battles")
    public Battle playRound(@RequestParam String battleId){
        return this.battleRepository.findAll().getFirst();
    }
    @PostMapping("/create-battle")
    public Battle createBattle(@RequestParam String captain1Name, @RequestParam String captain2Name){
        return battleService.startABattle(captain1Name, captain2Name);
    }
}