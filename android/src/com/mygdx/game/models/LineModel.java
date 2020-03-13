package com.mygdx.game.models;

import java.util.List;

public class LineModel implements Line {

    private List<Point> points;

    LineModel(Point start){
        this.points.add(start);
    }

    @Override
    public void addPoint(Point point) {
        this.points.add(point);
    }

    @Override
    public List<Point> getPoints() {
        return this.points;
    }


}
