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
    private ArrayList<OpponentModel> opponents;
    private ArrayList<String> players = new ArrayList<>();

    private PlayerModel player = null;
    private BoardModel board;
    private String username;
    private Color color;
    //Config config = new Config();
    private GameView gameView;
    private DatabaseReference mDatabase;
    private String roomID;
    //String playerID;
    private boolean gameStarted;
    public String AdminID;



    public RoomModel(String roomId) {
        opponents = new ArrayList<>();
        gameStarted = false;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        if (roomId == "1") {
            String key = mDatabase.push().getKey();
            this.roomID = key;
            mDatabase.child(roomID).child("started").setValue(gameStarted);
        } else {
            this.roomID = roomId;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players");
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID);

        //Legger til nye spillere i "players":
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    String playerID = dataSnapshot.getKey();
                    if (player != null){
                        if(player.playerID != playerID){
                            players.add(playerID);
                            opponents.add(new OpponentModel(playerID, roomID));
                        }

                    }else {
                        players.add(playerID);
                        opponents.add(new OpponentModel(playerID, roomID));
                    }
                }catch (Exception e){}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String playerID = dataSnapshot.getKey();
                    opponents.remove(playerID);
                }catch(Exception e){}
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
        player = new PlayerModel(u, Color.RED, roomID);
        if (opponents.size() > 0) {
            if(opponents.get(opponents.size()-1).getPlayerID() == player.playerID) {
                opponents.remove(opponents.size()-1);
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("admin");
        if (opponents.size() == 0){
            mDatabase.setValue(player.playerID);
            this.AdminID = player.playerID;
            //board.AdminID = player.playerID;

        }

    }

    public void back() {
        if (AdminID != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("admin");
            mDatabase.removeValue();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(player.playerID);
        mDatabase.removeValue();


    }

    //public void changeColor(){
    //}

    //Skal ta inn currentPlayer
    public ArrayList<OpponentModel> getOpponents() {
       /* opponents = new ArrayList<>();
        for (String playername : players) {
            //Log.d("TEST!", playername);
            if (playername != username) {
                opponents.add(playername);
            }
        }
        Log.d("TEST!", String.valueOf(opponents));*/
        return opponents;
    }


    public void startGame() {
        gameStarted = true;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("started");
        mDatabase.setValue(gameStarted);

    }

    public BoardModel getBoard(){
        return new BoardModel(getOpponents(), player);
    }

    public void createGameView(GameStateManager gsm) {
        gameView = new GameView(gsm, getBoard());
        gsm.push(gameView);
    }

    public void playerStart(GameStateManager gsm) {
        createGameView(gsm);
    }

    public PlayerModel getPlayer() { return this.player; }

}





