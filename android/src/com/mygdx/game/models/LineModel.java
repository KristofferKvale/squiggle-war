package com.mygdx.game.models;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class LineModel implements Line {

    private ArrayList<Vector2> points = new ArrayList<>();

    LineModel(Vector2 start){
        points.add(start);
    }

    @Override
    public void addPoint(Vector2 point) {
        this.points.add(point);
    }

    @Override
    public ArrayList<Vector2> getPoints() {
        return this.points;
    }

    @Override
    public Vector2 getLastPoint() {
        return this.points.get(points.size() - 1);
    }


}
