package com.idkstudios.game.items.tools.stone;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class StoneHoe extends Tool {

	public StoneHoe() {
		super("stone_hoe", BlockClass.SAND, Material.STONE, new Vec2i(1, 8),
				8.0f);
	}
}
