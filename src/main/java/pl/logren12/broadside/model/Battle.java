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
    private TurnOutcome status;

    public Battle(Captain captain1, Captain captain2) {
        this.captain1 = captain1;
        this.captain2 = captain2;
    }
}
// niby jedna bitwa może mieć wiele rund, ale currentRound może być tylko jedno. To odniesienie do tabeli z rundami one to many?
// moment currentRound to nie jest chyba żadne odniesienie tylko ilość już rozegranych rund? hmmm

/*
jeden kapitan wiele bitew, ale jedna bitwa może mieć tylko jednego kapitana.
z perspektywy kapitana one to many
z perspektywy bitwy: many to one?

id  captain1Id  captain2Id  currentRound                                status
1   1           2           13                                          CAPTAIN2DEFEATED
3   1           2           15(odniesienie do JEDNEJ rundy?)            ONGOING

* */