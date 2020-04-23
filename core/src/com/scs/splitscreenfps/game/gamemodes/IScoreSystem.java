package com.scs.splitscreenfps.game.gamemodes;

import com.scs.basicecs.AbstractEntity;

public interface IScoreSystem {
	
	int getWinningPlayer();
	
	void playerIsOnPoint(int side);

	String getHudText(int side);
}
