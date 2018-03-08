package com.idkstudios.game.items.tools.wood;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class WoodenPickaxe extends Tool {

	public WoodenPickaxe() {
		super("wooden_pickaxe", BlockClass.STONE, Material.WOOD,
				new Vec2i(0, 6), 7.5f);
	}
}
