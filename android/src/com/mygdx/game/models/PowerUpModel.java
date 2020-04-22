package com.mygdx.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Game;

import java.sql.Timestamp;
import java.util.Date;

public class PowerUpModel {
    public String name;
    public Vector2 position = Game.randomPosition(150);
    private Timestamp activated = null;
    private int duration = Game.DEFAULT_POWERUP_DURATION;

    PowerUpModel(String inputName) {
        this.name = inputName;
    }

    PowerUpModel(String inputName, int duration) {
        this.name = inputName;
        this.duration = duration;
    }

    PowerUpModel(String inputName, Vector2 position) {
        this.name = inputName;
        this.position = position;
    }

    PowerUpModel(String inputName, int duration, Vector2 position) {
        this.name = inputName;
        this.duration = duration;
        this.position = position;
    }

    void activate() {
        this.activated = new Timestamp(new Date().getTime());
    }

    public boolean checkStatus() {
        return this.getTimeLeft() > 0;
    }

    long getTimeDelta() {
        return new Timestamp(new Date().getTime()).getTime() - this.activated.getTime();
    }

    public int getTimeLeft() {
        if (this.duration * 1000 - this.getTimeDelta() > 0) {
            return (int) (this.duration - (this.getTimeDelta() / 1000));
        }
        return 0;
    }
}
