package com.mygdx.game.views;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.models.Config;
import com.mygdx.game.models.RoomModel;

import java.util.ArrayList;

public class ResultView extends State {

    Stage buttonStage;
    TextButton rematch;
    TextButton backToLobby;

    Skin uiskin;


    ArrayList<Score> scores;
    Stage scoreStage;
    Table scoreTable;

    private DatabaseReference mDatabase;
    RoomModel room;
    private GameStateManager gsm;

    public ResultView(GameStateManager gsm, String roomID) {
        super(gsm);
        this.gsm = gsm;
        this.room = new RoomModel(roomID);

        this.uiskin = new Skin(Gdx.files.internal("uiskin.json"));

        getScore(roomID);

        scoreStage = new Stage(new ScreenViewport());
        buttonStage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(buttonStage);

        createButtons();

    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {

        scoreStage.clear();
        this.scoreTable = createScoreTable();
        scoreStage.addActor(scoreTable);

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.setProjectionMatrix(this.cam.combined);
        sb.begin();
        scoreStage.draw();
        buttonStage.draw();
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Table createScoreTable() {
        Table table = new Table();

        Label topLabel = new Label("Players, Score:", uiskin);
        topLabel.setFontScale(4);
        table.add(topLabel).row();

        // Creating labels
        try {
            for (Score score : scores) {
                Label nameLabel = new Label(score.name, uiskin);
                nameLabel.setFontScale(3);
                nameLabel.setAlignment(Align.left);

                Label scoreLabel = new Label(String.valueOf(score.scoreValue), uiskin);
                scoreLabel.setFontScale(3);
                scoreLabel.setAlignment(Align.left);

                if (score.name == Config.getInstance().username) {
                    nameLabel.setColor(Color.RED);
                    scoreLabel.setColor(Color.RED);
                }

                table.add(nameLabel).left();
                table.add(scoreLabel).left().row();
            }

        } catch (Exception e) {

        }

        table.pack();
        table.setX(300);
        table.setY((Game.HEIGHT - table.getHeight()) / 2);

        return table;
    }

    private void getScore(String roomID) {

        final ArrayList<Score> scores = new ArrayList<Score>();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
            mDatabase.child(roomID).child("players").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String username = snapshot.child("username").getValue(String.class);
                        int scoreValue = 0;
                        try{
                            for (DataSnapshot scoreSnapshot:snapshot.child("score").getChildren()){
                                scoreValue = scoreSnapshot.getValue(int.class);
                            }
                        }catch(Exception ignored){}

                        scores.add(new Score(username, scoreValue));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {

        }

        this.scores = scores;
    }

    private void createButtons() {

        float btnScale = 100;
        float fontScale = 2;

        // Back to lobby button
        backToLobby = new TextButton("Back to lobby", uiskin);
        backToLobby.sizeBy(btnScale);
        backToLobby.getLabel().setFontScale(fontScale);

        backToLobby.setX((Game.WIDTH - backToLobby.getWidth()) / 2 + 200);
        backToLobby.setY(200);

        backToLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Log.d("Clicked", "Back button clicked!");
                gsm.push(new LobbySelectView(gsm));
            }
        });

        // Rematch button
        rematch = new TextButton("Rematch", uiskin);
        rematch.sizeBy(btnScale);
        rematch.getLabel().setFontScale(fontScale);

        rematch.setX((Game.WIDTH - rematch.getWidth()) / 2 - 100);
        rematch.setY(200);

        rematch.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Log.d("Clicked", "Rematch button clicked!");
            }
        });

        buttonStage.addActor(backToLobby);
        buttonStage.addActor(rematch);

    }

    private class Score {
        String name;
        int scoreValue;

        public Score(String name, int scoreValue) {
            this.name = name;
            this.scoreValue = scoreValue;
        }

    }
}
