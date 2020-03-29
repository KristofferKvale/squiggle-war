package com.mygdx.game.models;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.Game;
import com.mygdx.game.models.Config;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.views.GameStateManager;
import com.mygdx.game.views.GameView;

public class RoomModel {
    //Links up with firebase and creates game and player models
    private ArrayList<PlayerModel> opponents;
    private ArrayList<PlayerModel> players;
    private  PlayerModel player;
    private BoardModel board;
    private String username;
    private Color color;
    //Config config = new Config();
    private Integer countdown = 10;
    private GameView gameView;

    public RoomModel() {
    }

    /*
    public void createPlayer(){
        player = new PlayerModel(config.username, randomcolor, new Vector2(200,200));
        players.add(player);
    }

    //Funksjonen kalles fra RoomView etter det er valgt farge?
    //public void changeColor(){
    //}


    public void countdownComplete() {
        if (countdown == 0){ startGame(); }
    }

    /*
    Start game:
    RoomView push GameView til gsm
    GameView tar imot et BoardModel ved opprettelse
    BoardModel må da ha fått motstandere og spilleren selv av RoomModel

    Så det som må skje i RoomModel er at det lages en liste med spillere i det rommet, den listen brukes for å opprette en BoardModel.
    RoomModel må også ha en funksjon som RoomView kan bruke for å opprette et GameView med den BoardModellen, som pushes til gsm?


    public void startGame(){


    }

    private ArrayList<PlayerModel> getOpponents(){
        //return players.remove(player)
    }

    public BoardModel getBoard(){
        return new BoardModel(getOpponents(), player);
    }


    public void createGameView(GameStateManager gsm){
        gameView = new GameView(gsm, getBoard());
        gsm.push(gameView);
    }
*/


}
