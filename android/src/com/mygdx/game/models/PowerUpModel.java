package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Game;

import java.util.Date;
import java.sql.Timestamp;

public class PowerUpModel {
    String name;
    Vector2 position = Game.randomPosition(50);
    int duration = 30;
    Timestamp activated;
    Boolean active = true;

    public PowerUpModel(String name){
        this.name = name;
        this.activated = new Timestamp(new Date().getTime());
    }

    public PowerUpModel(String name, int duration){
        this.name = name;
        this.duration = duration;
        this.activated = new Timestamp(new Date().getTime());
    }

    public PowerUpModel(String name, Vector2 position){
        this.name = name;
        this.position = position;
        this.activated = new Timestamp(new Date().getTime());
    }

    public PowerUpModel(String name, int duration, Vector2 position){
        this.name = name;
        this.duration = duration;
        this.position = position;
        this.activated = new Timestamp(new Date().getTime());
    }

    public boolean checkStatus(){
        Timestamp now = new Timestamp(new Date().getTime());
        if (now.getTime() - this.activated.getTime() >= this.duration * 1000) {
            this.active = false;
        }
        return this.active;
    }
}
