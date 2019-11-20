package com.tutorial.game.view;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.tutorial.game.TutorialGame;

public class GameUI extends Table {
    public GameUI(final TutorialGame context) {
        super(context.getSkin());
        setFillParent(true);

        add(new TextButton("Raul", context.getSkin(), "huge")).fillX().expandX().bottom();
        bottom();
    }
}
