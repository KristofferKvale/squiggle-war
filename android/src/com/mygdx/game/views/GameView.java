package com.mygdx.game.views;

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
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;
import java.util.ListIterator;


public class GameView extends State {
    private int width = Game.WIDTH;
    private int height = Game.HEIGHT;
    private ArrayList<OpponentModel> opponents;
    private PlayerModel player;
    Texture lines;
    private ShapeRenderer playerHead = new ShapeRenderer();
    private ShapeRenderer playableArea = new ShapeRenderer();
    private BoardModel board;
    BitmapFont font;
    BitmapFont playerScore;
    String number = "3";
    public Pixmap line;
    ArrayList<BitmapFont> scores;

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
        scores = new ArrayList<BitmapFont>();
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
        this.handleInput();
        this.board.update(dt);
        this.updateLine();
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
        font.draw(sb,number,width/2,height/2);
        playerScore.draw(sb, Integer.toString(player.getScore()),(width/2) + 200, height - 20);
        ListIterator<BitmapFont> scoresIt = scores.listIterator();
        while (scoresIt.hasNext()) {
            int score = opponents.get(scoresIt.nextIndex()).getScore();
            scoresIt.next().draw(sb, Integer.toString(score), (width/2) + 300 + scoresIt.nextIndex()*100, height - 20);
        }
        sb.draw(lines, 0, 0, width, height);
        for (PowerUpModel powerup:this.board.powerups){
            sb.draw(powerup.texture, powerup.position.x, powerup.position.y, 40, 40);
        }
        sb.end();
        renderPlayerHead();
        lines.dispose();

    }

    @Override
    public void dispose () {
        line.dispose();
        lines.dispose();
    }

    public void updateLine() {
        line.setColor(player.getColor());
        Vector3 pos = player.getLastLinePosition();
        line.fillCircle((int)pos.x, (int)pos.y, (int)pos.z);

        ArrayList<OpponentModel> players = board.getOpponents();
        for(OpponentModel opponent:players) {
            Vector3 point = opponent.getPosition();
            if (point.x != -100) {
                line.setColor(opponent.getColor());
                line.fillCircle((int)point.x, (int)point.y, (int)point.z);
            }
        }
        lines = new Texture(line, Format.RGBA8888, false);
    }

    private void renderPlayableArea() {
        playableArea.begin(ShapeRenderer.ShapeType.Filled);
        playableArea.setColor(Color.GRAY);
        playableArea.rect(Game.SPACE_SIDE, height - Game.SPACE_TOP, Game.PLAYABLE_WIDTH, -Game.PLAYABLE_HEIGHT);
        playableArea.end();
    }

    private void renderPlayerHead() {
        playerHead.begin(ShapeRenderer.ShapeType.Filled);
        Vector3 pos = player.getPosition();
        playerHead.setColor(player.getColor());
        playerHead.circle((int)pos.x, height - (int)pos.y, player.getCurrentHeadSize());
        playerHead.end();
    }
}

