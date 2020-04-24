package com.mygdx.game.controllers;

import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;

public class PlayerController extends Controller {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID());

    public PlayerController(PlayerModel player, BoardModel board) {
        super(player, board);
    }

    @Override
    public void update(float dt) {
        if (!player.isCrashed() && this.board.gameStarted() && this.board.getPostCrash() == 0f){
            move(dt);
            Collision();
        }
    }

    public void turnLeft() {
        if (this.board.gameStarted()) {
            if (this.player.hasSpeedBoost()) {
                this.player.updateAngle(Game.ROTATION_SPEED * Game.ROTATION_SPEED_BOOST);
            } else {
                this.player.updateAngle(Game.ROTATION_SPEED);
            }
        }
    }

    public void turnRight() {
        if (this.board.gameStarted()) {
            if (this.player.hasSpeedBoost()) {
                this.player.updateAngle(-(Game.ROTATION_SPEED * Game.ROTATION_SPEED_BOOST));
            } else {
                this.player.updateAngle(-Game.ROTATION_SPEED);
            }
        }
    }


    private void move(float dt) {
        Vector3 pos = this.player.getPosition();
        int speed = Game.SPEED;
        float x = pos.x;
        float y = pos.y;
        if (this.player.hasSpeedBoost()) {
            speed *= Game.SPEED_BOOST;
        }
        float angle = this.player.getAngle();
        x += (speed * Math.cos(angle) * dt);
        y += (speed * Math.sin(angle) * dt);

        this.player.updateTimer(dt);
        this.player.setNewPoint(Math.round(x), Math.round(y));
    }


    //Function that returns a player if it has collided with a player or a wall
    private void Collision() {
        Vector3 pos = this.player.getPosition();
        if (!player.isCrashed()) {
            CollisionPowerup(pos);
            if (CollisionWalls(pos)) {
                this.player.setCrashed(true);
                String key =  mDatabase.child("players").child(player.getPlayerID()).child("crashed").push().getKey();
                assert key != null;
                mDatabase.child("players").child(player.getPlayerID()).child("crashed").child(key).setValue(true);
            }

            if (!this.player.isGhost()) {
                if (CollisionPlayer(pos) && this.player.getLineStatus()) {
                    this.player.setCrashed(true);
                    String key =  mDatabase.child("players").child(player.getPlayerID()).child("crashed").push().getKey();
                    assert key != null;
                    mDatabase.child("players").child(player.getPlayerID()).child("crashed").child(key).setValue(true);
                }
                try {
                    if (CollisionOpponent(pos) && this.player.getLineStatus()) {
                        this.player.setCrashed(true);
                        String key =  mDatabase.child("players").child(player.getPlayerID()).child("crashed").push().getKey();
                        assert key != null;
                        mDatabase.child("players").child(player.getPlayerID()).child("crashed").child(key).setValue(true);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }


    //Help function that checks if one player is outside the board
    private boolean CollisionWalls(Vector3 playerPos) {
        int x = (int) playerPos.x;
        int y = (int) playerPos.y;
        int z = Game.getHeadSize((int) playerPos.z);

        return x + z > Game.PLAYABLE_WIDTH || x - z < 0 || y + z > Game.PLAYABLE_HEIGHT || y - z < 0;
    }

    //Help function that checks if a players position has been visited
    private boolean CollisionOpponent(Vector3 playerPos) {
        for (OpponentModel opponent : board.getOpponents()) {
            ArrayList<Vector3> oppPoints;
            oppPoints = opponent.getPoints();
            for (Vector3 pos : oppPoints) {
                if (CollisionTestCircle(playerPos, (int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean CollisionPlayer(Vector3 playerPos) {
        ArrayList<Vector3> playerPoints = new ArrayList<>(this.player.getLinePoints());
        if (playerPoints.size() > 50) {
            playerPoints.subList(playerPoints.size() - 50, playerPoints.size()).clear();
            for (Vector3 pos : playerPoints) {
                if (CollisionTestCircle(playerPos, (int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void CollisionPowerup(Vector3 playerPos) {
        try {
            for (PowerUpModel powerUp : board.getPowerUps()) {
                int powerUpX = (int) powerUp.position.x;
                int powerUpY = Game.HEIGHT - (int) powerUp.position.y - 40;
                if (CollisionTestRectangle(playerPos, powerUpX, powerUpY, 40, 40)) {
                    powerUp.activate();
                    this.player.addPowerup(powerUp);
                    board.removePowerUp(powerUp);
                    mDatabase.child("powerups").child(powerUp.name).removeValue();
                }
            }
        } catch (Exception ignored) {
        }
    }

    private float dist(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
    }

    private boolean CollisionTestCircle(Vector3 playerPos, int x, int y, int r) {
        return (dist((int) playerPos.x, (int) playerPos.y, x, y) <= r + Game.getHeadSize((int) playerPos.z));
    }

    private boolean CollisionTestRectangle(Vector3 playerPos, int x, int y, int w, int h) {
        int testX = (int) playerPos.x;
        int testY = (int) playerPos.y;

        if (playerPos.x < x) testX = x;        // left edge
        else if (playerPos.x > x + w) testX = x + w;     // right edge
        if (playerPos.y < y) testY = y;        // top edge
        else if (playerPos.y > y + h) testY = y + h;     // bottom edge

        return (dist((int) playerPos.x, (int) playerPos.y, testX, testY) <= Game.getHeadSize((int) playerPos.z));
    }
}
