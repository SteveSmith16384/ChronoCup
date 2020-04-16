package com.scs.splitscreenfps.game.systems;

import com.scs.basicecs.ISystem;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.levels.QuantumLeagueLevel;

public class PlayerInputSystem implements ISystem {

	private Game game;

	public PlayerInputSystem(Game _game) {
		game = _game;
	}


	@Override
	public void process() {
		QuantumLeagueLevel level = (QuantumLeagueLevel)game.currentLevel;
		//if (level.qlPhaseSystem.isGamePhase()) { todo - stop rewind working?
			for (int i=0 ; i<game.players.length ; i++) {
				if (game.players[i] != null) {
					game.players[i].update();
				}
			}
		//}
	}

}
