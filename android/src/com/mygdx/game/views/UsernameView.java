package com.mygdx.game.views;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Game;

public class UsernameView extends State {

    private Stage stage;
    private TextField usernameField;
    private Texture t;

    public UsernameView(GameStateManager gsm) {

        super(gsm);
        cam.setToOrtho(false, Game.WIDTH / 2, Game.HEIGHT / 2);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
      //  Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.CHARTREUSE;
        usernameField = new TextField("", style);
        usernameField.setPosition(cam.position.x,cam.position.y);
        usernameField.setSize(300,40);
        stage.addActor(this.usernameField);
        this.t = new Texture("badlogic.jpg");
      /*  TextButton  submitButton = new TextButton("Submit", skin);
        submitButton.setPosition(100, 100);
        submitButton.setSize(300,60);
        submitButton.addListener( new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
               // gsm.push(new LobbySelectionView(gsm);
            }
        });
        stage.addActor(submitButton);*/
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {


    }

    @Override
    public void render(SpriteBatch sb) {

        sb.begin();
        sb.draw(this.t,cam.position.x,cam.position.y);
        stage.draw();
        sb.end();
        stage.draw();

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
