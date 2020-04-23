package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public interface Line {

    void addPoint(Vector3 point);
    ArrayList<Vector3> getPoints();
    Vector3 getLastPoint();

}
