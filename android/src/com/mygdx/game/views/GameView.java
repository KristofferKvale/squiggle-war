package com.mygdx.game.views;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;


public class GameView extends State {
    private int width = Game.WIDTH;
    private int height = Game.HEIGHT;
    private ArrayList<OpponentModel> opponents;
    private PlayerModel player;
    private Texture lines;
    private ShapeRenderer playerHead = new ShapeRenderer();
    private ShapeRenderer playableArea = new ShapeRenderer();
    private BoardModel board;
    private BitmapFont font;
    private BitmapFont playerScore;
    private String number = "3";
    private Pixmap line;
    private ArrayList<BitmapFont> scores;
    private ArrayList<BitmapFont> durations;
    private Float pingtimer = 0f;
    private Float getAllPingTimer = 0f;
    private String adminID;

    public GameView(GameStateManager gsm, BoardModel board) {
        super(gsm);
        this.board = board;
        this.board.addSpeedBoost();
        this.board.addGhost();
        this.board.addGrow();
        this.board.addShrink();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5f);
        line = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        scores = new ArrayList<>();
        durations = new ArrayList<>();
        opponents = board.getOpponents();
        player = board.getPlayer();
        playerScore = new BitmapFont();
        playerScore.setColor(player.getColor());
        playerScore.getData().setScale(5f);
        for(OpponentModel opp : opponents) {
            BitmapFont oppScore = new BitmapFont();
            oppScore.setColor(opp.getColor());
            oppScore.getData().setScale(5f);
            scores.add(oppScore);
        }
        this.updateLine();
        try {
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("admin");
            mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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
    }



    @Override
    public void handleInput() {
        for (int i = 0; i < 10; i++) { // 20 is max number of touch points
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i)<= width / 2) {
                    board.getPlayer().turnRight();
                }

                if (Gdx.input.getX(i) > width / 2) {
                    board.getPlayer().turnLeft();
                }

            }
        }
    }

    @Override
    public void update(float dt) {
        pingtimer += dt;
        if (pingtimer > 1f){
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("players").child(player.playerID).child("ping");
            Date d = new Date();
            mdatabase.setValue(d);
            pingtimer = 0f;
        }

        getAllPingTimer +=dt;
        if (getAllPingTimer > 5f) {
            getAllPingTimer = 0f;
            if (opponents.size()> 0) {
                final String roomID = this.player.getRoomID();

                final String playerID = this.player.playerID;
                final String adminID = this.adminID;
                for (final OpponentModel opponent : opponents) {
                    try {


                        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("players").child(opponent.playerID).child("ping");
                        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    Date d = dataSnapshot.getValue(Date.class);
                                    Date now = new Date();
                                    Long l = now.getTime() - d.getTime();
                                    if (l > 5000) {
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
        this.handleInput();
        this.board.update(dt);
        this.updateLine();
        if (this.board.finished){
            gsm.push(new ResultView(gsm,this.player.getRoomID()));
        }
        if(this.board.timeseconds < 4.1f) {
            if (this.board.timeseconds > 2f && this.board.timeseconds < 3f) {
                number = "1";
            }
            if (this.board.timeseconds < 2f && this.board.timeseconds > 1f) {
                number = "2";
            }
            if (this.board.timeseconds < 1f && this.board.timeseconds >= 0f) {
                line.dispose();
                line = new Pixmap(width, height, Pixmap.Format.RGBA8888);
                number = "3";
            }
            if (this.board.timeseconds > 4) {
                number = "";
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(this.cam.combined);
        renderPlayableArea();
        sb.begin();
        font.draw(sb,number,width/2f,height/2f);
        renderScores(sb);
        renderPowerUpDurations(sb);
        renderPowerUps(sb);
        sb.draw(lines, 0, 0, width, height);
        sb.end();
        renderPlayerHeads();
        lines.dispose();

    }

    @Override
    public void dispose () {
        line.dispose();
        lines.dispose();
    }

    private void updateLine() {
        line.setColor(player.getColor());
        Vector3 pos = player.getLastLinePosition();
        line.fillCircle((int)pos.x + Game.SPACE_SIDE, (int)pos.y + Game.SPACE_TOP, (int)pos.z);

        ArrayList<OpponentModel> players = board.getOpponents();
        for(OpponentModel opponent:players) {
            Vector3 point = opponent.getPosition();
            if (point.x != -100) {
                line.setColor(opponent.getColor());
                line.fillCircle((int)point.x + Game.SPACE_SIDE, (int)point.y + Game.SPACE_TOP, (int)point.z);
            }
        }
        lines = new Texture(line, Format.RGBA8888, false);
    }

    private void renderScores(SpriteBatch sb){
        playerScore.draw(sb, Integer.toString(player.getScore()),(width/2f) + 200, height - 20);
        ListIterator<BitmapFont> scoresIt = scores.listIterator();
        try{
            while (scoresIt.hasNext()) {
                int score = opponents.get(scoresIt.nextIndex()).getScore();
                scoresIt.next().draw(sb, Integer.toString(score), (width/2f) + 300 + scoresIt.nextIndex()*100, height - 20);
            }
        }catch(Exception e) {}
    }

    private void renderPowerUpDurations(SpriteBatch sb){
        int x = 0;
        for(PowerUpModel powerup:this.player.getPowerups()) {
            if (powerup.checkStatus()) {
                BitmapFont powerupDuration = new BitmapFont();
                powerupDuration.setColor(Color.WHITE);
                powerupDuration.getData().setScale(5f);
                durations.add(powerupDuration);
                powerupDuration.draw(sb, ":" + powerup.getTimeLeft(), 60 + 180 * x, height - 10);
                sb.draw(powerup.texture, 180 * x, height-70, 50, 50);
                x += 1;
            }
        }
    }

    private void renderPowerUps(SpriteBatch sb){
        for (PowerUpModel powerup:this.board.powerups){
            sb.draw(powerup.texture, powerup.position.x + Game.SPACE_SIDE, powerup.position.y - Game.SPACE_TOP, 40, 40);
        }
    }


    private void renderPlayableArea() {
        playableArea.begin(ShapeRenderer.ShapeType.Filled);
        playableArea.setColor(Color.GRAY);
        playableArea.rect(Game.SPACE_SIDE, height - Game.SPACE_TOP, Game.PLAYABLE_WIDTH, -Game.PLAYABLE_HEIGHT);
        playableArea.end();
    }

    private void renderPlayerHeads() {
        playerHead.begin(ShapeRenderer.ShapeType.Filled);
        Vector3 pos = player.getPosition();
        playerHead.setColor(player.getColor());
        playerHead.circle((int)pos.x + Game.SPACE_SIDE, height - (int)pos.y - Game.SPACE_TOP, player.getCurrentHeadSize());
        playerHead.end();

        ArrayList<OpponentModel> players = board.getOpponents();
        for(OpponentModel opponent:players) {
            Vector3 point = opponent.getPosition();
            if (point.x != -100 && point != opponent.getLastDrawnHead()) {
                playerHead.begin(ShapeRenderer.ShapeType.Filled);
                playerHead.setColor(opponent.getColor());
                playerHead.circle((int)point.x + Game.SPACE_SIDE, height - (int)point.y - Game.SPACE_TOP, player.getHeadSize((int) point.z));
                playerHead.end();
                opponent.addLastDrawnHead();
            }
        }
    }
}

