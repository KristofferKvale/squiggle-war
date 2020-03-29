package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.models.Config;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.GameView;
import com.mygdx.game.views.UsernameView;

public class Game extends ApplicationAdapter {

    public static int WIDTH;
    public static int HEIGHT;
    public static final int SPEED = 2;
    public static final double ROTATION_SPEED = 0.03;

    public static final String TITLE = "Squiggle War";

    private GameStateManager gsm;
    private SpriteBatch batch;
    public Config config;

    @Override
    public void create() {
        WIDTH = Gdx.app.getGraphics().getWidth();
        HEIGHT = Gdx.app.getGraphics().getHeight();

        config = Config.getInstance();
        batch = new SpriteBatch();
        gsm = new GameStateManager();
        Gdx.gl.glClearColor(1, 0, 0, 1);
        gsm.push(new UsernameView(gsm, Config.getInstance()));
    }

    @Override
    public void render() {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static Vector2 randomPosition() {
        int x = (int) (Math.random() * (WIDTH - 200)) + 100;
        int y = (int) (Math.random() * (HEIGHT - 200)) + 100;
        return new Vector2(x, y);
    }
}
