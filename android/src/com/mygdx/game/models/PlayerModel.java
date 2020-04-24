package com.mygdx.game.models;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.Random;


public class PlayerModel {
    private String username;
    private String playerID;
    private Color color;
    private LineModel line;
    private float angle;
    private Vector3 position;
    private ArrayList<PowerUpModel> powerups;
    private int score;
    private boolean crashed = false;
    private boolean ready = false;
    private String roomID;

    private boolean line_on;
    private int line_timer = 0;
    private int line_gap_timer = 0;


    //RoomModel needs an empty constructor to read from database
    public PlayerModel() {
    }

    PlayerModel(final String username, Color color, String roomID) {
        this.username = username;
        this.color = color;
        this.roomID = roomID;
        this.score = 0;
        this.powerups = new ArrayList<>();
        this.line = new LineModel(Game.randomPlayerPosition(100), playerID, roomID);

        this.position = this.line.getLastPoint();
        setAngle(Game.startDirection(this.position));
        this.powerups = new ArrayList<>();
    }


    public ArrayList<Vector3> getLinePoints() {
        return this.line.getPoints();
    }

    public String getUsername() {
        return this.username;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setReadyState(boolean ready) {
        this.ready = ready;
    }

    public boolean getReadyState() {
        return this.ready;
    }

    public float getAngle() {
        return this.angle;
    }

    public void updateAngle(double turnRate){
        this.angle += turnRate;
    }

    public void setAngle(float direction) {
        this.angle = direction;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public Vector3 getLastLinePosition() {
        return this.line.getLastPoint();
    }

    public void setColor(Color c){
        this.color = c;
    }

    public Color getColor() {
        return this.color;
    }

    public int getScore() {
        return score;
    }

    public void incScore() {
        this.score += 1;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String id) {
        this.playerID = id;
    }

    public boolean getLineStatus() {
        return line_on;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }


    // New Point
    public void setNewPoint(int x, int y) {
        Vector3 point = new Vector3(x, y, getSize());

        if ((int) point.x != (int) this.line.getLastPoint().x || (int) point.y != (int) this.line.getLastPoint().y) {
            this.position = point;
            if (this.line_on) {
                this.line.addPoint(point);
            }
        }
    }

    public void setPosition(Vector3 pos) {
        this.position = pos;
    }

    public void deleteLine() {
        this.line.delete();
    }


    // PowerUps
    public ArrayList<PowerUpModel> getPowerups() {
        return this.powerups;
    }

    public void addPowerup(PowerUpModel powerup) {
        this.powerups.add(powerup);
    }

    public boolean hasSpeedBoost() {
        for (PowerUpModel powerup : this.powerups) {
            if (powerup.name.equals("Speed_boost") && powerup.checkStatus()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGhost() {
        for (PowerUpModel powerup : this.powerups) {
            if (powerup.name.equals("Ghost") && powerup.checkStatus()) {
                return true;
            }
        }
        return false;
    }

    private boolean isBig() {
        for (PowerUpModel powerup : this.powerups) {
            if (powerup.name.equals("Grow") && powerup.checkStatus()) {
                return true;
            }
        }
        return false;
    }

    private boolean isSmall() {
        for (PowerUpModel powerup : this.powerups) {
            if (powerup.name.equals("Shrink") && powerup.checkStatus()) {
                return true;
            }
        }
        return false;
    }

    private int getSize() {
        int size = Game.DEFAULT_SIZE;
        if (this.isBig() && this.isSmall()) {
            long bigDelta = 0;
            long smallDelta = 0;
            for (PowerUpModel powerup : this.powerups) {
                if (powerup.name.equals("Grow")) {
                    bigDelta = powerup.getTimeDelta();
                } else if (powerup.name.equals("Shrink")) {
                    smallDelta = powerup.getTimeDelta();
                }
            }
            if (bigDelta >= smallDelta) {
                size = Game.SMALL_SIZE;
            } else {
                size = Game.BIG_SIZE;
            }
        } else if (this.isSmall()) {
            size = Game.SMALL_SIZE;
        } else if (this.isBig()) {
            size = Game.BIG_SIZE;
        }
        return size;
    }


    // Gap generation
    public void updateTimer(float dt) {
        if (this.isGhost()) {
            this.line_on = false;
            this.line_timer = 0;
            this.line_gap_timer = 0;
        } else {
            if (this.line_timer == 0 && this.line_gap_timer == 0) {
                this.line_on = true;
                this.line_timer = randomLineTime();
            }
            if (this.line_timer > this.line_gap_timer) {
                this.line_timer -= dt;
                if (this.line_timer == 0) {
                    this.line_on = false;
                    this.line_gap_timer = randomGapTime();
                }
            } else {
                this.line_gap_timer -= dt;
                if (this.line_gap_timer == 0) {
                    this.line_on = true;
                    this.line_timer = randomLineTime();
                }
            }
        }
    }

    private static int randomGapTime() {
        Random rand = new Random();
        return rand.nextInt(Game.MAX_GAP_TIME - Game.MIN_GAP_TIME) + Game.MIN_GAP_TIME;
    }

    private static int randomLineTime() {
        Random rand = new Random();
        return rand.nextInt(Game.MAX_LINE_TIME - Game.MIN_LINE_TIME) + Game.MIN_LINE_TIME;
    }
}
