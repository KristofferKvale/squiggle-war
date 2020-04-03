package com.mygdx.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Game;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.RoomModel;

public class RoomView extends State {

    private RoomModel room;

    private Stage stage;
    private SpriteBatch batch;

    private TextButton.TextButtonStyle style;
    private BitmapFont font;

    private Skin uiskin;

    private PlayerModel[] players;



    public RoomView(GameStateManager gsm) {
        super(gsm);

        batch = new SpriteBatch();

        stage = new Stage(new ScreenViewport());

        uiskin = new Skin(Gdx.files.internal("uiskin.json"));

        //Create Tables
        Table colorTable = colorTable();
        Table playerTable = playerTable();

        //Add tables to stage
        stage.addActor(colorTable);
        stage.addActor(playerTable);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(this.cam.combined);
        sb.begin();

            stage.draw();

        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Table colorTable() {

        //Create Table
        Table mainTable = new Table();

        Label colorLabel = new Label("Pick a color:", uiskin);
        colorLabel.setFontScale(3);

        float size = 150;
        float padSize = 50;

        final Button redBtn = new Button(uiskin);
        redBtn.setColor(Color.RED);

        redBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                redBtn.setColor(Color.CLEAR);
            }
        });

        Button greenBtn = new Button(uiskin);
        greenBtn.setColor(Color.GREEN);
        Button blueBtn = new Button(uiskin);
        blueBtn.setColor(Color.BLUE);

        //Add buttons to table
        mainTable.add(colorLabel);
        mainTable.row();
        mainTable.add(redBtn).width(size).height(size);
        mainTable.add(greenBtn).width(size).height(size).padRight(padSize);
        mainTable.add(blueBtn).width(size).height(size);

        mainTable.pack();

        mainTable.setX(Game.WIDTH - mainTable.getWidth() - 200);
        mainTable.setY((Game.HEIGHT - mainTable.getHeight()) / 2);
        return mainTable;
    }

    private Table playerTable() {

        String[] testNames = {"Adrian", "er", "veldig", "sexy!"};

        //Create Table
        Table mainTable = new Table();

        Label topLabel = new Label("Players:", uiskin);
        topLabel.setFontScale(4);
        mainTable.add(topLabel).row();

        // TEST LOOP
        for (String player : testNames) {
            Label playerLabel = new Label(player, uiskin);
            playerLabel.setFontScale(3);
            playerLabel.setAlignment(Align.left);
            mainTable.add(playerLabel).left().row();
        }


        /*
        for (PlayerModel player : this.players) {
            Label playerLabel = new Label(player.getUsername(), uiskin);
            playerLabel.setFontScale(3);
            mainTable.add(playerLabel).left().row();
        }
        */

        mainTable.pack();

        mainTable.setX(300);
        mainTable.setY((Game.HEIGHT - mainTable.getHeight()) / 2);

        return mainTable;
    }
}
