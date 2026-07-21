package pl.logren12.broadside.model;

public enum ShipType {
    SLOOP(2,1,2,1,2,1),
    FLYUT(2,2,1,2,2,2),
    FRIGATE(3,3,3,3,3,3),
    GALLEON(4,5,3,3,4,4),
    SHIP_OF_THE_LINE(5,5,5,5,5,5);

    public final int maxHull;
    public final int maxHold;
    public final int maxSails;
    public final int maxCanons;
    public final int maxCrew;
    public final int tier;

    ShipType(int maxHull, int maxHold, int maxSails, int maxCanons, int maxCrew, int tier) {
        this.maxHull = maxHull;
        this.maxHold = maxHold;
        this.maxSails = maxSails;
        this.maxCanons = maxCanons;
        this.maxCrew = maxCrew;
        this.tier = tier;
    }
}