package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
@NoArgsConstructor
public class Captain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Faction faction;

    private boolean isBot;
    private int sailing;
    private int leadership;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipId")
    private Ship ship;


    public Captain(String name, Faction faction, boolean isBot, Ship ship, int sailingModifier, int leadershipModifier) {
        this.name = name;
        this.faction = faction;
        this.isBot = isBot;
        this.ship = ship;
        this.sailing = faction.baseSailingSkill + sailingModifier;
        this.leadership = faction.baseLeadershipSkill + leadershipModifier;
    }
    // bot constructor
    public Captain(String name, Faction faction, Ship ship) {
        this(name, faction,true, ship, 0,0);
    }
    public List<Integer> rollTheDice(int noDice){
        List<Integer> diceRoll = new ArrayList<>(noDice);
        for(int i = 0; i < noDice; i++){
            int roll = ThreadLocalRandom.current().nextInt(1, 7);
            diceRoll.add(roll);
        }
        return diceRoll;
    }
    public List<Integer> maneuver(){
        if (this.ship.getSails() <= 0) return rollTheDice(1);

        return rollTheDice(this.sailing);
    }
    public List<Integer> fireCanons(int canonsToFire){
        int availableCanons = this.ship.getCanons();
        if (availableCanons == 0) return Collections.emptyList();

        List<Integer> roll = this.rollTheDice(Math.min(availableCanons, canonsToFire));
        List<Integer> locationsHit = new ArrayList<>(roll.size());
        for (int die : roll){
            if (die >= 5){
                locationsHit.add(ThreadLocalRandom.current().nextInt(1, 5));
            }else locationsHit.add(die);
        }
        return locationsHit;
    }

    public int crewAttack(){
        List<Integer> roll = this.rollTheDice(this.leadership);
        int damage = 0;
        for (int die : roll){
            if ((die == 5 || die == 6) && (damage < this.ship.getCrew())){
                damage++;
            }
        }
        return damage;
    }
    public void changeShip(Ship newShip){
        if (newShip.getType().tier > this.ship.getType().tier) {
            this.ship = newShip;
        }
    }
    public CaptainAction decideAction() {
        if (this.getShip().getCanons() <= 0) return CaptainAction.BOARD;
        return CaptainAction.FIRE;
        }
    @Override
    public String toString() {
        return String.format("Captain: %s \n Faction: %s isBot: %b %n Sailing: %d Leadership: %d, Ship %s", this.name, this.faction.toString(), this.isBot, this.sailing, this.leadership, this.getShip().getType());
    }
}
