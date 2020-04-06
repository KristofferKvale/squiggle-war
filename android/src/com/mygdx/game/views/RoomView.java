package com.mygdx.game.views;

import android.util.Log;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.Game;
import com.mygdx.game.controllers.RoomController;
import com.mygdx.game.models.Config;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.RoomModel;

import java.util.ArrayList;

public class RoomView extends State {

    private RoomModel room = null;
    private String roomID;

    private Stage stage;
    private SpriteBatch batch;

    private TextButton.TextButtonStyle style;
    private BitmapFont font;

    private Skin uiskin;
    private float timeToStart;

    private PlayerModel player;
    private ArrayList<OpponentModel> opponents;
    private RoomController roomController;

    Table playerTable;


    public RoomView(GameStateManager gsm) {
        super(gsm);

        batch = new SpriteBatch();

        stage = new Stage(new ScreenViewport());

        timeToStart = 0f;

        uiskin = new Skin(Gdx.files.internal("uiskin.json"));


        //Create Tables
        Table colorTable = colorTable();
        this.playerTable = playerTable();

        //Add tables to stage
        stage.addActor(colorTable);

        //Stage should control input:
        Gdx.input.setInputProcessor(stage);
    }

    public void createRoom(String roomID) {
        this.room = new RoomModel(roomID);
        this.roomID = roomID;
    }

    public void createPlayer() {
        room.createPlayer(Config.getInstance().username);
        roomController = new RoomController(this, room, gsm);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(room != null) {
            if(room.getOpponents().size() >= 1) {
                timeToStart += dt;
            }
            if(timeToStart > 4.1f) {
                room.playerStart(gsm);
                DatabaseReference roomState = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("started");
                roomState.setValue(true);
            }
        }
        this.playerTable = playerTable();
        stage.addActor(playerTable);

        Log.d("MSG", Float.toString(timeToStart) + " Antall mot: " + (room.getOpponents().size()));
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

        try {
            this.player = this.room.getPlayer();
            this.opponents = this.room.getOpponents();
        } catch (Exception e) {

        }

        //Create Table
        Table mainTable = new Table();

        Label topLabel = new Label("Players:", uiskin);
        topLabel.setFontScale(4);
        mainTable.add(topLabel).row();

        // Creating labels
        try {
        Label playerLabel = new Label(player.getUsername(), uiskin);
        playerLabel.setFontScale(3);
        playerLabel.setAlignment(Align.left);
        mainTable.add(playerLabel).left().row();


        for (OpponentModel opponent : this.opponents) {
            Label opponentLabel = new Label(opponent.getUsername(), uiskin);
            opponentLabel.setFontScale(3);
            mainTable.add(opponentLabel).left().row();
        }

        } catch (Exception e) {

        }

        mainTable.pack();
        mainTable.setX(300);
        mainTable.setY((Game.HEIGHT - mainTable.getHeight()) / 2);

        return mainTable;
    }
}
