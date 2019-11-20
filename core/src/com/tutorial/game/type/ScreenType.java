package com.tutorial.game.type;


import com.tutorial.game.screen.AbstractScreen;
import com.tutorial.game.screen.GameScreen;
import com.tutorial.game.screen.LoadingScreen;

public enum ScreenType {
    GAME(GameScreen.class),
    LOADING(LoadingScreen.class);

    private final Class<? extends AbstractScreen> screenClass;

    ScreenType(final Class<? extends AbstractScreen> screenClass){
        this.screenClass = screenClass;
    }

    public Class<? extends  AbstractScreen> getScreenClass(){
        return screenClass;
    }
}
