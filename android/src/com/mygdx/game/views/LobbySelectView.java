package com.mygdx.game.views;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.models.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class LobbySelectView extends State {
    private static final int LOBBY_TOP = (int) (Game.HEIGHT * 0.7);
    private static final int LOBBY_LEFT = (int) (Game.WIDTH * 0.1);
    private static final int LOBBY_WIDTH = 1000;
    private static final int LOBBY_HEIGHT = 100;
    private static final int LOBBY_DISTANCE = 50;

    private DatabaseReference mDatabase;


    private BitmapFont font;

    private List<Lobby> lobbies;
    private Random rand;
    private Button settings;
    private ArrayList<String> roomIDs = new ArrayList<>();
    int oldListLength = 0;
    int listLength = 0;
    private List<Button> buttons;

    public LobbySelectView(GameStateManager gsm) {
        super(gsm);
        this.font = new BitmapFont();
        this.buttons = new ArrayList<>();
        this.lobbies = new ArrayList<>();
        rand = new Random();
        this.settings = new NormalButton(new Texture("settings.png"),
                Game.WIDTH - 200, Game.HEIGHT - 200, 150, 150,
                new SettingsView(gsm));
        this.buttons.add(this.settings);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    final String roomID = dataSnapshot.getKey();
                    final Boolean[] started = new Boolean[1];
                    mDatabase.child(roomID).child("started").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if (!dataSnapshot.getValue(Boolean.class)) {
                                    roomIDs.add(roomID);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e) {
                    Log.e("Err", e.toString());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Lager én lobby
        // TODO: Check for actual lobbies


    }

    public void makeButtons(){
        //roomIDs.add("1");
        this.buttons = new ArrayList<>();
        this.lobbies = new ArrayList<>();

        this.buttons.add(this.settings);

        if (roomIDs.size() < 5){
            for (int x=0; x<(5-roomIDs.size()); x++){
                mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
                String key = mDatabase.push().getKey();
                mDatabase.child(key).child("started").setValue(false);

            }
        }

        final ListIterator<String> roomIt = roomIDs.listIterator();
        try {
            while (roomIt.hasNext()) {
                final int i = roomIt.nextIndex();
                int y = LOBBY_TOP - i * (LOBBY_HEIGHT + LOBBY_DISTANCE);
                int t = i + 1;
                String name = "Lobby " + t;
                //int players = rand.nextInt(5); //TODO: get number of players from lobby
                int players = 0;

                RoomView rv = new RoomView(gsm);
                rv.createRoom(roomIt.next());
                Lobby lby = new Lobby(LOBBY_LEFT, y, LOBBY_WIDTH, LOBBY_HEIGHT, name, players, rv);

                this.lobbies.add(lby);
                this.buttons.add(lby);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomIDs.get(i)).child("players");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lobbies.get(i).players = (int) dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }catch (Exception e){}
    }

    @Override
    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = Game.HEIGHT - Gdx.input.getY();

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
        listLength = roomIDs.size();
        if (listLength != oldListLength){
            makeButtons();
        }
        oldListLength = listLength;

    }

    @Override
    public void render(SpriteBatch sb) {
        // show things on the screen
        Gdx.gl.glClearColor(0, 0, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(this.cam.combined);

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
        void create() {}

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
        private RoomView roomView;

        Lobby(int x, int y, int width, int height, String name, int players, RoomView gameView) {
            super(x, y, width, height, gameView);
            this.name = name;
            this.roomView = gameView;
            this.players = players;
            this.shapes = new ShapeRenderer();
            this.font = new BitmapFont();
            this.margin = 20;
        }

        @Override
        void click(){
            create();
            super.click();
        }

        @Override
        void create() {
            this.roomView.createPlayer();
        }

        public void draw(SpriteBatch sb){
            this.shapes.begin(ShapeRenderer.ShapeType.Filled);
            this.shapes.rect(this.x, this.y, this.width, this.height);
            this.shapes.end();
            sb.begin();
            font.setColor(Color.MAGENTA);
            font.getData().setScale(5f);
            font.draw(sb, this.name,
                    this.x + this.margin, this.height + this.y - this.margin);
            font.draw(sb, this.players + "/5",
                    Game.WIDTH/2, this.height + this.y - this.margin);
            sb.end();
        }
    }
}
