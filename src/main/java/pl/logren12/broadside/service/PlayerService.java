package pl.logren12.broadside.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.logren12.broadside.model.*;
import pl.logren12.broadside.repository.CaptainRepository;

@Service
public class PlayerService {
    private final CaptainRepository captainRepository;

    public PlayerService(CaptainRepository captainRepository) {
        this.captainRepository = captainRepository;
    }


}
