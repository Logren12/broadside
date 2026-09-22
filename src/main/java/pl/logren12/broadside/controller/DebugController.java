package pl.logren12.broadside.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.service.BattleService;

public class DebugController {
    // services
    private final BattleService battleService;

    public DebugController(BattleService battleService) {
        this.battleService = battleService;
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
}
