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
@Table(name = "captains")
public class Captain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Faction faction;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ship_id")
    private Ship ship;
    private int sailing = 1;
    private int leadership = 1;
    private boolean bot;

    public Captain(String name, Faction faction, Ship ship, int sailingModifier, int leadershipModifier, boolean bot) {
        if (sailingModifier < 0){
            throw new IllegalArgumentException("Modifiers cannot be negative: " + sailingModifier);
        }
        if(leadershipModifier < 0){
            throw new IllegalArgumentException("Modifiers cannot be negative: " + leadershipModifier);
        }
        this.name = name;
        this.faction = faction;
        this.ship = ship;
        this.sailing = faction.getBaseSailingSkill() + sailingModifier;
        this.leadership = faction.getBaseLeadershipSkill() + leadershipModifier;
        this.bot = bot;
    }

    /**
    * bot constructor (no modifiers)
    */
    public Captain(String name, Faction faction, Ship ship) {
        this(name, faction, ship, 0,0, true);
    }
    private List<Integer> rollTheDice(int noDice){
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
        int crewCap = this.ship.getCrew();
        int damage = 0;
        for (int die : roll){
            if ((die == 5 || die == 6) && (damage < crewCap)){
                damage++;
            }
        }
        return damage;
    }
    public void changeShip(Ship newShip){
        this.ship = newShip;
    }

    public CaptainAction decideAction() {
        if (this.getShip().getCanons() <= 0) return CaptainAction.BOARD;
        return CaptainAction.FIRE;
        }

    public void levelUp(SkillType skillType){
        switch(skillType){
            case SAILING -> sailing ++;
            case LEADERSHIP -> leadership ++;
            default -> throw new IllegalArgumentException("Unhandled skill type: " + skillType);
        }
    }

    @Override
    public String toString() {
        return String.format("Captain: %s \n Faction: %s isBot: %b %n Sailing: %d Leadership: %d, Ship %s", this.name, this.faction.toString(), this.bot, this.sailing, this.leadership, this.getShip().getType());
    }
}
