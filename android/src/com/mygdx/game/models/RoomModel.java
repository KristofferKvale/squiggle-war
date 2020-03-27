package com.mygdx.game.models;

import com.mygdx.game.Game;
import com.mygdx.game.models.Config;
import java.util.List;
import com.badlogic.gdx.math.Vector2;

public class RoomModel {
    //Links up with firebase and creates game and player models
    private Game game;
    private List<PlayerModel> players;
    Config config = new Config();


    public RoomModel() {
        game = new Game();
    }

    /*
    public AddPlayer() {
        //Adds player to list of players inside the room
        //players.add(new PlayerModel(config.username, random_color, new Vector2(200,200)));
    }*/

    //Starts countdown when two players enter room
    //Start game when finished
    //(Vet ikke helt hvordan det skal gjøres)
    // public startGame()

}
