package com.scs.splitscreenfps.game.gamemodes;

import com.scs.basicecs.AbstractEntity;

public interface IScoreSystem {
	
	AbstractEntity getWinningPlayer();
	
	void playerIsOnPoint(int side);

	String getHudText(int side);
}
