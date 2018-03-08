package com.idkstudios.game.blocks.customblocks;

import java.nio.FloatBuffer;

import com.idkstudios.game.Side;
import com.idkstudios.game.blocks.Block;
import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.BlockType;
import com.idkstudios.game.blocks.DefaultBlock;
import com.idkstudios.game.blocks.DefaultBlockBrush;
import com.idkstudios.game.math.Vec2f;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.Chunk.LightType;
import com.idkstudios.game.world.LightBuffer;

public class RedstoneLamp extends DefaultBlock implements RedstoneLogic {

	private final static DefaultBlockBrush BRUSH_POWERED;
	public static final byte LUMINOSITY = 15;

	static {
		BRUSH_POWERED = new DefaultBlockBrush();
		BRUSH_POWERED.setTexture(new Vec2f(4, 13));
	}

	private boolean powered;

	public RedstoneLamp(Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType("redstone_lamp"), chunk,
				pos);
	}

	@Override
	public void feed(int power) {
		System.out.println("RedstoneLamp power = " + power);
		if (!powered) {
			powered = true;
			chunk.spreadLight(getX(), getY(), getZ(), (byte) 15,
					LightType.BLOCK);
		}
	}

	@Override
	public void unfeed(int power) {
		if (powered) {
			powered = false;
			chunk.unspreadLight(getX(), getY(), getZ(), (byte) 15,
					LightType.BLOCK);
		}
	}

	@Override
	public boolean isPowered() {
		return powered;
	}

	@Override
	public void connect(Side side) {

	}

	@Override
	public void disconnect(Side side) {
	}

	@Override
	public synchronized void neighborChanged(Side side) {
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
		super.destruct();

		if (powered) {
			chunk.unspreadLight(getX(), getY(), getZ(), (byte) 15,
					LightType.BLOCK);
		}
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer) {
		if (!powered) {
			super.storeInVBO(vbo, lightBuffer);
		} else {
			BRUSH_POWERED.setFaceMask(getFaceMask());
			BRUSH_POWERED.storeInVBO(vbo, getX() + 0.5f, getY() + 0.5f,
					getZ() + 0.5f, lightBuffer);
		}
	}

}
