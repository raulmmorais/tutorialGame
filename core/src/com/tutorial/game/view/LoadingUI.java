package com.tutorial.game.view;

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
    private final String loadingString;

    private final ProgressBar progressBar;
    private final TextButton pressAnyKeyButton;
    private final TextButton txtButton;

    public LoadingUI(final TutorialGame context) {
        super(context.getSkin());
        setFillParent(true);

        final I18NBundle i18NBundle = context.getI18NBundle();

        progressBar = new ProgressBar(0, 1, 0.01f, false, getSkin(), "default");
        //progressBar.setAnimateDuration(1);

        loadingString = i18NBundle.format("loading");
        txtButton = new TextButton(loadingString, getSkin(), "huge");
        txtButton.getLabel().setWrap(true);

        pressAnyKeyButton = new TextButton(i18NBundle.format("pressAnyKey"), getSkin(), "normal");
        pressAnyKeyButton.getLabel().setWrap(true);
        pressAnyKeyButton.setVisible(false);
        pressAnyKeyButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            context.getInputManager().notifyKeyDown(GameKeys.SELECT);
            return true;
            }
        });

        add(pressAnyKeyButton).expand().fill().center().row();
        add(txtButton).expandX().fillX().bottom().row();
        add(progressBar).expandX().fillX().bottom().pad(20, 25, 20, 25);
        //bottom();
    }

    public void setProgress(float progress){
        progressBar.setValue(progress);
        final StringBuilder stringBuilder = txtButton.getLabel().getText();
        stringBuilder.setLength(0);
        stringBuilder.append(loadingString);
        stringBuilder.append(" (");
        stringBuilder.append((int) (progress * 100));
        stringBuilder.append("%)");
        txtButton.getLabel().invalidateHierarchy();

        if (progress >= 1 && !pressAnyKeyButton.isVisible()){
            pressAnyKeyButton.setVisible(true);
            pressAnyKeyButton.setColor(1, 1, 1, 0);
            pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1, 1), Actions.alpha(0, 1))));
            txtButton.addAction(Actions.sequence(Actions.alpha(0, 3)));
            progressBar.addAction(Actions.sequence(Actions.alpha(0, 4)));
        }
    }
}
