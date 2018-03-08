package com.idkstudios.game.world.generators;

import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.world.ChunkManager;

public class Generator {

	protected ChunkManager chunkManager;
	protected BlockManager blockManager;
	protected final long worldSeed;

	public Generator() {
		chunkManager = Game.getInstance().getWorld().getChunkManager();
		blockManager = BlockManager.getInstance();
		worldSeed = Game.getInstance().getWorld().getWorldSeed();
	}
}
