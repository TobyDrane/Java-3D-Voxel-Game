package com.idkstudios.game.items.tools;

import com.idkstudios.game.blocks.BlockType.BlockClass;
import com.idkstudios.game.items.Tool;
import com.idkstudios.game.math.Vec2i;

public class BedrockAxe extends Tool {

	public BedrockAxe() {
		super("bedrock_axe", BlockClass.WOOD, Material.BEDROCK,
				new Vec2i(1, 16), 7.0f);
	}
}
