package com.idkstudios.game.items.tools;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class BedrockShovel extends Tool {

	public BedrockShovel() {
		super("bedrock_shovel", BlockClass.SAND, Material.BEDROCK, new Vec2i(2,
				16), 7.0f);
	}
}
