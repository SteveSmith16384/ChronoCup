package com.scs.splitscreenfps.game.levels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.BillBoardFPS_Main;
import com.scs.splitscreenfps.Settings;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.MapData;
import com.scs.splitscreenfps.game.components.HasGuiSpriteComponent;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.IsRecordable;
import com.scs.splitscreenfps.game.components.ql.QLCanShoot;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;
import com.scs.splitscreenfps.game.components.ql.RemoveAtEndOfPhase;
import com.scs.splitscreenfps.game.data.MapSquare;
import com.scs.splitscreenfps.game.entities.Floor;
import com.scs.splitscreenfps.game.entities.Wall;
import com.scs.splitscreenfps.game.entities.ql.QuantumLeagueEntityFactory;
import com.scs.splitscreenfps.game.systems.ql.QLBulletSystem;
import com.scs.splitscreenfps.game.systems.ql.QLPhaseSystem;
import com.scs.splitscreenfps.game.systems.ql.QLRecordAndPlaySystem;
import com.scs.splitscreenfps.game.systems.ql.QLShootingSystem;
import com.scs.splitscreenfps.game.systems.ql.StandOnPointSystem;

import ssmith.libgdx.GridPoint2Static;

public class QuantumLeagueLevel extends AbstractLevel {

	public static Properties prop;

	private List<String> instructions = new ArrayList<String>(); 
	public QLPhaseSystem qlPhaseSystem;
	public QLRecordAndPlaySystem qlRecordAndPlaySystem;
	private final AbstractEntity[][] shadows; // Player, phase
	public GridPoint2Static spot; // todo - rename
	public float[] timeOnPoint = new float[game.players.length];

	public QuantumLeagueLevel(Game _game) {
		super(_game);

		prop = new Properties();
		/*try {
			prop.load(new FileInputStream("quantumleague/ql_config.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		instructions.add("Todo");

		this.shadows = new AbstractEntity[game.players.length][2];

		this.qlPhaseSystem = new QLPhaseSystem(this);
		this.qlRecordAndPlaySystem = new QLRecordAndPlaySystem(game.ecs, this);
	}


	public AbstractEntity getShadow(int playerIdx, int phase) {
		return this.shadows[playerIdx][phase];
	}


	@Override
	public void setupAvatars(AbstractEntity player, int playerIdx) {
		player.addComponent(new QLPlayerData(playerIdx));

		// Todo - move to where we know size of the map
		Camera cam = game.players[playerIdx].camera;
		cam.lookAt(7,  0.4f,  7);
		cam.update();

		// Add crosshairs
		/*Texture weaponTex = new Texture(Gdx.files.internal("quantumleague/crosshairs.png"));		
		Sprite sprite = new Sprite(weaponTex);
		sprite.setPosition((Gdx.graphics.getWidth()-sprite.getWidth())/2, 0);		
		HasGuiSpriteComponent hgsc = new HasGuiSpriteComponent(sprite, HasGuiSpriteComponent.Z_CARRIED, new Rectangle(0.45f, 0.45f, 0.1f, 0.1f));
		game.players[playerIdx].addComponent(hgsc);
		 */

		// Create shadows ready for phase
		GridPoint2Static start = this.startPositions.get(playerIdx);
		for (int phase=0 ; phase<2 ; phase++) {
			AbstractEntity shadow = QuantumLeagueEntityFactory.createShadow(game.ecs, playerIdx, phase, start.x, start.y);
			this.shadows[playerIdx][phase] = shadow;
		}

		player.addComponent(new IsRecordable(playerIdx));//"Player " + playerIdx + "_recordable", this.shadows[playerIdx][0]));
		player.addComponent(new QLCanShoot());
	}


	@Override
	public void setBackgroundColour() {
		Gdx.gl.glClearColor(.6f, .6f, 1, 1);
	}


	public float getCurrentPhaseTime() {
		return this.qlPhaseSystem.getCurrentPhaseTime();
	}


	@Override
	public void load() {
		if (Settings.SMALL_MAP) {
			loadMapFromFile("quantumleague/map_small.csv");
		} else {
			loadMapFromFile("quantumleague/map1.csv");
		}
	}


