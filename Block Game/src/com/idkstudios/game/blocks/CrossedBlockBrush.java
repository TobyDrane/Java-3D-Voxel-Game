/*******************************************************************************
 * Copyright 2012 Martijn Courteaux <martijn.courteaux@skynet.be>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.idkstudios.game.blocks;

import static com.idkstudios.game.rendering.ChunkMeshBuilder.*;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import com.idkstudios.game.game.TextureStorage;
import com.idkstudios.game.math.Vec2f;
import com.idkstudios.game.math.Vec3f;
import com.idkstudios.game.world.LightBuffer;

public class CrossedBlockBrush extends BlockBrush {
	
	private Vec2f texturePosition;
	private Vec2f textureSize;
	private Vec3f position;
	private int callList;
	private Texture terrain;

	public CrossedBlockBrush() {
		texturePosition = new Vec2f();
		textureSize = new Vec2f();
		position = new Vec3f();
		terrain = TextureStorage.getTexture("terrain");
	}

	@Override
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public void setTexturePosition(int x, int y) {
		texturePosition.set(x * 16f / terrain.getImageWidth(), y * 16f
				/ terrain.getImageHeight());
		textureSize.set(1.0f / 16.0f, 1.0f / 16.0f);
	}

	@Override
	public void render(LightBuffer lightBuffer) {
		if (callList != 0) {
			terrain.bind();

			float light = lightBuffer.get(1, 1, 1) / 30.001f;

			/* Texture 11,10 -> 11,15 */
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			// GL11.glDepthFunc(GL11.GL_ALWAYS);
			GL11.glPushMatrix();
			GL11.glTranslatef(position.x() + 0.5f, position.y() + 0.5f,
					position.z() + 0.5f);
			GL11.glColor3f(light, light, light);

			GL11.glCallList(callList);

			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Override
	public void create() {
		callList = GL11.glGenLists(1);

		GL11.glNewList(callList, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(texturePosition.x(), texturePosition.y());
		GL11.glVertex3f(-0.5f, 0.5f, -0.5f);
		GL11.glTexCoord2f(texturePosition.x() + textureSize.x(),
				texturePosition.y());
		GL11.glVertex3f(0.5f, 0.5f, 0.5f);
		GL11.glTexCoord2f(texturePosition.x() + textureSize.x(),
				texturePosition.y() + textureSize.y());
		GL11.glVertex3f(0.5f, -0.5f, 0.5f);
		GL11.glTexCoord2f(texturePosition.x(), texturePosition.y()
				+ textureSize.y());
		GL11.glVertex3f(-0.5f, -0.5f, -0.5f);

		GL11.glTexCoord2f(texturePosition.x(), texturePosition.y());
		GL11.glVertex3f(0.5f, 0.5f, -0.5f);
		GL11.glTexCoord2f(texturePosition.x() + textureSize.x(),
				texturePosition.y());
		GL11.glVertex3f(-0.5f, 0.5f, 0.5f);
		GL11.glTexCoord2f(texturePosition.x() + textureSize.x(),
				texturePosition.y() + textureSize.y());
		GL11.glVertex3f(-0.5f, -0.5f, 0.5f);
		GL11.glTexCoord2f(texturePosition.x(), texturePosition.y()
				+ textureSize.y());
		GL11.glVertex3f(0.5f, -0.5f, -0.5f);
		GL11.glEnd();
		GL11.glEndList();
	}

	@Override
	public void release() {
		GL11.glDeleteLists(callList, 1);
	}

	@Override
	public int getVertexCount() {
		return 8;
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, float x, float y, float z,
			LightBuffer lightBuffer) {
		byte light = lightBuffer.get(1, 1, 1);

		/* Blade 0 */
		put3f(vbo, x - 0.5f, y + 0.5f, z - 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x(), texturePosition.y());

		put3f(vbo, x + 0.5f, y + 0.5f, z + 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x() + textureSize.x(),
				texturePosition.y());

		put3f(vbo, x + 0.5f, y - 0.5f, z + 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x() + textureSize.x(),
				texturePosition.y() + textureSize.y());

		put3f(vbo, x - 0.5f, y - 0.5f, z - 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x(),
				texturePosition.y() + textureSize.y());

		/* Blade 1 */
		put3f(vbo, x + 0.5f, y + 0.5f, z - 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x(), texturePosition.y());

		put3f(vbo, x - 0.5f, y + 0.5f, z + 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x() + textureSize.x(),
				texturePosition.y());

		put3f(vbo, x - 0.5f, y - 0.5f, z + 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x() + textureSize.x(),
				texturePosition.y() + textureSize.y());

		put3f(vbo, x + 0.5f, y - 0.5f, z - 0.5f);
		putColorWithLight(vbo, COLOR_WHITE, light);
		put2f(vbo, texturePosition.x(),
				texturePosition.y() + textureSize.y());

	}

	public Vec2f getTexturePosition() {
		return texturePosition;
	}

}
