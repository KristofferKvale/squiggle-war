package com.mygdx.game.controllers;

import com.mygdx.game.models.PlayerModel;

public abstract class Controller {
    PlayerModel player;

    Controller(PlayerModel player) {
        this.player = player;
    }


    public abstract void update(int y);
}
