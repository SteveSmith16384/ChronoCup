package com.scs.splitscreenfps.game.components.ql;

import com.scs.basicecs.AbstractEntity;

public class IsRecordable {

	public String name;
	//public AbstractEntity entity; // Entity to record against
	public int playerIdx;
	public IsRecordable(int _playerIdx) {//String _name, AbstractEntity _entity) {
		playerIdx = _playerIdx;
		//name = _name;
		/*entity = _entity;
		
		if (entity == null) {
			throw new RuntimeException("Null entity!");
		}*/
	}
	
}
