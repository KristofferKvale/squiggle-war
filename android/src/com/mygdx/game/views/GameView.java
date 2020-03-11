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

import java.util.ArrayList;

public class GameView extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    private int width = 1920;
    private int height = 1080;
    private ArrayList<GridPoint2> line;

    Texture oneLineTex;
    int x = 0;
    int y = 0;


    public void create () {
        batch = new SpriteBatch();
        //background = new Texture("");
        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();

    }


    public void render () {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        //batch.draw(background, 0, 0, width, height);
        //Importer lines fra board her:
        updateLine();
        batch.draw(oneLineTex, 0, 0);
        batch.end();
        oneLineTex.dispose();
    }

    public void dispose () {
        batch.dispose();
        //background.dispose();
        oneLineTex.dispose();
    }

    public void updateLine(){
        x++;
        y++;
        Pixmap oneLine = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        oneLine.setColor(Color.BLUE);
        for(int i = 0; i < 1; i++){
            oneLine.drawLine(0, 0, x+20, y+20);
            oneLine.drawLine(400, 0, x+420, y+20);

        }

        oneLineTex = new Texture(oneLine, Format.RGBA8888, false);

        oneLine.dispose();
    }
}

