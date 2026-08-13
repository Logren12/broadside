package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name="ships")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ShipType type;
    private int hull;
    private int hold;
    private int sails;
    private int canons;
    private int crew;

    public Ship(ShipType type){
        this.type = type;
        repair();
    }
    public void repair(){
        this.hull = this.type.getMaxHull();
        this.hold = this.type.getMaxHold();
        this.sails = this.type.getMaxSails();
        this.canons = this.type.getMaxCanons();
        this.crew = this.type.getMaxCrew();
    }

    public void receiveDamage(List<Integer> locationsHit){
        for(int location : locationsHit){
            if(isDestroyed()) break;

            switch (location) {
                case 1 -> {
                    if (hold > 0) hold--;
                    else hull--;
                }
                case 2 -> {
                    if (sails > 0) sails--;
                    else hull--;
                }
                case 3 -> {
                    if (canons > 0) canons--;
                    else hull--;
                }
                case 4 -> {
                    if (crew > 0) crew--;
                    else hull--;
                }
                default -> throw new IllegalArgumentException("Location out of bound (1-4): " + location);
            }
        }
    }

    public boolean isDestroyed(){
        return this.hull <= 0;
    }
}
