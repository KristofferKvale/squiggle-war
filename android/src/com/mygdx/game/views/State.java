package com.mygdx.game.views;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Game;

public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse;
    protected GameStateManager gsm;

    protected State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera(Game.WIDTH, Game.HEIGHT);
        cam.setToOrtho(false);
        mouse = new Vector3();

    }

    // handleInput takes the input, if a controller is involved just send it to the controller
    protected abstract void handleInput();

    // update changes the models by using the models update function(s)
    public abstract void update(float dt);

    public abstract void render(SpriteBatch sb);

    // dispose is used to dispose music, textures etc. that has been used in the view
    public abstract void dispose();
}