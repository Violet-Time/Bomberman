package com.example.bomberman.model;

import java.util.concurrent.Exchanger;

public record ExchangerGameId(Exchanger<Long> gameId, String namePlayer) {
}
