package com.scs.splitscreenfps.game.gamemodes;

import com.scs.basicecs.AbstractEntity;
import com.scs.splitscreenfps.game.Game;

public class LastPlayerOnPointScoreSystem implements IScoreSystem {

	private Game game;
	public int last_side_on_point = -1;
	
	public LastPlayerOnPointScoreSystem(Game _game) {
		game = _game;
	}
	

	@Override
	public AbstractEntity getWinningPlayer() {
		if (this.last_side_on_point >= 0) {
			return game.players[this.last_side_on_point];
		}
		return null;
	}


	@Override
	public void playerIsOnPoint(int side) {
		this.last_side_on_point = side; // todo - don't change straight away
	}


	@Override
	public String getHudText(int side) {
		return side == this.last_side_on_point ? "ON POINT!" : "Off Point";
	}
}
