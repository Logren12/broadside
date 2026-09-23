package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="battleTurns")
public class BattleTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="battleId")
    private Battle battle;

    private int turnNumber;

    @Enumerated(EnumType.STRING)
    private CaptainAction captain1Action;

    @Enumerated(EnumType.STRING)
    private CaptainAction captain2Action;

    @Enumerated(EnumType.STRING)
    private TurnOutcome turnOutcome;

    public BattleTurn(Battle battle, int turnNumber, CaptainAction action1, CaptainAction action2, TurnOutcome outcome) {
        this.battle = battle;
        this.turnNumber = turnNumber;
        this.captain1Action = action1;
        this.captain2Action = action2;
        this.turnOutcome = outcome;
    }
}

/*
id  battleId    turnNumber  captain1Action  captain2Action  turnOutcome
1   1           1           FIRE            FIRE            ONGOING
2   1           2           FIRE            FIRE            ONGOING
3   2           1           FIRE            BOARD           CREW_FIGHT_INITIATED
4   1           3           FIRE            FIRE            ONGOING
 */
