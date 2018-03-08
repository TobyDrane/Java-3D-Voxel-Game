package com.idkstudios.game.items.tools.stone;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class StoneAxe extends Tool {

	public StoneAxe() {
		super("stone_axe", BlockClass.WOOD, Material.STONE, new Vec2i(1, 7),
				7.2f);
	}
}
