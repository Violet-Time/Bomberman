package com.example.bomberman.controller;

import com.example.bomberman.service.ConnectionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matchmaker")
public class MatchMakerController {

    private static final Logger log = LoggerFactory.getLogger(MatchMakerController.class);

    private final ConnectionProducer connectionProducer;

    public MatchMakerController(ConnectionProducer connectionProducer) {
        this.connectionProducer = connectionProducer;
    }

    @PostMapping(path = "/join")
    public String join(@RequestParam String name) {
        log.info("{} join", name);
        return "" + connectionProducer.produce(name);
    }
}
