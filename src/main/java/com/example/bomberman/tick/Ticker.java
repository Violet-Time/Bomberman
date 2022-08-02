package com.example.bomberman.tick;


import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Ticker implements Runnable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Ticker.class);
    public static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private Set<Tickable> tickables = new ConcurrentSkipListSet<>();
    private long tickNumber = 0;

    public void gameLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            long started = System.currentTimeMillis();
            act(FRAME_TIME);
            long elapsed = System.currentTimeMillis() - started;
            if (elapsed < FRAME_TIME) {
                log.info("All tick finish at {} ms", elapsed);
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(FRAME_TIME - elapsed));
            } else {
                log.warn("tick lag {} ms", elapsed - FRAME_TIME);
            }
            log.info("{}: tick ", tickNumber);
            tickNumber++;
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    public void registerTickable(Tickable tickable) {
        tickables.add(tickable);
    }

    public void unregisterTickable(Tickable tickable) {
        tickables.remove(tickable);
    }

    private void act(long elapsed) {
        tickables.forEach(tickable -> tickable.tick(elapsed));
    }

    public long getTickNumber() {
        return tickNumber;
    }

    @Override
    public void run() {
        log.info("Started");
        gameLoop();
    }
}
