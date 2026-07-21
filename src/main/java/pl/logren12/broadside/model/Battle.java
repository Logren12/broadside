package pl.logren12.broadside.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Battle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Captain captain1; // powiązane z tabelą kapitanów. Odniesienie trzymamy tutaj. Czyli many to one?
    @ManyToOne //todo może bez fetch lepsze/gorsze?
    private Captain captain2;
    private int currentRound;
    @Enumerated(EnumType.STRING)
    private BattleStatus status;

    public Battle(Captain c1, Captain c2){
        this.captain1 = c1;
        this.captain2 = c2;
        this.currentRound = 0;
        this.status = BattleStatus.ONGOING;
    }
}
/*
id  captain1Id  captain2Id  currentRound                                status
1   1           2           13                                          CAPTAIN2DEFEATED
3   1           3           3                                           ONGOING
* */