package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector3;

import java.util.List;

public interface Line {

    void addPoint(Vector3 point);
    List<Vector3> getPoints();
    Vector3 getLastPoint();

}
