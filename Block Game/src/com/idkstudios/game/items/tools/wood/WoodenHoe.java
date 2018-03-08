package com.idkstudios.game.items.tools.wood;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class WoodenHoe extends Tool{

	public WoodenHoe() {
		super("wooden_hoe", BlockClass.SAND, Material.WOOD, new Vec2i(0, 8), 8.0f);
	}
}
