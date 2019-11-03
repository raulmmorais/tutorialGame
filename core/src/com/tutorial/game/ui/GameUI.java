package com.tutorial.game.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.tutorial.game.TutorialGame;

public class GameUI extends Table {
    public GameUI(final TutorialGame context) {
        super(context.getSkin());
    }
}
