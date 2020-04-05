package com.mygdx.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.models.RoomModel;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.LobbySelectView;
import com.mygdx.game.views.RoomView;

public class RoomController implements InputProcessor {

    private RoomView view;
    private RoomModel room;
    private GameStateManager gsm;

    public RoomController(RoomView view, RoomModel room, GameStateManager gsm){
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        this.view = view;
        this.room = room;
        this.gsm = gsm;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            room.back();
            gsm.push(new LobbySelectView(gsm));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
