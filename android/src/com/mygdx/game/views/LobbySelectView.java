package com.mygdx.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Game;
import com.mygdx.game.models.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LobbySelectView extends State {
    private static final int LOBBY_TOP = (int) (Gdx.graphics.getHeight() * 0.9);
    private static final int LOBBY_LEFT = (int) (Gdx.graphics.getWidth() * 0.1);
    private static final int LOBBY_WIDTH = 1000;
    private static final int LOBBY_HEIGHT = 100;
    private static final int LOBBY_DISTANCE = 50;

    private BitmapFont font;

    private List<Lobby> lobbies;

    private Button settings;

    private List<Button> buttons;

    public LobbySelectView(GameStateManager gsm) {
        super(gsm);
        this.font = new BitmapFont();

        this.buttons = new ArrayList<>();
        this.settings = new NormalButton(new Texture("settings.png"), Gdx.graphics.getWidth()-150, Gdx.graphics.getHeight()-150, 150, 150, new SettingsView(gsm));
        this.buttons.add(this.settings);

        // TODO: Check for actual lobbies
        Random rand = new Random();
        this.lobbies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int height = LOBBY_HEIGHT;
            int y = LOBBY_TOP - height - LOBBY_DISTANCE - i * (height + LOBBY_DISTANCE);
            String name = "Lobby " + i;
            int players = rand.nextInt(5);

            RoomView rv = new RoomView(gsm);
            Lobby lby = new Lobby(LOBBY_LEFT, y, LOBBY_WIDTH, height, name, players, rv);

            this.lobbies.add(lby);
            this.buttons.add(lby);
        }
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (Button btn : this.buttons){
                if (btn.checkHit(x, y)) {
                    btn.click();
                }
            }
        }

    }

    @Override
    public void update(float dt) {
        // check for new room information and update it
        this.handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        // show things on the screen
        Gdx.gl.glClearColor(0, 0, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.settings.draw(sb);

        for (Lobby l : this.lobbies){
            l.draw(sb);
        }

        sb.begin();
        this.font.getData().setScale(5f);
        this.font.draw(sb, Config.getInstance().username, 50, Game.HEIGHT - 50);
        sb.end();


        /*//working shitty code
        this.shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < this.lobbies.size(); i++) {
            this.shapes.rect(this.lobbyLeft, this.lobbyBottom - i * (this.lobbyHeight + this.lobbyMargin), this.lobbyWidth, this.lobbyHeight);
        }
        this.shapes.end();

        sb.begin();
        this.font.getData().setScale(5f);
        this.font.setColor(Color.MAGENTA);
        for (int i = 0; i < this.lobbies.size(); i++) {
            //sb.draw(this.lobby, this.lobbyLeft, this.lobbyBottom - i * (this.lobbyHeight + 10), this.lobbyWidth, this.lobbyHeight);
            this.font.draw(sb,this.lobbies.get(i).get("name").toString(),this.lobbyLeft + this.lobbyMargin, this.lobbyBottom + this.lobbyHeight - this.lobbyMargin - i * (this.lobbyHeight + this.lobbyMargin));
            this.font.draw(sb,this.lobbies.get(i).get("players").toString() + "/5",this.lobbyLeft + 700, this.lobbyBottom + this.lobbyHeight - this.lobbyMargin - i * (this.lobbyHeight + this.lobbyMargin));
        }
        sb.end();*/
    }

    @Override
    public void dispose() {
        // dispose of any textures used
    }

    private abstract class Button{
        int x;
        int y;
        int width;
        int height;
        State state;

        Button(int x, int y, int width, int height, State state){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.state = state;
        }

        boolean checkHit(int x, int y){
            boolean x_good = false;
            boolean y_good = false;
            if (x > this.x && x < this.x + this.width) {
                x_good = true;
            }
            if (y > this.y && y < this.y + this.height) {
                y_good = true;
            }
            return (x_good && y_good);
        }

        void click(){
            gsm.push(this.state);
        }

        abstract void draw(SpriteBatch sb);
    }

    private class NormalButton extends Button{
        private Texture texture;

        NormalButton(Texture texture, int x, int y, int width, int height, State state) {
            super(x, y, width, height, state);
            this.texture = texture;
        }

        @Override
        void draw(SpriteBatch sb) {
            sb.begin();
            sb.draw(this.texture, this.x, this.y, this.width, this.height);
            sb.end();
        }
    }

    private class Lobby extends Button{
        private ShapeRenderer shapes;
        private BitmapFont font;
        private String name;
        private int players;
        private int margin;
        private int gap;

        Lobby(int x, int y, int width, int height, String name, int players, RoomView gameView) {
            super(x, y, width, height, gameView);
            this.name = name;
            this.players = players;
            this.shapes = new ShapeRenderer();
            this.font = new BitmapFont();
            this.margin = 20;
            this.gap = 700;
        }

        public void draw(SpriteBatch sb){
            this.shapes.begin(ShapeRenderer.ShapeType.Filled);
            this.shapes.rect(this.x, this.y, this.width, this.height);
            this.shapes.end();
            sb.begin();
            font.setColor(Color.MAGENTA);
            font.getData().setScale(5f);
            font.draw(sb, this.name, this.x + this.margin, this.y - this.margin + this.height);
            font.draw(sb, this.players + "/5", this.x + gap, this.y - this.margin + this.height);
            sb.end();
        }
    }
}
