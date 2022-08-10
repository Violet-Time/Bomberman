package com.example.bomberman.controller;

import com.example.bomberman.service.ConnectionProducer;
import com.example.bomberman.service.MatchMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matchmaker")
public class MatchMakerController {

    private static final Logger log = LoggerFactory.getLogger(MatchMakerController.class);

    private ConnectionProducer connectionProducer;

    public MatchMakerController(ConnectionProducer connectionProducer) {
        this.connectionProducer = connectionProducer;
    }

    @PostMapping(path = "/join"/*,
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE*/)
    public String join(@RequestParam String name) {
        //return connectionProducer.produce(name);

        log.info("{} join", name);
        //gameService.
        return "" + connectionProducer.produce(name);
    }
}