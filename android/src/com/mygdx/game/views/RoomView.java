package com.mygdx.game.views;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.controllers.RoomController;
import com.mygdx.game.models.Config;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.RoomModel;

import java.util.ArrayList;
import java.util.Date;

public class RoomView extends State {

    private RoomModel room = null;
    public String roomID;

    private Stage mainStage;
    private Stage playerStage;
    private SpriteBatch batch;

    private TextButton.TextButtonStyle style;
    private BitmapFont font;

    private Skin uiskin;
    private float timeToStart;
    private String adminID;

    public PlayerModel player;
    private ArrayList<OpponentModel> opponents;
    private RoomController roomController;
    private Float pingtimer = 0f;
    private Float getAllPingTimer = 0f;

    private DatabaseReference mDatabase;

    private boolean ready = false;

    Table playerTable;
    Table readyTable;

    Image checkMark;


    public RoomView(GameStateManager gsm) {
        super(gsm);
        try {
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("admin");
            mdatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    adminID = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



        batch = new SpriteBatch();

        mainStage = new Stage(new ScreenViewport());
        playerStage = new Stage(new ScreenViewport());

        timeToStart = 0f;

        uiskin = new Skin(Gdx.files.internal("uiskin.json"));


        //Create Tables
        Table colorTable = colorTable();
        this.playerTable = playerTable();

        //Add tables to stage
        mainStage.addActor(colorTable);
        readyTable = createReadyBtn();
        mainStage.addActor(readyTable);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
    }

    public void createRoom(String roomID) {
        this.room = new RoomModel(roomID);
        this.roomID = roomID;
    }

    public void createPlayer() {
        room.createPlayer(Config.getInstance().username);
        roomController = new RoomController(this, room, gsm, mainStage);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

        pingtimer += dt;
        if (pingtimer > 1f){
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("players").child(player.playerID).child("ping");
            Date d = new Date();
            mdatabase.setValue(d);
            pingtimer = 0f;
            DatabaseReference mdata = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("admin");
            mdata.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   adminID =  dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        getAllPingTimer +=dt;
        if (getAllPingTimer > 5f) {
            getAllPingTimer = 0f;
            if (opponents.size()> 0) {
                final String roomID = this.roomID;
                final String adminID = this.adminID;
                final String playerID = this.player.playerID;
                    for (final OpponentModel opponent : opponents) {
                        try {


                            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("players").child(opponent.playerID).child("ping");

                            mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        Date d = dataSnapshot.getValue(Date.class);
                                        Date now = new Date();
                                        Long l = now.getTime() - d.getTime();
                                        if (l > 50000) {
                                            FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(opponent.playerID).removeValue();
                                            if (opponent.playerID.equals(adminID)) {
                                                FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("admin").setValue(playerID);
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
        if(room != null) {
            room.removeSelf();
            if(room.getOpponents().size() >= 1 && this.room.getPlayer().getReadyState()) {
                boolean opponentsReady = false;
                for (OpponentModel opponent : opponents) {
                    if (!opponent.getReadyState()) {
                        opponentsReady = false;
                        break;
                    } else {
                        opponentsReady = true;
                        continue;
                    }
                }
                timeToStart += dt;
            } else if (Game.PLAY_TESTING && this.room.getPlayer().getReadyState()) {
                timeToStart += dt;
            } else {
                timeToStart = 0;
            }

            if(timeToStart > 4.1f) {
                room.playerStart(gsm);
                Log.d("RoomID", this.roomID);
                DatabaseReference roomState = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.roomID).child("started");
                roomState.setValue(true);
            }
        }

        this.playerTable = playerTable();
        playerStage.clear();
        playerStage.addActor(playerTable);
        this.opponents = this.room.getOpponents();
        Log.d("MSG", Float.toString(timeToStart) + " Antall mot: " + (room.getOpponents().size()));
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(this.cam.combined);

        sb.begin();

        mainStage.draw();
        playerStage.draw();

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

        mainTable.add(playerLabel).left();

        if (player.getReadyState()) {
            Image checkMark = new Image(new TextureRegion(new Texture("checkmark.png")));
            mainTable.add(checkMark).width(50).height(50).padLeft(50);
        }

        mainTable.row();


        for (OpponentModel opponent : this.opponents) {
            Label opponentLabel = new Label(opponent.getUsername(), uiskin);
            opponentLabel.setFontScale(3);

            mainTable.add(opponentLabel).left();
            if (opponent.getReadyState()) {
                Image checkMark = new Image(new TextureRegion(new Texture("checkmark.png")));
                mainTable.add(checkMark).width(50).height(50).padLeft(50);
            }
            mainTable.row();
        }

        } catch (Exception e) {

        }

        mainTable.pack();
        mainTable.setX(300);
        mainTable.setY((Game.HEIGHT - mainTable.getHeight()) / 2);

        return mainTable;
    }

    private Table createReadyBtn() {
        Table t = new Table();
        t.setTouchable(Touchable.enabled);

        TextButton readyBtn = new TextButton("Ready", uiskin);

        readyBtn.setTouchable(Touchable.enabled);

        readyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Log.d("READY?", "TRIED!!!");

                ready = !ready;
                checkMark.setVisible(ready);

                if (ready) {
                    mDatabase.child(roomID).child("players").child(room.getPlayer().getPlayerID()).child("ready").setValue(true);
                    room.getPlayer().setReadyState(true);
                } else {
                    mDatabase.child(roomID).child("players").child(room.getPlayer().getPlayerID()).child("ready").setValue(false);
                    room.getPlayer().setReadyState(false);
                }
            }
        });

        t.add(readyBtn);

        readyBtn.sizeBy(200);
        readyBtn.getLabel().setFontScale(3);

        checkMark = new Image(new TextureRegion(new Texture("checkmark.png")));
        checkMark.setVisible(ready);

        t.add(checkMark).width(50).height(50).padLeft(50);

        t.pack();
        t.setX((Game.WIDTH - t.getWidth()) / 2 );
        t.setY(200);

        return t;
    }
}