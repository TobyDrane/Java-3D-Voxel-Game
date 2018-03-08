package com.idkstudios.game.items;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.opengl.Texture;

import com.idkstudios.game.inventory.InventoryItem;
import com.idkstudios.game.math.Vec2f;
import com.idkstudios.game.math.Vec2i;

public abstract class TexturedItem extends InventoryItem {

	protected Vec2i texturePosition;
	protected Texture texture;
	protected Vec2f texPosUpLeft;
	protected Vec2f texPosDownRight;

	public TexturedItem(String name, float animationSpeed, Texture texture,
			Vec2i texPos) {
		super(name, animationSpeed);
		this.texture = texture;
		this.texturePosition = texPos;

		this.texPosUpLeft = new Vec2f((float) 16.0f * texturePosition.x()
				/ texture.getImageWidth(), (float) 16.0f * texturePosition.y()
				/ texture.getImageHeight());
		this.texPosDownRight = new Vec2f(texPosUpLeft.x() + 0.0624f,
				texPosUpLeft.y() + (16.0f / texture.getImageHeight()));

	}

	@Override
	public float calcDamageFactorToBlock(byte block) {
		return 1.1f;
	}

	@Override
	public float calcDamageInflictedByBlock(byte block) {
		return 0.0f;
	}

	@Override
	public short getInventoryTypeID() {
		int w = texture.getImageWidth() / 16;
		return (short) (ItemManager.ITEM_OFFSET + (texturePosition.y() * w + texturePosition
				.x()));
	}

	@Override
	public void renderInventoryItem() {
		glEnable(GL_TEXTURE_2D);
		/* Render the texture */

		float hw = 16f;
		float hh = 16f;

		texture.bind();

		glBegin(GL_QUADS);
		glTexCoord2f(texPosUpLeft.x(), texPosUpLeft.y());
		glVertex3f(-hw, hh, 0.0f);
		glTexCoord2f(texPosDownRight.x(), texPosUpLeft.y());
		glVertex3f(hw, hh, 0.0f);
		glTexCoord2f(texPosDownRight.x(), texPosDownRight.y());
		glVertex3f(hw, -hh, 0.0f);
		glTexCoord2f(texPosUpLeft.x(), texPosDownRight.y());
		glVertex3f(-hw, -hh, 0.0f);
		glEnd();
	}

	@Override
	public float getHealth() {
		return 1.0f;
	}

	@Override
	public abstract boolean isStackable();

	@Override
	public void inflictDamage(float toolDamage) {

	}

	@Override
	public abstract void update();
}
