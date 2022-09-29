package com.example.bomberman.service.tick;


import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Ticker implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Ticker.class);
    public static final int FPS = 60;
    public static final long FRAME_TIME = 1000 / FPS;
    private final Set<Ticking> ticking = new LinkedHashSet<>();
    private long tickNumber = 0;

    private long elapsed = FRAME_TIME;

    public void gameLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.currentTimeMillis();
            act(elapsed);
            elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                log.info("All tick finish at {} ms", elapsed);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(FRAME_TIME - elapsed));
                elapsed = FRAME_TIME;
            } else {
                log.warn("tick lag {} ms", elapsed - FRAME_TIME);
            }
            log.info("{}: tick ", tickNumber);
            tickNumber++;
        }
    }

    public void registerTicking(Ticking ticking) {
        this.ticking.add(ticking);
    }

    public void unregisterTicking(Ticking ticking) {
        this.ticking.remove(ticking);
    }

    private void act(long elapsed) {
        ticking.forEach(ticking -> ticking.tick(elapsed));
    }

    public long getTickNumber() {
        return tickNumber;
    }

    @Override
    public void run() {
        log.info("Started");
        gameLoop();
        log.info("Ended");
    }
}
