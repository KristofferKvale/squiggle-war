package com.mygdx.game.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.List;

public class LineModel implements Line {

    private ArrayList<Vector3> points = new ArrayList<>();
    private String roomID;
    private String playerID;
    private DatabaseReference mDatabase;


    LineModel(Vector3 start, String playerID, String roomID) {
        this.playerID = playerID;
        this.roomID = roomID;
        addPoint(start);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        assert key != null;
        mDatabase.child(key).setValue(start);
    }

    @Override
    public void addPoint(Vector3 point) {
        points.add(point);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        assert key != null;
        mDatabase.child(key).setValue(point);
    }

    @Override
    public ArrayList<Vector3> getPoints() {
        return points;
    }

    @Override
    public Vector3 getLastPoint() {
        return this.points.get(points.size() - 1);
    }

    void delete() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("points");
        mDatabase.removeValue();
        this.points = new ArrayList<>();
        addPoint(Game.randomPlayerPosition(100));
    }

}
