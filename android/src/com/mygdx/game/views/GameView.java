package com.mygdx.game.views;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.PlayerModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GameView extends State {

    private int width = Game.WIDTH;
    private int height = Game.HEIGHT;
    private ArrayList<PlayerModel> players;
    Texture lines;
    Texture texture;
    private Texture background;
    private BoardModel board;
    BitmapFont font;
    String number = "3";
    public Pixmap line;

    public GameView(GameStateManager gsm, BoardModel board) {
        super(gsm);
        background = new Texture("badlogic.jpg");
        this.board = board;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5f);
        line = new Pixmap(width, height, Pixmap.Format.RGBA8888);
    }



    @Override
    public void handleInput() {
        for (int i = 0; i < 2; i++) { // 20 is max number of touch points
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

        sb.begin();
        font.draw(sb,number,width/2,height/2);
        sb.draw(lines, 0, 0, width, height);
        sb.end();
        lines.dispose();

    }

    @Override
    public void dispose () {
        //background.dispose();
        line.dispose();
        lines.dispose();
    }

    public void updateLine() {

        ArrayList<PlayerModel> players = board.getPlayers();
        for(int j = 0; j < players.size(); j++) {
            Vector2 point = players.get(j).getPosition();
            line.setColor(players.get(j).getColor());
            line.fillCircle((int)point.x, (int)point.y, 8);
        }
        lines = new Texture(line, Format.RGBA8888, false);
    }
}

