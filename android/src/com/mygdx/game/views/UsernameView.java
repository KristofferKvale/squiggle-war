package com.mygdx.game.views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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
        sb.end();


    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
