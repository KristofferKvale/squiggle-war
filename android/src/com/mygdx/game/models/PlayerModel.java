package com.mygdx.game.models;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.List;


public class PlayerModel {
            String username;
            Color color;
            LineModel line;
            boolean active;
			float angle;
            private DatabaseReference mDatabase;
            String playerID;
            String gameID;


    public PlayerModel(String username, Color color, Vector2 start){
                this.username = username;
                this.color = color;
                this.active = true;
				this.angle = 1;
                mDatabase = FirebaseDatabase.getInstance().getReference().child("players");
                String key = mDatabase.push().getKey();
                this.playerID = key;
                mDatabase.child(key).setValue(username);
                this.line = new LineModel(start, key);

            }

            public ArrayList<Vector2> getLinePoints(){
                return this.line.getPoints();
            }

            public void setNotActive(){
                this.active = false;
            }

            public void setActive(){
                this.active = true;
			}

			public Vector2 getPosition(){
				return this.line.getLastPoint();
			}

			public Color getColor() {
                return this.color;
            }

			public void turnLeft(){
				this.angle += Game.ROTATION_SPEED;
			}

			public void turnRight(){
				this.angle -= Game.ROTATION_SPEED;
			}

			public void move(){
				Vector2 coords = this.getPosition();
				float x = coords.x;
				float y = coords.y;
				x += (Game.SPEED * Math.cos(this.angle));
				y += (Game.SPEED * Math.sin(this.angle));

				setNewPoint(Math.round(x), Math.round(y));
			}


    public void setNewPoint(int x, int y){
        Vector2 point = new Vector2(x,y);

        this.line.addPoint(point);
    }

}
