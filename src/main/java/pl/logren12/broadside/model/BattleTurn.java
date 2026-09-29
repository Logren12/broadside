package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "battle_turns", uniqueConstraints = @UniqueConstraint(columnNames = {"battle_id", "turn_number"}))

public class BattleTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "battle_id", nullable = false)
    private Battle battle;

    private int turnNumber;

    @Enumerated(EnumType.STRING)
    private CaptainAction captain1Action;

    @Enumerated(EnumType.STRING)
    private CaptainAction captain2Action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurnOutcome outcome;

    public BattleTurn(@NonNull Battle battle, int turnNumber, @NonNull CaptainAction action1, @NonNull CaptainAction action2, @NonNull TurnOutcome outcome) {
        requireFinished(outcome);
        this.battle = battle;
        this.turnNumber = turnNumber;
        this.captain1Action = action1;
        this.captain2Action = action2;
        this.outcome = outcome;
    }

    public static BattleTurn forCaptain1(@NonNull Battle battle, int turnNumber, @NonNull CaptainAction action) {
        BattleTurn turn = new BattleTurn();
        turn.battle = battle;
        turn.turnNumber = turnNumber;
        turn.captain1Action = action;
        turn.outcome = TurnOutcome.UNFINISHED;
        return turn;
    }

    public static BattleTurn forCaptain2(@NonNull Battle battle, int turnNumber, @NonNull CaptainAction action) {
        BattleTurn turn = new BattleTurn();
        turn.battle = battle;
        turn.turnNumber = turnNumber;
        turn.captain2Action = action;
        turn.outcome = TurnOutcome.UNFINISHED;
        return turn;
    }

    private static void requireFinished(TurnOutcome outcome) {
        if (outcome == TurnOutcome.UNFINISHED) {
            throw new IllegalArgumentException("Outcome can't be UNFINISHED here");
        }
    }

    public void completeWithCaptain1Action(@NonNull CaptainAction action, @NonNull TurnOutcome outcome) {
        requireFinished(outcome);
        if (this.captain1Action != null) {
            throw new IllegalStateException("Already submitted");
        }
        this.captain1Action = action;
        this.outcome = outcome;
    }

    public void completeWithCaptain2Action(@NonNull CaptainAction action, @NonNull TurnOutcome outcome) {
        requireFinished(outcome);
        if (this.captain2Action != null) {
            throw new IllegalStateException("Already submitted");
        }
        this.captain2Action = action;
        this.outcome = outcome;
    }

}