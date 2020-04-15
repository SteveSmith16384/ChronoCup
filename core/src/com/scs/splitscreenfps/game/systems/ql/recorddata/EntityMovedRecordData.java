package com.scs.splitscreenfps.game.systems.ql.recorddata;

import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;

public class EntityMovedRecordData extends AbstractRecordData {

	public AbstractEntity entity; // todo - store actual entity!
	public Vector3 position = new Vector3();
	public float direction;
	
	public EntityMovedRecordData(AbstractEntity _entityId, float _time, Vector3 pos, float dir) {
		super(CMD_MOVED, _time);
		entity = _entityId;
		position.set(pos);
		direction = dir;
	}
	

}
