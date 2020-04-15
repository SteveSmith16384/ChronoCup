package com.scs.splitscreenfps.game.components.ql;

import com.scs.basicecs.AbstractEntity;

public class IsRecordable {

	public String name;
	public AbstractEntity entity; // Entity to record against
	
	public IsRecordable(String _name, AbstractEntity _entity) {
		name = _name;
		entity = _entity;
		
		if (entity == null) {
			throw new RuntimeException("Null entity!");
		}
	}
	
}
