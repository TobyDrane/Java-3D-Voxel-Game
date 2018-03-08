package com.idkstudios.game.items.tools.wood;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class WoodenAxe extends Tool {

	public WoodenAxe() {
		super("wooden_axe", BlockClass.WOOD, Material.WOOD, new Vec2i(0, 7),
				7.2f);
	}
}
