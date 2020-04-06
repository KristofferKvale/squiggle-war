package com.mygdx.game.views;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.models.Config;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.RoomModel;

import java.util.ArrayList;
import java.util.Comparator;

public class ResultView extends State {

    Stage buttonStage;
    Button rematch;
    Button backToLobby;


    ArrayList<Score> scores;
    Stage scoreStage;
    Table scoreTable;

    private DatabaseReference mDatabase;
    RoomModel room;


    public ResultView(GameStateManager gsm, String roomID) {
        super(gsm);
        this.room = new RoomModel(roomID);
        getScore(roomID);

        scoreStage = new Stage(new ScreenViewport());
        buttonStage = new Stage(new ScreenViewport());

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
        sb.end();
    }

    @Override
    public void dispose() {

    }

    private Table createScoreTable() {
        Table table = new Table();
        Skin uiskin = new Skin(Gdx.files.internal("uiskin.json"));

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
                        int scoreValue = snapshot.child("score").getValue(int.class);

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

    private class Score {
        String name;
        int scoreValue;

        public Score(String name, int scoreValue) {
            this.name = name;
            this.scoreValue = scoreValue;
        }

    }
}
