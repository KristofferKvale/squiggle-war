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
    public Timestamp activated = null;
    public Boolean active = true;
    private int duration = 10;

    public PowerUpModel(String name) {
        this.name = name;
        this.texture = new Texture(name + ".png");
    }

    public PowerUpModel(String name, int duration) {
        this.name = name;
        this.texture = new Texture(name + ".png");
        this.duration = duration;
    }

    public PowerUpModel(String name, Vector2 position) {
        this.name = name;
        this.texture = new Texture(name + ".png");
        this.position = position;
    }

    public PowerUpModel(String name, int duration, Vector2 position) {
        this.name = name;
        this.texture = new Texture(name + ".png");
        this.duration = duration;
        this.position = position;
    }

    public void activate() {
        this.activated = new Timestamp(new Date().getTime());
    }

    public boolean checkStatus() {
        Timestamp now = new Timestamp(new Date().getTime());
        long delta = now.getTime() - this.activated.getTime();
        if (delta >= this.duration * 1000) {
            this.active = false;
        }
        System.out.println("Now: " + now.getTime() + ", activated: " + this.activated.getTime() + ", delta: " + delta + "duration: " + this.duration * 1000);
        return this.active;
    }
}
