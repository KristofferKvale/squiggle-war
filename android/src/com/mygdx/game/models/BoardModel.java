package com.mygdx.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.List;


public class BoardModel {
    //TO DO:
    // Push position to firebase
    // Push collided boolean to firebase
    // Clear lines and start new round

    public String AdminID;
    public List<PowerUpModel> powerups = new ArrayList<>();
    public float timeseconds = 0f;
    public float postCrash = 0f;
    ArrayList<OpponentModel> opponents;
    float period = 4f;
    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    private PlayerModel player;

    public BoardModel(ArrayList<OpponentModel> opponents, PlayerModel player) {
        this.opponents = opponents;
        this.player = player;
    }

    //Function that returns a player if it has collided with a player or a wall
    public void Collision() {
        if (!player.isCrashed()) {
            CollisionPowerup();
            if (CollisionWalls()) {
                player.setCrashed(true);
            }

            if (!player.isGhost()) {
                if (CollisionPlayer() && player.line_on) {
                    player.setCrashed(true);
                }
                try {
                    if (CollisionOpponent() && player.line_on) {
                        player.setCrashed(true);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    //Help function that checks if one player is outside the board
    private boolean CollisionWalls() {
        ArrayList<Vector3> points = this.player.getLinePoints();
        Vector3 point = points.get(points.size() - 1);
        int x = (int) point.x;
        int y = (int) point.y;
        int z = this.player.getCurrentHeadSize();
        return x + z > Game.PLAYABLE_WIDTH + Game.SPACE_SIDE || x - z < Game.SPACE_SIDE || y + z > Game.PLAYABLE_HEIGHT + Game.SPACE_TOP || y - z < Game.SPACE_TOP;
    }

    //Help function that checks if a players position has been visited

    private boolean CollisionOpponent() {
        for (OpponentModel opponent : this.opponents) {
            ArrayList<Vector3> oppPoints;
            oppPoints = opponent.getPoints();
            for (Vector3 pos : oppPoints) {
                if (player.CollisionTestCircle((int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean CollisionPlayer() {
        ArrayList<Vector3> playerPoints = new ArrayList<>(player.getLinePoints());
        if (playerPoints.size() > 50) {
            playerPoints.subList(playerPoints.size() - 50, playerPoints.size()).clear();
            for (Vector3 pos : playerPoints) {
                if (player.CollisionTestCircle((int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void CollisionPowerup() {
        try {
            for (PowerUpModel powerup : this.powerups) {
                int powerUpX = (int) powerup.position.x;
                int powerUpY = Game.HEIGHT - (int) powerup.position.y - 40;
                if (player.CollisionTestRectangle(powerUpX, powerUpY, 40, 40)) {
                    powerup.activate();
                    this.player.powerups.add(powerup);
                    this.powerups.remove(powerup);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void update(float dt) {
        timeseconds += dt;
        if (timeseconds > period) {
            this.Collision();
            if (!player.isCrashed() && postCrash == 0) {
                player.move(dt);
            }
            this.playersCrashed(dt);
        }
    }

    public void playersCrashed(float dt) {
        //Get status from opponents
        int numPlayerCrash = 0; // Skal være 0
        if (player.isCrashed()) {
            numPlayerCrash++;
        }
        for (OpponentModel opponent : opponents) {
            if (opponent.isCrashed()) {
                numPlayerCrash++;
            }
        }
        if (numPlayerCrash >= opponents.size() && (!Game.PLAY_TESTING || player.isCrashed())) {
            if (postCrash < 6.1f) {
                postCrash += dt;
            } else {
                if (!player.isCrashed()) {
                    player.incScore();
                }
                for (OpponentModel opp : opponents) {
                    opp.nextGame();
                }
                player.nextGame();
                postCrash = 0f;
                timeseconds = 0f;
            }

        }
    }

    public void addRandomPowerUp() {
        int rnd = (int) (Math.random() * Game.AVAILABLE_POWERUPS.length);
        this.powerups.add(new PowerUpModel(Game.AVAILABLE_POWERUPS[rnd]));
    }

    public void addSpeedBoost() {
        this.powerups.add(new PowerUpModel("Speed_boost"));
    }

    public void addGhost() {
        this.powerups.add(new PowerUpModel("Ghost"));
    }

    public void addGrow() {
        this.powerups.add(new PowerUpModel("Grow"));
    }

    public void addShrink() {
        this.powerups.add(new PowerUpModel("Shrink"));
    }


    public ArrayList<OpponentModel> getOpponents() {
        return opponents;
    }

    public PlayerModel getPlayer() {
        return player;
    }
}

