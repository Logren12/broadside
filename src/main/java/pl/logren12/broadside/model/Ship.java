package pl.logren12.broadside.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Ship {
    private final ShipType type;
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
        this.hull = this.type.maxHull;
        this.hold = this.type.maxHold;
        this.sails = this.type.maxSails;
        this.canons = this.type.maxCanons;
        this.crew = this.type.maxCrew;
    }
    public void receiveDamage(List<Integer> locationsHit){
        for(int location : locationsHit){
            switch (location) {
                case 1 -> {
                    if (this.hold > 0) this.hold--;
                    else this.hull--;
                }
                case 2 -> {
                    if (this.sails > 0) this.sails--;
                    else this.hull--;
                }
                case 3 -> {
                    if (this.canons > 0) this.canons--;
                    else this.hull--;
                }
                case 4 -> {
                    if (this.crew > 0) this.crew--;
                    else this.hull--;
                }
                default -> throw new IllegalArgumentException("Location out of bound (1-4): " + location);
            }
        }
    }
    public boolean isDestroyed(){
        return this.hull <= 0;
    }
}
