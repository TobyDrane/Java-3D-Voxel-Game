package com.idkstudios.game.rendering.particles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import com.idkstudios.game.GameObject;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.game.TextureStorage;
import com.idkstudios.game.math.MathHelper;
import com.idkstudios.game.math.Vec3f;

public class Smoke extends GameObject {

	private static final int CALL_LIST_BASE;
	private static final int PARTICLE_COUNT;
	private static final Vec3f UP_VECTOR;
	private static final Texture PARTICLES_TEXTURE;

	static {
		UP_VECTOR = new Vec3f(0.0f, 2.0f, 0.0f);
		PARTICLE_COUNT = 8;
		float PARTICLE_SIZE = 0.1f;
		PARTICLES_TEXTURE = TextureStorage.getTexture("particles");

		CALL_LIST_BASE = GL11.glGenLists(PARTICLE_COUNT);

		float tileSize = 16.0f / PARTICLES_TEXTURE.getImageWidth();

		for (int i = 0; i < PARTICLE_COUNT; ++i) {
			GL11.glNewList(CALL_LIST_BASE + i, GL11.GL_COMPILE);
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glTexCoord2f(i * tileSize, 0.0f);
			GL11.glVertex2f(-PARTICLE_SIZE, PARTICLE_SIZE);

			GL11.glTexCoord2f((i + 1) * tileSize, 0.0f);
			GL11.glVertex2f(PARTICLE_SIZE, PARTICLE_SIZE);

			GL11.glTexCoord2f((i + 1) * tileSize, tileSize);
			GL11.glVertex2f(PARTICLE_SIZE, -PARTICLE_SIZE);

			GL11.glTexCoord2f(i * tileSize, tileSize);
			GL11.glVertex2f(-PARTICLE_SIZE, -PARTICLE_SIZE);

			GL11.glEnd();
			GL11.glEndList();
		}
	}

	private List<Particle> particles;
	private Vec3f origin;
	private Vec3f vector;
	private Vec3f playerPosition;

	private static class Particle {
		public Vec3f position;
		public float size;
		public float speed;
		public float rotation;
		public float rotationSpeed;
		public int texture;

		public Particle(float x, float y, float z, float size) {
			position = new Vec3f(x, y, z);
			this.size = size;
		}
	}

	public Smoke(float x, float y, float z) {
		particles = new ArrayList<Particle>();
		origin = new Vec3f(x, y, z);
		vector = new Vec3f();
	}

	public void addParticle() {
		float size = (float) (Math.random() * 0.2d + 0.2d);
		Particle p = new Particle(origin.x(), origin.y(), origin.z(), size);
		p.rotationSpeed = (float) (Math.random() * 40.0d - 20.0d);
		p.texture = PARTICLE_COUNT - 1;
		particles.add(p);
	}

	@Override
	public void update() {

		float step = Game.getInstance().getStep();
		for (int i = 0; i < particles.size(); ++i) {
			Particle p = particles.get(i);
			p.position.addFactor(UP_VECTOR, p.speed * step);
			p.speed += step * 0.8f;
			p.rotation += p.rotationSpeed * step;
			p.size -= step * 0.4f;
			p.texture = MathHelper.clamp((int) (p.size * 20.0f), 0,
					PARTICLE_COUNT - 1);

			if (p.size <= 0.0f) {
				/* Remove this particle */
				particles.remove(i--);
			}
		}
	}

	@Override
	public void render() {
		if (playerPosition == null) {
			playerPosition = Game.getInstance().getWorld().getActivePlayer()
					.getPosition();
		}

		PARTICLES_TEXTURE.bind();

		/* Compute the angle of rotation around the Y axis to the player */
		vector.set(origin);
		vector.sub(playerPosition);
		float angle = MathHelper.atan2(vector.z(), vector.x())
				- (MathHelper.f_PI * 0.5f);

		for (int i = 0; i < particles.size(); ++i) {
			Particle p = particles.get(i);

			/* Prepare the model matrix */
			GL11.glPushMatrix();
			GL11.glTranslatef(p.position.x(), p.position.y(), p.position.z());
			GL11.glRotatef(p.rotation, 0, 0, 1);
			GL11.glRotatef(-MathHelper.toDegrees(angle), 0, 1, 0);

			/* Draw all the particles */
			GL11.glCallList(CALL_LIST_BASE + p.texture);

			/* Restore the original matrix */
			GL11.glPopMatrix();
		}
	}
}
