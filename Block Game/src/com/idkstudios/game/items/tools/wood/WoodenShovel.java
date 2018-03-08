package com.idkstudios.game.items.tools.wood;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class WoodenShovel extends Tool {

	public WoodenShovel() {
		super("wooden_shovel", BlockClass.SAND, Material.WOOD, new Vec2i(0, 5),
				8.0f);
	}
}
