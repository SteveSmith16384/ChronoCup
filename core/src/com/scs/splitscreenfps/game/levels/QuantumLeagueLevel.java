package com.scs.splitscreenfps.game.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.BillBoardFPS_Main;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.MapData;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.ql.IsRecordable;
import com.scs.splitscreenfps.game.components.ql.QLCanShoot;
import com.scs.splitscreenfps.game.components.ql.QLPlayerData;
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
	private AbstractEntity[][] shadows; // Player, phase

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


	public AbstractEntity getShadow(int player, int phase) {
		return this.shadows[player][phase];
	}


	@Override
	public void setupAvatars(AbstractEntity player, int playerIdx) {
		player.addComponent(new QLPlayerData(playerIdx));

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
		loadMapFromFile("quantumleague/map1.csv");
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
							Floor floor = new Floor(game.ecs, "quantumleague/textures/deploy_sq.png", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
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
		ecs.addSystem(new QLBulletSystem(ecs));
		ecs.addSystem(new QLShootingSystem(ecs, game, this));
		ecs.addSystem(new StandOnPointSystem(ecs));

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
		game.font_med.setColor(1, 1, 1, 1);
		game.font_med.draw(batch2d, "In-Game?: " + this.qlPhaseSystem.isGamePhase(), 10, 30);
		game.font_med.draw(batch2d, "Time: " + (int)(this.getCurrentPhaseTime()), 10, 60);
		game.font_med.draw(batch2d, "Phase: " + (int)(this.qlPhaseSystem.getPhaseNum012()), 10, 90);

		QLPlayerData playerData = (QLPlayerData)game.players[viewIndex].getComponent(QLPlayerData.class);
		game.font_med.draw(batch2d, "Health: " + (int)(playerData.health), 10, 120);
	}


	public boolean isGamePhase() {
		return this.qlPhaseSystem.isGamePhase();
	}


	public void startRewindPhase() {
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

			// Record against next shadow
			/*IsRecordable isRecordable = (IsRecordable)game.players[playerIdx].getComponent(IsRecordable.class);
			if (this.qlPhaseSystem.getPhaseNum012() < 2) { // Only need to record if not last phase
				isRecordable.entity = this.shadows[playerIdx][this.qlPhaseSystem.getPhaseNum012()-1];
			} else {
				isRecordable.entity = null; // No need to record any more.
			}*/

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
		// todo - calc winner and game over
		game.playerHasWon(null);
	}


	@Override
	public void startGame() {
		this.qlPhaseSystem.startGamePhase();

		BillBoardFPS_Main.audio.startMusic("sfx/fight.wav");

	}


}