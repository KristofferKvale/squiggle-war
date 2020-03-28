package com.mygdx.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        //Create Tables
        Table colorTable = colorTable();
        Table playerTable = playerTable();

        //Add tables to stage
        stage.addActor(colorTable);
        stage.addActor(playerTable);

    }

    // public void createRoom(RoomModel room) {
    //      this.room = room;
    // }

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
        mainTable.setX(2000);
        mainTable.setY(900);

        Label colorLabel = new Label("Pick a color:", uiskin);
        colorLabel.setFontScale(3);

        float size = 150;
        float padSize = 50;

        Button redBtn = new Button(uiskin);
        redBtn.setColor(Color.RED);
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

        return mainTable;
    }

    private Table playerTable() {

        String[] testNames = {"Adrian", "er", "veldig", "sexy!"};

        //Create Table
        Table mainTable = new Table();
        mainTable.setX(500);
        mainTable.setY(900);

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


        return mainTable;
    }
}
