package pl.logren12.broadside.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.BattleRepository;
import pl.logren12.broadside.repository.BattleTurnRepository;
import pl.logren12.broadside.repository.CaptainRepository;

import java.util.Objects;

@Service
public class BattleService {
    private final TurnService turnService;
    private final CaptainRepository captainRepository;
    private final BattleRepository battleRepository;
    private final BattleTurnRepository battleTurnRepository;

    public BattleService(TurnService turnService, CaptainRepository captainRepository, BattleRepository battleRepository, BattleTurnRepository battleTurnRepository) {
        this.turnService = turnService;
        this.captainRepository = captainRepository;
        this.battleRepository = battleRepository;
        this.battleTurnRepository = battleTurnRepository;
    }

    @Transactional
    public Battle startABattle(String name1, String name2) {
    /* todo i need to add validation?
        How do I want creating matches to work? Do i want to make a matchmaker? Or do i want to allow any battle?
        Do i want to implement something like the board game that you may attack only targets that server finds you?
        Or do you always attack what i want you to attack?
        Do i want to add a campaign?
        Do i want to add trade, upgrading ship and things like that? Hell yes but is this a correct project (and framework) for that?
        I dont think so.
     */
        Captain c1 = captainRepository.findByName(name1);
        Captain c2 = captainRepository.findByName(name2);
        Battle battle = new Battle(c1, c2);
        battleRepository.save(battle);
        return battle;
    }

    @Transactional
    public TurnOutcome registerTurn(long battleId, Long captainId, CaptainAction action, int turnNumber) {
        // 1. validation: check if battle exist and captain with captainId participates in that battle
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Battle not found"));

        if (!battle.isCaptain1(captainId) && !battle.isCaptain2(captainId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong battle");//FIXME write better message
        }

        // then check whether turnNumber is equal to battle.getCurrentTurn and not already registered
        if (battle.getCurrentTurn() != turnNumber) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong turn");
        }
        // todo how to check whether the answer is not already submitted?
        battleTurnRepository.findByBattleIdAndTurnNumber(battleId, turnNumber);
        // Możliwe scenariusze:
        // nie ma takiego rekordu -> jesteśmy pierwsi – tworzymy!
        // Ktoś już stworzył rekord tury i jesteśmy drudzy – rekord istnieje, sprawdzamy, czy ma naszą odpowiedź.


        // 2. create a waiting object with captain, captain action, and awaiting second captain decision
        // and return status (awaiting for other player or evaluate if oponent is AI)
        Captain captain;
        Captain opponent;

        if (battle.isCaptain1(captainId)) {
            captain = battle.getCaptain1();
            opponent = battle.getCaptain2();
        } else {
            captain = battle.getCaptain2();
            opponent = battle.getCaptain1();
        }

        if (opponent.isBot()) {
            turnService.processTurn(captain, action, opponent); //todo make processTurn process by turnId?
            BattleTurn turn = battleTurnRepository.findByBattleId(battle.getId()).getLast();
            return turn.getOutcome();
        } else {
            if (battle.isCaptain1(captainId)) {
                BattleTurn turn = BattleTurn.forCaptain1(battle, turnNumber, action);
                battleTurnRepository.save(turn);
                return turn.getOutcome();

            } else {
                BattleTurn turn = BattleTurn.forCaptain2(battle, turnNumber, action);
                battleTurnRepository.save(turn);
                return turn.getOutcome();
            }
        }
    }

    public TurnOutcome sendOutcome(Long captainId, long battleId, int turnNumber) {
        // todo: Ensure validation works correctly
        if (captainId == null){
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, "Captain id is null");
        }
        // todo: Decision: Do i want to send any turn outcome or only current turn outcome?
        Battle battle = battleRepository.findById(battleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Battle not found") {
        });
        if (!Objects.equals(battle.getCaptain1().getId(), captainId) && !Objects.equals(battle.getCaptain2().getId(), captainId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong battle");
        } else {
            Captain captain = (captainId.equals(battle.getCaptain1().getId())) ? battle.getCaptain1() : battle.getCaptain2(); // todo Czy jest jakiś lepszy sposób na przeszukanie bazy szukając kapitana?
        }
        BattleTurn turn = battleTurnRepository.findByBattleId(battleId).get(turnNumber - 1); //todo super źle wygląda
        return turn.getOutcome();
    }
}