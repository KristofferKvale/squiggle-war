package com.mygdx.game.models;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.GameView;

public class RoomModel {
    //Links up with firebase and creates game and player models
    private ArrayList<String> opponents;
    private ArrayList<String> players = new ArrayList<>();

    private PlayerModel player;
    private BoardModel board;
    private String username;
    private Color color;
    //Config config = new Config();
    private GameView gameView;
    private DatabaseReference mDatabase;
    private String roomID;
    //String playerID;
    private boolean gameStarted;


    public RoomModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        String key = mDatabase.push().getKey();
        this.roomID = key;
        gameStarted = false;
        mDatabase.child(roomID).child("started").setValue(gameStarted);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players");
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID);

        //Legger til nye spillere i "players":
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PlayerModel player = dataSnapshot.getValue(PlayerModel.class);
                if (player.username != null){
                    players.add(player.username); //Henter bare ut username, skal hente ut PlayerModels
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

    }



    public void createPlayer(String u) {
        player = new PlayerModel(u, Color.RED);
        //mDatabase.child(roomID).child("players").child(player.playerID).setValue(player); //PlayerID?
        mDatabase.child(player.playerID).setValue(player); //PlayerID?
        //Må kanskje endres^
    }

    //public void changeColor(){
    //}

    //Skal ta inn currentPlayer
    public ArrayList<String> getOpponents(String username) {
        opponents = new ArrayList<>();
        for (String playername : players) {
            //Log.d("TEST!", playername);
            if (playername != username) {
                opponents.add(playername);
            }
        }
        Log.d("TEST!", String.valueOf(opponents));
        return opponents;
    }


    public void startGame() {
        gameStarted = true;
        mDatabase.child(roomID).child("started").setValue(gameStarted);

    }

    /*public BoardModel getBoard(){
        return new BoardModel(getOpponents(), player);
    }

    public void createGameView(GameStateManager gsm) {
        gameView = new GameView(gsm, getBoard());
        gsm.push(gameView);
    }

    public void playerStart(GameStateManager gsm) {
        createGameView(gsm);
    }
*/

}





