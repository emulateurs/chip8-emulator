package fr.emulators.chip8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;

import java.io.File;
import java.io.IOException;

public class Main extends ApplicationAdapter {

	private Components components;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		this.components = new Components();
		components.initPixels();
		this.shapeRenderer = new ShapeRenderer();
		try {
			components.loadGame(new File("game.ch8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timer.schedule(new Timer.Task(){

			@Override
			public void run() {
				if(components.cpu.soundCounter > 0) components.cpu.soundCounter-=1;
				if(components.cpu.systemCounter > 0) components.cpu.systemCounter-=1;
			}
		}, 0.16f);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		try {
			Thread.sleep(100);
			components.update(shapeRenderer);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}
