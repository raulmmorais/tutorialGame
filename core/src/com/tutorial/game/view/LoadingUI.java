package com.tutorial.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;
import com.tutorial.game.TutorialGame;
import com.tutorial.game.input.GameKeys;

public class LoadingUI extends Table {
    private final ProgressBar progressBar;
    private final TextButton loading;
    private final TextButton pressAnyKey;
    private final String loadingText;
    public LoadingUI(final TutorialGame context) {
        super(context.getSkin());
        setFillParent(true);

        final I18NBundle i18NBundle = context.getI18NBundle();

        progressBar = new ProgressBar(0, 1, 0.01f, false, getSkin(), "default");
        progressBar.setAnimateDuration(1);
        loadingText = i18NBundle.format("loading");
        loading = new TextButton(i18NBundle.format("loading"), getSkin(), "normal");
        loading.getLabel().setWrap(true);

        pressAnyKey = new TextButton(i18NBundle.format("pressAnyKey"), getSkin(), "normal");
        pressAnyKey.getLabel().setWrap(true);
        pressAnyKey.setVisible(false);
        pressAnyKey.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                context.getInputManager().notifyKeyDown(GameKeys.SELECT);
                return true;
            }
        });

        add(pressAnyKey).expand().fill().center().row();
        add(loading).expandX().fillX().bottom().row();
        add(progressBar).expandX().fillX().bottom().pad(20, 25, 20, 25);
        bottom();
        //setDebug(true, true);
    }

    public void setProgress(float progress){
        progressBar.setValue(progress);
        final StringBuilder stringBuilder = loading.getLabel().getText();
        stringBuilder.setLength(0);
        stringBuilder.append(loadingText);
        stringBuilder.append(" (" + progress * 100 + " %)");
        Gdx.app.debug("Progress", loading.getLabel().toString());

        if (progress >= 1 && !pressAnyKey.isVisible()){
            pressAnyKey.setVisible(true);
            pressAnyKey.setColor(1, 1, 1, 0);
            pressAnyKey.addAction(Actions.forever(Actions.sequence(Actions.alpha(1, 1), Actions.alpha(0, 1))));
            loading.addAction(Actions.sequence(Actions.alpha(0, 3)));
            progressBar.addAction(Actions.sequence(Actions.alpha(0, 4)));
        }
    }
}
