package com.mygdx.game.controllers;

import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.PlayerModel;

public abstract class Controller {
    PlayerModel player;
    BoardModel board;

    Controller(PlayerModel player, BoardModel board) {
        this.player = player;
        this.board = board;
    }


    public abstract void update(float dt);
}
