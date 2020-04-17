package com.scs.splitscreenfps.game.systems.ql.recorddata;

import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;

public class BulletFiredRecordData extends AbstractRecordData {

	//public AbstractEntity shooter;
	public int playerIdx;
	public Vector3 start;
	public Vector3 offset;
	
	public BulletFiredRecordData(int phase, float time, int _playerIdx, Vector3 _start, Vector3 _offset) {
		super(CMD_BULLET_FIRED, phase, time);
		
		playerIdx = _playerIdx;
		//shooter = _shooter;
		start = _start;
		offset = _offset;
	}
}
