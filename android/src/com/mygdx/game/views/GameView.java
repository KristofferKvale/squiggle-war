package com.mygdx.game.views;

import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.google.android.gms.ads.AdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.controllers.GameController;
import com.mygdx.game.controllers.PlayerController;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;


public class GameView extends State {
    private HashMap<String, Texture> powerup_textures;

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
    private Color player_color;

    private PlayerController playerController;
    private GameController gameController;

    public GameView(GameStateManager gsm, BoardModel board) {
        super(gsm);

        this.powerup_textures = new HashMap<String, Texture>();
        this.powerup_textures.put("Ghost", new Texture("Ghost.png"));
        this.powerup_textures.put("Grow", new Texture("Grow.png"));
        this.powerup_textures.put("Shrink", new Texture("Shrink.png"));
        this.powerup_textures.put("Speed_boost", new Texture("Speed_boost.png"));

        this.board = board;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5f);
        line = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        scores = new ArrayList<>();
        opponents = board.getOpponents();
        player = board.getPlayer();
        this.playerController = new PlayerController(player, board);
        this.gameController = new GameController(player, board);
        player_color = player.getColor();
        playerScore = new BitmapFont();
        playerScore.setColor(player_color);
        playerScore.getData().setScale(5f);
        for(OpponentModel opp : opponents) {
            BitmapFont oppScore = new BitmapFont();
            oppScore.setColor(opp.getColor());
            oppScore.getData().setScale(5f);
            scores.add(oppScore);
        }
        this.updateLine();
    }



    @Override
    public void handleInput() {
        for (int i = 0; i < 10; i++) { // 20 is max number of touch points
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i)<= width / 2) {
                    playerController.turnRight();
                }

                if (Gdx.input.getX(i) > width / 2) {
                    playerController.turnLeft();
                }

            }
        }
    }

    @Override
    public void update(float dt) {
        this.handleInput();
        this.playerController.update(dt);
        this.gameController.update(dt);
        this.updateLine();
        if (this.board.finished){
            Game.mAL.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Game.mAL.mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                            Log.d("AdMob", "Ad showed.");
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Log.d("AdMob", "Ad is closed.");

                        }
                    });
                    Game.mAL.mInterstitialAd.show();
                }
            });
            gsm.push(new ResultView(gsm, player.getRoomID()));
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
        line.setColor(player_color);
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
        playerScore.draw(sb, "You: " + player.getScore(),(width/2f)-200, height - 20);
        ListIterator<BitmapFont> scoresIt = scores.listIterator();
        try{
            while (scoresIt.hasNext()) {
                OpponentModel opp = opponents.get(scoresIt.nextIndex());
                int score = opp.getScore();
                String name = opp.getUsername();
                scoresIt.next().draw(sb, name.substring(0,3) + ": " + score, (width/2f)-200 + scoresIt.nextIndex()*250, height - 20);
            }
        }catch(Exception ignored) {}
    }

    private void renderPowerUpDurations(SpriteBatch sb){
        int x = 0;
        for(PowerUpModel powerup:this.player.getPowerups()) {
            if (powerup.checkStatus()) {
                BitmapFont powerupDuration = new BitmapFont();
                powerupDuration.setColor(Color.WHITE);
                powerupDuration.getData().setScale(5f);
                powerupDuration.draw(sb, ":" + powerup.getTimeLeft(), 60 + 180 * x, height - 10);
                sb.draw(this.powerup_textures.get(powerup.name), 180 * x, height-70, 50, 50);
                x += 1;
            }
        }
    }

    private void renderPowerUps(SpriteBatch sb){
        try{
            for (PowerUpModel powerup:this.board.getPowerUps()){
                sb.draw(this.powerup_textures.get(powerup.name), powerup.position.x + Game.SPACE_SIDE, powerup.position.y - Game.SPACE_TOP, 40, 40);
            }
        } catch (Exception ignored){}
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
        playerHead.setColor(player_color);
        playerHead.circle((int)pos.x + Game.SPACE_SIDE, height - (int)pos.y - Game.SPACE_TOP, Game.getHeadSize((int)pos.z));
        playerHead.end();

        ArrayList<OpponentModel> players = board.getOpponents();
        for(OpponentModel opponent:players) {
            Vector3 point = opponent.getPosition();
            if (point.x != -100 && point != opponent.getLastDrawnHead()) {
                playerHead.begin(ShapeRenderer.ShapeType.Filled);
                playerHead.setColor(opponent.getColor());
                playerHead.circle((int)point.x + Game.SPACE_SIDE, height - (int)point.y - Game.SPACE_TOP, Game.getHeadSize((int) point.z));
                playerHead.end();
                opponent.addLastDrawnHead();
            }
        }
    }
}

