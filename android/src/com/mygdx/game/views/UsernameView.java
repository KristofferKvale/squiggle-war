package com.mygdx.game.views;

import androidx.annotation.NonNull;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.mygdx.game.models.Config;

public class UsernameView extends State {

    private Stage stage;
    private TextField usernameField;
    private Texture t;
    private Config config;
    private GameStateManager g;

    public UsernameView(GameStateManager gsm, final Config config) {

        super(gsm);
        g = gsm;
/*        cam.setToOrtho(false, Game.WIDTH / 2f, Game.HEIGHT / 2f);
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
        this.t = new Texture("badlogic.jpg");*/
        this.config= config;
        Gdx.input.getTextInput(new Input.TextInputListener() {

            @Override
            public void input(String text) {
                config.username = text;
                g.push(new LobbySelectView(g));

            }

            @Override
            public void canceled() {
                System.out.println("ups");
            }
        },"Username","","");
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
        g = gsm;
        if(Gdx.input.justTouched()){
            g = gsm;
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    config.username = text;


                }

                @Override
                public void canceled() {
                    System.out.println("ups");
                }
            },"Username","","");
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if (this.config.username != null){
            gsm.push(new LobbySelectView(gsm));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0, 0, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(this.cam.combined);

        sb.begin();
        //sb.draw(this.t,cam.position.x,cam.position.y);
        sb.end();


    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
