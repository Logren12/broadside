package pl.logren12.broadside.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.repository.BattleRoundRepository;
import pl.logren12.broadside.repository.CaptainRepository;
import pl.logren12.broadside.service.BattleService;

@RestController
public class GameController {

    private final BattleService battleService;
    private final CaptainRepository captainRepository;
    private final BattleRepository battleRepository;
    private final BattleRoundRepository  battleRoundRepository;

    public GameController(BattleService battleService, CaptainRepository captainRepository, BattleRepository battleRepository, BattleRoundRepository battleRoundRepository) {
        this.battleService = battleService;
        this.captainRepository = captainRepository;
        this.battleRepository =  battleRepository;
        this.battleRoundRepository = battleRoundRepository;
    }
    @GetMapping("/create-player")
    public String createPlayer(@RequestParam String name, @RequestParam Faction faction, @RequestParam ShipType shipType,@RequestParam(defaultValue = "true") boolean isBot) {
        Captain newCaptain = new Captain(name, faction, isBot, new Ship(shipType),0,0);
        captainRepository.save(newCaptain);
        return newCaptain.toString() + "created!";
    }

    @GetMapping("/test-turn")
    public String testTurn(@RequestParam(defaultValue = "ESCAPE") CaptainAction action) {
        //
        Ship mockShip = new Ship(ShipType.SHIP_OFTHE_LINE);
        Ship mockShip2 = new Ship(ShipType.SLOOP);
        Captain mockCaptain = new Captain("TestBot", Faction.BRITISH, mockShip);
        Captain mockCaptain2 = new Captain("TestBot2", Faction.FRENCH, mockShip2);
        CaptainAction action2 = mockCaptain2.decideAction();

        StringBuilder response = new StringBuilder();
        response.append(String.format("Captain1: %s, Captain2: %s", mockCaptain.getName(), mockCaptain2.getName()));
        response.append(String.format("Action1: %s, Action2: %s", action, action2));
        response.append("BattleRound outcome:");
        response.append(battleService.processTurn(mockCaptain, action, mockCaptain2, action2).toString());
        return response.toString();

    }
    @GetMapping("/stat-check")
    public String checkDb(@RequestParam(defaultValue = "John") String captainName, @RequestParam(defaultValue = "SLOOP") ShipType shipType) {
        Captain captain = new Captain(captainName, Faction.BRITISH, new Ship(shipType));
        return captain.toString();
    }
}