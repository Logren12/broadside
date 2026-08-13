package pl.logren12.broadside.model;

import lombok.Getter;

@Getter
public enum ShipType {
    SLOOP(2,1,2,1,2,1),
    FLUYT(2,2,1,2,2,2),
    FRIGATE(3,3,3,3,3,3),
    GALLEON(4,5,3,3,4,4),
    SHIP_OF_THE_LINE(5,5,5,5,5,5);

    private final int maxHull;
    private final int maxHold;
    private final int maxSails;
    private final int maxCanons;
    private final int maxCrew;
    private final int tier;

    ShipType(int maxHull, int maxHold, int maxSails, int maxCanons, int maxCrew, int tier) {
        this.maxHull = maxHull;
        this.maxHold = maxHold;
        this.maxSails = maxSails;
        this.maxCanons = maxCanons;
        this.maxCrew = maxCrew;
        this.tier = tier;
    }
}