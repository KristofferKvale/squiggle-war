package com.mygdx.game.models;

public class Config {

    private static Config instance = new Config();
    String username = "";

    private Config(){}

    public static Config getInstance(){
        return instance;
    }
}
