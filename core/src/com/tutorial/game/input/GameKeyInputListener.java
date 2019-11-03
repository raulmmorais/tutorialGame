package com.tutorial.game.input;

public interface GameKeyInputListener {
    public void keyPressed(final InputManager manager, final GameKeys key);

    public void keyUp(final InputManager manager, final GameKeys key);
}
