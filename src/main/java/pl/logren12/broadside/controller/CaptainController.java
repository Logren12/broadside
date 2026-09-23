package pl.logren12.broadside.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.service.CaptainService;

import java.util.List;
@Validated
@RestController
@RequestMapping("/api/captains")
public class CaptainController {

    private final CaptainService captainService;

    public CaptainController(CaptainService captainService) {
        this.captainService = captainService;
    }

    @PostMapping
    public Captain createCaptain(@RequestParam @NotBlank(message = "Name must not be blank") @Size(max=25) String name,
                                 @RequestParam Faction faction,
                                 @RequestParam ShipType shipType,
                                 @RequestParam(defaultValue = "true") boolean bot) {
        //todo Add/implement modifiers
        if (bot){
            return this.captainService.create(name, faction, shipType, 0,0,true);
        }
        else{
            return this.captainService.create(name, faction, shipType,0,0, false);
        }
    }
    @GetMapping
    public List<Captain> searchCaptains(
            @RequestParam(required = false) @Size(max = 25) String name,
            @RequestParam(required = false) Faction faction,
            @RequestParam(required = false) Boolean bot) //Boolean and not boolean to have 3 states to make it possible to search for non bots! Diffrentiate omitting filter and speficying false
    {
        return captainService.search(name, faction, bot);
    }

    @GetMapping("/{captainId}")
    public Captain getCaptain(@PathVariable long captainId) {
        return captainService.getById(captainId);
    }
}