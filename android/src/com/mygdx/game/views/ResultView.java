package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.mygdx.game.models.RoomModel;

import java.util.ArrayList;

public class ResultView extends State {

    ArrayList<BitmapFont> scores;
    Button rematch;
    Button backToLobby;

    RoomModel room;


    public ResultView(GameStateManager gsm, String roomID) {
        super(gsm);
        this.room = new RoomModel(roomID);

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();

        sb.end();
    }

    @Override
    public void dispose() {

    }
}
