package com.example.bomberman.model.game.staticObj.tile;

import com.example.bomberman.model.game.Size;
import com.example.bomberman.model.game.staticObj.StaticGameObject;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Tile extends StaticGameObject {
    @JsonIgnore
    private static final Size size = new Size(32, 32);
    public Tile(GameEntityRepository gameEntityRepository, String type) {
        super(gameEntityRepository, size, type);
    }

    public void remove() {
        gameEntityRepository.removeTile(this);
        /*gGameEngine.stage.removeChild(this.bmp);
        for (var i = 0; i < gGameEngine.tiles.length; i++) {
            var tile = gGameEngine.tiles[i];
            if (this == tile) {
                gGameEngine.tiles.splice(i, 1);
            }
        }*/
    }

}
