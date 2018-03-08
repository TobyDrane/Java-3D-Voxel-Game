package com.idkstudios.game.math;

import java.util.Comparator;

import com.idkstudios.game.blocks.Block;

public class BlockDistanceComparator implements Comparator<Block> {

	private Vec3f _origin;
	private Vec3f _blockVector;
	private int _id;

	public BlockDistanceComparator() {
		_blockVector = new Vec3f();
	}

	public void newID() {
		_id++;
		if (_id == 10000) {
			_id = 0;
		}
	}

	public void setOrigin(Vec3f origin) {
		_origin = origin;
	}

	private Vec3f relativeToOrigin(Vec3f point) {
		return _blockVector.set(point).sub(_origin);
	}

	public int compare(Block o1, Block o2) {
		if (o1.distanceID != _id) {
			o1.distanceID = _id;
			o1.distance = relativeToOrigin(o1.getAABB().getPosition())
					.lengthSquared();
		}
		if (o2.distanceID != _id) {
			o2.distanceID = _id;
			o2.distance = relativeToOrigin(o2.getAABB().getPosition())
					.lengthSquared();
		}

		if (o1.distance < o2.distance)
			return 1;
		if (o1.distance > o2.distance)
			return -1;
		return 0;
	}
}