	private void loadMapFromFile(String file) {
		String str = Gdx.files.internal(file).readString();
		String[] str2 = str.split("\n");

		this.map_width = str2[0].split("\t").length;
		this.map_height = str2.length;

		game.mapData = new MapData(map_width, map_height);

		int row = 0;
		for (String s : str2) {
			s = s.trim();
			if (s.length() > 0 && s.startsWith("#") == false) {
				String cells[] = s.split("\t");
				for (int col=0 ; col<cells.length ; col++) {
					game.mapData.map[col][row] = new MapSquare(game.ecs);

					String cell = cells[col];
					String tokens[] = cell.split(Pattern.quote("+"));
					for (String token : tokens) {
						if (token.equals("S")) { // Start pos
							this.startPositions.add(new GridPoint2Static(col, row));
							Floor floor = new Floor(game.ecs, "quantumleague/textures/corridor.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
						} else if (token.equals("W")) { // Wall
							game.mapData.map[col][row].blocked = true;
							Wall wall = new Wall(game.ecs, "quantumleague/textures/ufo2_03.png", col, 0, row, false);
							game.ecs.addEntity(wall);
						} else if (token.equals("C")) { // Chasm
							game.mapData.map[col][row].blocked = true;
						} else if (token.equals("F")) { // Floor
							Floor floor = new Floor(game.ecs, "quantumleague/textures/corridor.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
						} else if (token.equals("G")) { // Goal point
							Floor floor = new Floor(game.ecs, "quantumleague/textures/centre.png", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
							spot = new GridPoint2Static(col, row);
						} else if (token.equals("B")) { // Border
							game.mapData.map[col][row].blocked = true;
							Wall wall = new Wall(game.ecs, "quantumleague/textures/mjst_metal_beamwindow_diffuse.png", col, 0, row, false);
							game.ecs.addEntity(wall);
						} else {
							throw new RuntimeException("Unknown cell type: " + token);
						}
					}
				}
				row++;
			}
		}
	}


	@Override
	public void addSystems(BasicECS ecs) {
		ecs.addSystem(new QLBulletSystem(ecs, game));
		ecs.addSystem(new QLShootingSystem(ecs, game, this));
		ecs.addSystem(new StandOnPointSystem(ecs, this));

	}


	@Override
	public void update() {
		game.ecs.processSystem(QLBulletSystem.class);
		game.ecs.processSystem(QLShootingSystem.class);
		game.ecs.processSystem(StandOnPointSystem.class);

		qlRecordAndPlaySystem.process(); // Must be before phase system!
		this.qlPhaseSystem.process();
	}


	public void renderUI(SpriteBatch batch2d, int viewIndex) {
		float yOff = game.font_med.getLineHeight() * 1.2f;

		game.font_med.setColor(1, 1, 1, 1);
		game.font_med.draw(batch2d, "In-Game?: " + this.qlPhaseSystem.isGamePhase(), 10, (yOff*2));
		game.font_med.draw(batch2d, "Time: " + (int)(this.getCurrentPhaseTime()), 10, (yOff*3));
		game.font_med.draw(batch2d, "Phase: " + (int)(this.qlPhaseSystem.getPhaseNum012()), 10, (yOff*4));

		QLPlayerData playerData = (QLPlayerData)game.players[viewIndex].getComponent(QLPlayerData.class);
		game.font_med.draw(batch2d, "Health: " + (int)(playerData.health), 10, (yOff*5));
		game.font_med.draw(batch2d, "Score: " + (int)(this.timeOnPoint[playerData.side]), 10, (yOff*6));
		/*
		int x = (int)game.viewports[viewIndex].viewPos.getCenterX();
		int y = (int)game.viewports[viewIndex].viewPos.getCenterY();
		//crosshairs.setPosition(x, y);
		//batch2d.draw(crosshairs, x, y);
		crosshairs.draw(batch2d);
		 */
	}


	public boolean isGamePhase() {
		return this.qlPhaseSystem.isGamePhase();
	}


	public void startRewindPhase() {
		// Remove relevant entities
		Iterator<AbstractEntity> it = game.ecs.getEntityIterator();
		while (it.hasNext()) {
			AbstractEntity e = it.next();
			if (e.getComponent(RemoveAtEndOfPhase.class) != null) {
				e.remove();
			}
		}

		this.qlRecordAndPlaySystem.startRewind();

		BillBoardFPS_Main.audio.stopMusic();

		BillBoardFPS_Main.audio.startMusic("sfx/Replenish.wav");

	}


	public static void setAvatarColour(AbstractEntity e, boolean alive) {
		// Reset player colours
		HasModelComponent hasModel = (HasModelComponent)e.getComponent(HasModelComponent.class);
		ModelInstance instance = hasModel.model;
		for (int i=0 ; i<instance.materials.size ; i++) {
			if (alive) {
				instance.materials.get(i).set(ColorAttribute.createDiffuse(Color.BLACK));
				instance.materials.get(i).set(ColorAttribute.createAmbient(Color.BLACK));
			} else {
				instance.materials.get(i).set(ColorAttribute.createDiffuse(Color.WHITE));
				instance.materials.get(i).set(ColorAttribute.createAmbient(Color.WHITE));
			}
		}
	}


	public void nextGamePhase() {
		BillBoardFPS_Main.audio.startMusic("sfx/fight.wav");

		this.qlRecordAndPlaySystem.loadNewRecordData();
		this.qlPhaseSystem.startGamePhase();

		for (int playerIdx=0 ; playerIdx<game.players.length ; playerIdx++) {
			// Reset all health
			QLPlayerData playerData = (QLPlayerData)game.players[playerIdx].getComponent(QLPlayerData.class);
			playerData.health = 100;
			setAvatarColour(game.players[playerIdx], true);

			for (int phase = 0 ; phase<2 ; phase++) {
				playerData = (QLPlayerData)this.shadows[playerIdx][phase].getComponent(QLPlayerData.class);
				playerData.health = 100;
				setAvatarColour(this.shadows[playerIdx][phase], true);
			}


			// Add shadow avatars to ECS
			if (this.qlPhaseSystem.getPhaseNum012() > 0) {
				AbstractEntity shadow = this.shadows[playerIdx][this.qlPhaseSystem.getPhaseNum012()-1];
				game.ecs.addEntity(shadow);
			}

			// Move players avatars back to start
			GridPoint2Static start = this.startPositions.get(playerIdx);
			PositionComponent posData = (PositionComponent)game.players[playerIdx].getComponent(PositionComponent.class);
			posData.position.x = start.x + 0.5f;
			posData.position.z = start.y + 0.5f;
		}
	}


	public void allPhasesOver() {
		this.game.ecs.removeSystem(QLPhaseSystem.class);
		this.game.ecs.removeSystem(QLRecordAndPlaySystem.class);

		if (this.timeOnPoint[0] == this.timeOnPoint[1]) {
			game.playerHasWon(null);
		} else if (this.timeOnPoint[0] < this.timeOnPoint[1]) {
			game.playerHasWon(game.players[1]);
		} else {
			game.playerHasWon(game.players[0]);
		}
	}


	@Override
	public void startGame() {
		this.qlPhaseSystem.startGamePhase();

		BillBoardFPS_Main.audio.startMusic("sfx/fight.wav");

	}


}