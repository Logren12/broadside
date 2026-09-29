package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "battles")
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Captain captain1;
    @ManyToOne(fetch = FetchType.LAZY)
    private Captain captain2;
    private int currentTurn;
    @Enumerated(EnumType.STRING)
    private BattleStatus status;

    /**
     * captain1 is always the captain with the lower id, so the same pair can't exist in two orders.
     */
    public Battle(@NonNull Captain c1, @NonNull Captain c2) {
        if (c1.getId() == null || c2.getId() == null) {
            throw new IllegalArgumentException("Captains must be saved first");
        }
        if (c1.getId().equals(c2.getId())) {
            throw new IllegalArgumentException("Captain can't fight with themself!");
        }
        if (c1.getId() < c2.getId()) {
            this.captain1 = c1;
            this.captain2 = c2;
        } else {
            this.captain1 = c2;
            this.captain2 = c1;
        }
        this.currentTurn = 1;
        this.status = BattleStatus.ONGOING;
    }

    public boolean isCaptain1(long captainId) {
        return captain1.getId().equals(captainId);
    }

    public boolean isCaptain2(long captainId) {
        return captain2.getId().equals(captainId);
    }
}