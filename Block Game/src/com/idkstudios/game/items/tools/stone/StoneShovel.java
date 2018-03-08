package com.idkstudios.game.items.tools.stone;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class StoneShovel extends Tool {

	public StoneShovel() {
		super("stone_shovel", BlockClass.SAND, Material.STONE, new Vec2i(1, 5),
				8.0f);
	}
}
