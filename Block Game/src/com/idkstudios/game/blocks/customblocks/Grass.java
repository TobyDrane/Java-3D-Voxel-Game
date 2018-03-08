package com.idkstudios.game.blocks.customblocks;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.BlockType;
import com.idkstudios.game.blocks.DefaultBlock;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;

public class Grass extends DefaultBlock{

	public Grass(BlockType type, Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType("grass"), chunk, pos);
	}
}
