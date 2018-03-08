package com.idkstudios.game.blocks.customblocks;

import com.idkstudios.game.Side;
import com.idkstudios.game.blocks.Block;
import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.BlockType;
import com.idkstudios.game.blocks.CrossedBlock;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;

public class RedstoneTorch extends CrossedBlock implements RedstoneLogic {

	private int connectionCount;
	private boolean[] connections;

	public RedstoneTorch(Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType("redstone_torch"), chunk,
				pos);
		addToVisibilityList();

		connections = new boolean[6];

	}

	@Override
	public void feed(int power) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unfeed(int power) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPowered() {
		return true;
	}

	@Override
	public void connect(Side side) {
		if (!connections[side.ordinal()]) {
			connectionCount++;
		}
		connections[side.ordinal()] = true;
		if (isPowered()) {
			feedNeighbor(side, MAXIMUM_REDSTONE_TRAVELING_DISTANCE);
		}
	}

	@Override
	public void disconnect(Side side) {
		if (connections[side.ordinal()]) {
			connectionCount--;
			connections[side.ordinal()] = false;
			if (isPowered()) {
				unfeedNeighbor(side, MAXIMUM_REDSTONE_TRAVELING_DISTANCE);
			}
		}
	}

	private void feedNeighbor(Side side, int power) {
		Vec3i n = side.getNormal();
		Block bl = chunk.getSpecialBlockAbsolute(getX() + n.x(),
				getY() + n.y(), getZ() + n.z());
		if (bl == null) {
			return;
		}
		if (bl instanceof RedstoneLogic) {
			RedstoneLogic rl = (RedstoneLogic) bl;
			rl.feed(power);
		}
	}

	private void unfeedNeighbor(Side side, int power) {
		Vec3i n = side.getNormal();
		Block bl = chunk.getSpecialBlockAbsolute(getX() + n.x(),
				getY() + n.y(), getZ() + n.z());
		if (bl == null) {
			return;
		}
		if (bl instanceof RedstoneLogic) {
			RedstoneLogic rl = (RedstoneLogic) bl;
			rl.unfeed(power);
		}
	}

	@Override
	public void neighborChanged(Side side) {
		super.neighborChanged(side);
		Vec3i n = side.getNormal();
		byte bType = chunk.getBlockTypeAbsolute(getX() + n.x(),
				getY() + n.y(), getZ() + n.z(), false, false, false);
		if (bType <= 0) {
			disconnect(side);
			return;
		}
		BlockType type = BlockManager.getInstance().getBlockType(bType);
		if (type.hasRedstoneLogic()) {
			Block block = chunk.getSpecialBlockAbsolute(getX() + n.x(), getY()
					+ n.y(), getZ() + n.z());
			RedstoneLogic rl = (RedstoneLogic) block;
			rl.connect(Side.getOppositeSide(side));
			connect(side);
		} else {
			disconnect(side);
		}
	}

	@Override
	public void destruct() {
		disconnect(Side.BACK);
		disconnect(Side.FRONT);
		disconnect(Side.LEFT);
		disconnect(Side.RIGHT);
		disconnect(Side.TOP);
		disconnect(Side.BOTTOM);
	}

}
