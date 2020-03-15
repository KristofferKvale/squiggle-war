package com.mygdx.game.views;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.mygdx.game.Game;

import java.util.ArrayList;

public class GameView extends State {

    SpriteBatch batch;
    Texture background;

    private ArrayList<GridPoint2> line;

    Texture oneLineTex;
    int x = 0;
    int y = 0;

    public GameView(GameStateManager gsm) {
        super(gsm);

        batch = new SpriteBatch();
        //background = new Texture("");
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        //batch.draw(background, 0, 0, width, height);
        //Importer lines fra board her:
        //updateLine();

        //batch.draw(oneLineTex, 0, 0);

        batch.end();

        //oneLineTex.dispose();
    }

    @Override
    public void dispose () {
        batch.dispose();
        //background.dispose();
        oneLineTex.dispose();
    }

    public void updateLine(){
        x++;
        y++;
        Pixmap oneLine = new Pixmap(Game.WIDTH, Game.HEIGHT, Pixmap.Format.RGBA8888);

        oneLine.setColor(Color.BLUE);
        for(int i = 0; i < 1; i++){
            oneLine.drawLine(0, 0, x+20, y+20);
            oneLine.drawLine(400, 0, x+420, y+20);

        }

        oneLineTex = new Texture(oneLine, Format.RGBA8888, false);

        oneLine.dispose();
    }
}

