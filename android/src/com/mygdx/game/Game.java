package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.models.Config;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.UsernameView;

public class Game extends ApplicationAdapter {

    public static AndroidLauncher mAL;

    //Set to True to enable single player to test new code alone
    public static boolean PLAY_TESTING = false;

    //Screen size
    public static int WIDTH;
    public static int HEIGHT;
    public static final int PLAYABLE_WIDTH = 1750;
    public static final int PLAYABLE_HEIGHT = 1000;
    public static int SPACE_TOP = 90;
    public static int SPACE_SIDE;

    //Gameplay variables
    public static final int DEFAULT_SIZE = 8;
    public static final int SMALL_SIZE = 2;
    public static final int BIG_SIZE = 24;
    public static final int DEFAULT_HEAD_SIZE = 16;
    public static final int SMALL_HEAD_SIZE = 8;
    public static final int BIG_HEAD_SIZE = 36;
    public static final int SPEED = 160;
    public static final double SPEED_BOOST = 1.5;
    public static final double ROTATION_SPEED = 0.03;
    public static final double ROTATION_SPEED_BOOST = 1.4;
    public static final int DEFAULT_POWERUP_DURATION = 8;
    public static final String[] AVAILABLE_POWERUPS = new String[]{"Speed_boost", "Ghost", "Grow", "Shrink"};

    //Gap timer values
    public static final int MIN_GAP_TIME = 30;
    public static final int MAX_GAP_TIME = 80;

    public static final int MIN_LINE_TIME = 200;
    public static final int MAX_LINE_TIME = 250;


    public static final String TITLE = "Squiggle War";

    private GameStateManager gsm;
    private SpriteBatch batch;
    public Config config;

    public Game(AndroidLauncher mAL) {
        Game.mAL = mAL;
    }

    @Override
    public void create() {
        WIDTH = Gdx.app.getGraphics().getWidth();
        HEIGHT = Gdx.app.getGraphics().getHeight();
        SPACE_SIDE = (WIDTH - PLAYABLE_WIDTH) / 2;

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

    public static Vector3 randomPlayerPosition(int distance) {
        int x = (int) (Math.random() * (PLAYABLE_WIDTH - 2 * distance)) + distance;
        int y = (int) (Math.random() * (PLAYABLE_HEIGHT - 2 * distance)) + distance;
        return new Vector3(x, y, DEFAULT_SIZE);
    }

    public static Vector2 randomPosition(int distance) {
        int x = (int) (Math.random() * (PLAYABLE_WIDTH - 2 * distance)) + distance;
        int y = (int) (Math.random() * (PLAYABLE_HEIGHT - 2 * distance)) + distance;
        return new Vector2(x, y);
    }


    public static float startDirection(Vector3 pos) {
        float direction;
        if (pos.x <= (float) Game.WIDTH / 2 && pos.y <= (float) Game.HEIGHT / 2) {
            direction = (float) (Math.random() * 0.5f);
        } else if (pos.x > (float) Game.WIDTH / 2 && pos.y <= (float) Game.HEIGHT / 2) {
            direction = (float) (Math.random() * 0.5f) + 0.5f;
        } else if (pos.x > (float) Game.WIDTH / 2 && pos.y > (float) Game.HEIGHT / 2) {
            direction = (float) (Math.random() * 0.5f) + 1f;
        } else {
            direction = (float) (Math.random() * 0.5f) + 1.5f;
        }
        return (float) (direction * Math.PI);
    }

    public static int getHeadSize(int z) {
        if (z == Game.SMALL_SIZE) {
            return Game.SMALL_HEAD_SIZE;
        } else if (z == Game.BIG_SIZE) {
            return Game.BIG_HEAD_SIZE;
        } else {
            return Game.DEFAULT_HEAD_SIZE;
        }
    }
}
