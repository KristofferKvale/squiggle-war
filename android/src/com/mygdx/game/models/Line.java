package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public interface Line {

    void addPoint(Vector2 point);
    List<Vector2> getPoints();

}
