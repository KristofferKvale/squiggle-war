package com.mygdx.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Game;

import java.sql.Timestamp;
import java.util.Date;

public class PowerUpModel {
    public String name;
    public Texture texture;
    public Vector2 position = Game.randomPosition(150);
    private Timestamp activated = null;
    private Boolean active = true;
    private int duration = 10;

    public PowerUpModel(String inputName) {
        this.name = inputName;
        this.texture = new Texture(inputName + ".png");
    }

    public PowerUpModel(String inputName, int duration) {
        this.name = inputName;
        this.texture = new Texture(inputName + ".png");
        this.duration = duration;
    }

    public PowerUpModel(String inputName, Vector2 position) {
        this.name = inputName;
        this.texture = new Texture(inputName + ".png");
        this.position = position;
    }

    public PowerUpModel(String inputName, int duration, Vector2 position) {
        this.name = inputName;
        this.texture = new Texture(inputName + ".png");
        this.duration = duration;
        this.position = position;
    }

    public void activate() {
        this.activated = new Timestamp(new Date().getTime());
    }

    public boolean checkStatus() {
        this.active = (this.getTimeLeft() > 0);
        return this.active;
    }

    public long getTimeDelta(){
        return new Timestamp(new Date().getTime()).getTime() - this.activated.getTime();
    }

    public int getTimeLeft(){
        if (this.duration * 1000 - this.getTimeDelta() > 0){
            return (int) (this.duration - (this.getTimeDelta() / 1000));
        }
        return 0;
    }
}
