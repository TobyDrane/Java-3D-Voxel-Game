package com.idkstudios.game.items.tools.stone;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class StonePickaxe extends Tool {

	public StonePickaxe() {
		super("stone_pickaxe", BlockClass.STONE, Material.STONE,
				new Vec2i(1, 6), 7.0f);
	}
}
