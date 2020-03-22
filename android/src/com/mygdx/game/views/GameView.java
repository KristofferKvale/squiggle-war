package com.mygdx.game.views;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Game;
import com.mygdx.game.models.PlayerModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GameView extends State {

    private int width = 1920;
    private int height = 1080;
    private ArrayList<PlayerModel> players;
    Texture lines;
    Texture texture;


    public GameView(GameStateManager gsm) {
        super(gsm);
        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();
        players = getPlayers();



    }

    public ArrayList<PlayerModel> getPlayers() {
        //Skal egentlig hente fra BoardModel
        return new ArrayList<PlayerModel>(Arrays.asList(new PlayerModel("Per", Color.BLUE, new Vector2(0,0))));
    }

    @Override
    public void handleInput() {
        for (int i = 0; i < 2; i++) { // 20 is max number of touch points
            if (Gdx.input.isTouched(i)) {
                if (Gdx.input.getX(i)<= width / 2) {
                    for(int j = 0; j < players.size(); j++) {
                        players.get(j).turnLeft();
                    }
                }

                if (Gdx.input.getX(i) > width / 2) {
                    for(int j = 0; j < players.size(); j++) {
                        players.get(j).turnRight();
                    }
                }

            }
        }
    }

    @Override
    public void update(float dt) {
        this.handleInput();
        for(int j = 0; j < players.size(); j++) {
            players.get(j).move();
        }
        this.updateLine();

    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sb.begin();
        sb.draw(lines, 500, 500);
        sb.end();
        lines.dispose();

    }

    @Override
    public void dispose () {
        //background.dispose();
        lines.dispose();
    }

    public void updateLine(){
        Pixmap line = new Pixmap(height, width, Pixmap.Format.RGBA8888);
        for(int j = 0; j < players.size(); j++) {
            ArrayList<Vector2> points = players.get(j).getLinePoints();
            line.setColor(players.get(j).getColor());
            for(int i = 0; i < points.size()-1; i++){
                Vector2 point1 = points.get(i);
                Vector2 point2 = points.get(i+1);
                line.drawLine((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
            }

        }
        lines = new Texture(line, Format.RGBA8888, false);
        line.dispose();
    }
}

