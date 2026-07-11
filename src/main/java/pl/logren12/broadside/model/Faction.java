package pl.logren12.broadside.model;

public enum Faction {
    BRITISH(4,1),
    FRENCH(1,4),
    SPANISH(2,3),
    DUTCH(3,2),
    PIRATE(1,6);

    public final int baseSailingSkill;
    public final int baseLeadershipSkill;

    Faction(int baseSailingSkill, int baseLeadershipSkill){
        this.baseSailingSkill = baseSailingSkill;
        this.baseLeadershipSkill = baseLeadershipSkill;
    }
}
