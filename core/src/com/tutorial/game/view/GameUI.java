package com.tutorial.game.view;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.tutorial.game.TutorialGame;

public class GameUI extends Table {
    public GameUI(final TutorialGame context) {
        super(context.getSkin());
    }
}
