package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGame extends Game {
	SpriteBatch batch;

	public static final float PPM = 100f;
	public static float correct = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new Menu(this));
	}

	@Override
	public void render () {
		super.render();
	}
	

}
