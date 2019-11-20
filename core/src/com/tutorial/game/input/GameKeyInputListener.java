package com.tutorial.game.input;

public interface GameKeyInputListener {
    void keyPressed(final InputManager manager, final GameKeys key);

    void keyUp(final InputManager manager, final GameKeys key);
}
