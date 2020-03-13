package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class LineModel implements Line {

    private List<Vector2> points;

    LineModel(Vector2 start){
        this.points.add(start);
    }

    @Override
    public void addPoint(Vector2 point) {
        this.points.add(point);
    }

    @Override
    public List<Vector2> getPoints() {
        return this.points;
    }


}
