package com.scs.splitscreenfps.game.levels;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.entities.PlayersAvatar_Person;

import ssmith.libgdx.GridPoint2Static;

public abstract class AbstractLevel {

	protected Game game;
	protected int map_width;
	protected int map_height;
	protected List<GridPoint2Static> startPositions = new ArrayList<GridPoint2Static>();
	
	public AbstractLevel(Game _game) {
		game = _game;
	}
	
	
	public void loadAvatars() {
		for (int i=0 ; i<game.players.length ; i++) {
			game.players[i] = new PlayersAvatar_Person(game, i, game.viewports[i], game.inputs.get(i), i);
			game.ecs.addEntity(game.players[i]);
		}	
	}
	
	
	public void setBackgroundColour() {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
	}
	
	
	/**
	 * This will get called at the start and also every time the screen is resized.
	 */
	public void loadAssets() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SHOWG.TTF"));
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = Gdx.graphics.getBackBufferHeight()/30;
		//Settings.p("Font size=" + parameter.size);
		game.font_small = generator.generateFont(parameter);
		
		parameter = new FreeTypeFontParameter();
		parameter.size = Gdx.graphics.getBackBufferHeight()/20;
		//Settings.p("Font size=" + parameter.size);
		game.font_med = generator.generateFont(parameter);
		
		parameter = new FreeTypeFontParameter();
		parameter.size = Gdx.graphics.getBackBufferHeight()/10;
		//Settings.p("Font size=" + parameter.size);
		game.font_large = generator.generateFont(parameter);
		
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}

	public abstract void load();
	
	public abstract void startGame();
	
	public void setupAvatars(AbstractEntity player, int playerIdx) {}
	
	public abstract void addSystems(BasicECS ecs); // todo - remove this

	public abstract void update();
	
	public void renderHelp(SpriteBatch batch2d, int viewIndex) {
		game.font_med.setColor(1, 1, 1, 1);
		game.font_med.draw(batch2d, "Sorry, there is currently no help for this game", 10, game.font_med.getLineHeight()*2);
	
	}
	
	public void renderUI(SpriteBatch batch, int viewIndex) {}

	public GridPoint2Static getPlayerStartMap(int idx) {
		return this.startPositions.get(idx);
	}
	
	public String getMusicFilename() {
		// Override if required
		return null;
	}
}
