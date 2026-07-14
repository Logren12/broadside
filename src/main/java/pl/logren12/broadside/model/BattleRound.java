package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BattleRound {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int roundNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="battleId")
    private Battle battle;
    @Enumerated(EnumType.STRING)
    private CaptainAction captain1Action;
    @Enumerated(EnumType.STRING)
    private CaptainAction captain2Action;
    @Enumerated(EnumType.STRING)
    private TurnOutcome turnOutcome;
}

/*
id  roundNumber battleId    captain1Action  captain2Action  turnOutcome
1   1           1           FIRE            FIRE            ONGOING
2   2           1           FIRE            FIRE            ONGOING
3   1           2           FIRE            BOARD           CREW_FIGHT_INITIATED
4   3           1           FIRE            FIRE            ONGOING
 */
