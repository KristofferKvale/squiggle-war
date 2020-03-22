package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class LobbySelectView extends State {
    //private List<RoomModel> lobbies;

    protected LobbySelectView(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    protected void handleInput() {
        //if justClicked (check which button was hit, based on button -> method)
    }

    @Override
    public void update(float dt) {
        // check for new room information and update it
    }

    @Override
    public void render(SpriteBatch sb) {
        // show things on the screen
    }

    @Override
    public void dispose() {
        // dispose of any textures used
    }
}
