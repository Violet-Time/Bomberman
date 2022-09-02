package com.example.bomberman.model;

import java.util.List;
import java.util.Map;

public record GameIdName(long gameId, String playerName) {
    public GameIdName(Map<String, List<String>> query) {
        this(Long.parseLong(query.get("gameId").get(0)), query.get("name").get(0));
    }
}
