package com.mygdx.game.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.graphics.Color;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.GameView;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomModel {
    //Links up with firebase and creates game and player models
    private ArrayList<OpponentModel> opponents;

    private PlayerModel player = null;
    private BoardModel board;
    private GameView gameView;
    private DatabaseReference mDatabase;
    private String roomID;
    private boolean gameStarted;
    String AdminID;
    private ArrayList<Color> colors = new ArrayList<>(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CHARTREUSE));



    public RoomModel(String roomId) {
        opponents = new ArrayList<>();
        gameStarted = false;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms");
        if (roomId.equals("1")) {
            this.roomID = mDatabase.push().getKey();
            assert roomID != null;
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
                        if(!player.getPlayerID().equals(playerID)){
                            opponents.add(new OpponentModel(playerID, roomID, setColor()));
                        }

                    }else {
                        opponents.add(new OpponentModel(playerID, roomID, setColor()));
                    }
                }catch (Exception ignored){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String playerID = dataSnapshot.getKey();
                    removePlayer(playerID);

                }catch(Exception ignored){}
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
        player = new PlayerModel(u, setColor(), roomID);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("admin");
        if (opponents.size() == 0){
            mDatabase.setValue(player.getPlayerID());
            this.AdminID = player.getPlayerID();

        }else if (opponents.size() == 1) {
            if (opponents.get(0).getPlayerID().equals(player.getPlayerID())){
                mDatabase.setValue(player.getPlayerID());
                this.AdminID = player.getPlayerID();
            }
        }

    }

    public void back() {
        if (AdminID != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("admin");
            mDatabase.removeValue();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(player.getPlayerID());
        mDatabase.removeValue();


    }


    private Color randomColor(int min, int max){
        int i = (int)(Math.random() * (max - min) + min);
        return colors.get(i);
    }

    private Color setColor() {
        for (OpponentModel opponent : opponents) {
            colors.remove(opponent.getColor());
        }
        return randomColor(0, colors.size());
    }



    public ArrayList<OpponentModel> getOpponents() { return opponents; }


    public void startGame() {
        gameStarted = true;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("started");
        mDatabase.setValue(gameStarted);

    }

    private BoardModel getBoard(){
        if (this.board == null) {
            this.board = new BoardModel(getOpponents(), player);
            this.board.setRoom(this);
        }
        return this.board;
    }

    public String getRoomID() { return this.roomID; }

    private void createGameView(GameStateManager gsm) {
        gameView = new GameView(gsm, getBoard());
        gsm.push(gameView);
    }

    public void playerStart(GameStateManager gsm) {
        createGameView(gsm);
    }

    public PlayerModel getPlayer() { return this.player; }

    public void removeSelf(){
        for (OpponentModel opp : opponents) {
            if (opp.getPlayerID().equals(player.getPlayerID())){
                opponents.remove(opp);
            }
        }
    }

    private void removePlayer(String id){
        for (OpponentModel opp : opponents) {
            if (opp.getPlayerID().equals(id)){
                opponents.remove(opp);
            }

        }
    }

}





