package com.mygdx.game.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;

import java.util.ArrayList;

public class OpponentModel implements Line {
    public String playerID;
    private String roomID;
    private String username;
    private boolean ready = false;
    private int score = 0;
    private boolean crashed = false;
    private Color color;
    private DatabaseReference mDatabase;
    private ArrayList<Vector3> drawnHeads = new ArrayList<>();
    private ArrayList<Vector3> points = new ArrayList<>();


    OpponentModel(String playerId, String roomId, Color inputColor) {
        this.playerID = playerId;
        this.roomID = roomId;
        this.setColor(inputColor);
        final OpponentModel thisOpponent = this;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("username");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisOpponent.username = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("score");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    score = dataSnapshot.getValue(Integer.class);

                } catch (Exception e) {
                    Log.e("ERR", "Err: " + e.toString());
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("crashed");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    crashed = dataSnapshot.getValue(Boolean.class);

                } catch (Exception e) {
                    Log.e("ERR", "Err: " + e.toString());
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("points");
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    addPoint(dataSnapshot.getValue(Vector3.class));
                } catch (Exception ignored) {
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("color");
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    color = dataSnapshot.getValue(Color.class);
                } catch (Exception ignored) {
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("ready");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    setReadyState(dataSnapshot.getValue(Boolean.class));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("ready");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    setReadyState(dataSnapshot.getValue(Boolean.class));
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void setColor(Color color) {
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    boolean isCrashed() {
        return crashed;
    }

    @Override
    public void addPoint(Vector3 point) {
        points.add(point);
    }

    @Override
    public ArrayList<Vector3> getPoints() {
        return points;
    }


    @Override
    public Vector3 getLastPoint() {
        return getPosition();
    }

    public Color getColor() {
        return color;
    }

    void nextGame() {
        points = new ArrayList<>();
    }

    public boolean getReadyState() {
        return this.ready;
    }

    private void setReadyState(boolean ready) {
        this.ready = ready;
    }

    public Vector3 getPosition() {
        if (points.size() >= 1) {
            return this.points.get(points.size() - 1);
        } else {
            return new Vector3(-100, -100, Game.DEFAULT_SIZE);
        }
    }

    String getPlayerID() {
        return playerID;
    }


    public Vector3 getLastDrawnHead() {
        if (this.drawnHeads.size() > 10) { //
            return this.drawnHeads.get(drawnHeads.size() - 10);
        } else if (this.drawnHeads.size() > 0) {
            return this.drawnHeads.get(drawnHeads.size() - 1);
        }
        return new Vector3(-100, -100, Game.DEFAULT_SIZE);
    }


    public void addLastDrawnHead() {
        this.drawnHeads.add(getPosition());
    }
}
