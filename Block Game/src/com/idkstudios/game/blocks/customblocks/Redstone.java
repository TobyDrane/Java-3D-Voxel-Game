package com.idkstudios.game.blocks.customblocks;

import java.nio.FloatBuffer;

import static com.idkstudios.game.rendering.ChunkMeshBuilder.*;

import com.idkstudios.game.Side;
import com.idkstudios.game.blocks.Block;
import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.blocks.BlockType;
import com.idkstudios.game.datastructures.AABB;
import com.idkstudios.game.game.Game;
import com.idkstudios.game.inventory.InventoryItem;
import com.idkstudios.game.math.Vec2f;
import com.idkstudios.game.math.Vec3f;
import com.idkstudios.game.math.Vec3i;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.Chunk.LightType;
import com.idkstudios.game.world.LightBuffer;

public class Redstone extends Block implements RedstoneLogic {

	private static final float MINIMUM_TEXTURE_SIZE = 3.0f / 16.0f / 16.0f;
	private static final float MAXIMUM_TEXTURE_SIZE = 8.0f / 16.0f / 16.0f;
	private static final float MINIMUM_TERRAIN_SIZE = 3.0f / 16.0f;
	private static final Vec2f TEXTURE_CENTER_CROSS;
	private static final Vec2f TEXTURE_CENTER_LINE;
	private static final float COLOR_INTENSITY_LOOKUP_TABLE[];
	public static final byte LUMINOSITY = 4;

	static {
		TEXTURE_CENTER_CROSS = new Vec2f(4.5f, 10.5f);
		TEXTURE_CENTER_LINE = new Vec2f(5.5f, 10.5f);

		TEXTURE_CENTER_CROSS.scale(16.0f / 256.0f);
		TEXTURE_CENTER_LINE.scale(16.0f / 256.0f);

		COLOR_INTENSITY_LOOKUP_TABLE = new float[MAXIMUM_REDSTONE_TRAVELING_DISTANCE + 1];
		for (int i = 0; i < COLOR_INTENSITY_LOOKUP_TABLE.length; ++i) {
			COLOR_INTENSITY_LOOKUP_TABLE[i] = (float) (1.0f - Math.pow(1.17f, -i - 3));
		}
	}

	private int connectionCount;
	private boolean powered;
	private boolean[] connections;
	private boolean visible;
	private int power;

	public Redstone(Chunk chunk, Vec3i pos) {
		super(BlockManager.getInstance().getBlockType("redstone"), chunk, pos);

		connections = new boolean[6];

		aabb = new AABB(new Vec3f(getPosition()).add(0.5f, 0.02f, 0.5f), new Vec3f(0.5f, 0.02f, 0.5f));
	}

	@Override
	public void feed(int power) {
		if (power <= 0)
			return;
		if (!powered || power > this.power) {
			chunk.needsNewVBO();
			this.power = Math.max(this.power, power);
			powered = true;
			refeedNeighbors();
			/* Spread the light */
			chunk.spreadLight(getX(), getY(), getZ(), LUMINOSITY, LightType.BLOCK);
		}
	}

	public void refeedNeighbors() {
		if (this.power > 1) {
			/* Conduct the power */
			for (int i = 0, j = 0; i < 6 && j < connectionCount; ++i) {
				if (connections[i]) {
					++j;
					feedNeighbor(Side.getSide(i), power - 1);
				}
			}
		}
	}

	@Override
	public void unfeed(int power) {
		if (power <= 0)
			return;
		if (powered) {
			chunk.needsNewVBO();

			if (power == this.power) {
				powered = false;
				for (int i = 0, j = 0; i < 6 && j < connectionCount; ++i) {
					if (connections[i]) {
						++j;
						unfeedNeighbor(Side.getSide(i), this.power - 1);
					}
				}
				/* Unspread the light */
				chunk.unspreadLight(getX(), getY(), getZ(), LUMINOSITY, LightType.BLOCK);
				this.power = 0;
			} else {
				if (power < this.power) {
					Game.getInstance().getWorld().respreadRedstone(getX(), getY(), getZ());
				}
			}
		}
	}

	@Override
	public boolean isPowered() {
		return powered;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(LightBuffer lightBuffer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer) {
		boolean connectedL = connections[Side.LEFT.ordinal()];
		boolean connectedR = connections[Side.RIGHT.ordinal()];
		boolean connectedF = connections[Side.FRONT.ordinal()];
		boolean connectedB = connections[Side.BACK.ordinal()];

		int cons = connectionCount;
		if (connections[Side.TOP.ordinal()]) {
			cons--;
		}
		if (connections[Side.BOTTOM.ordinal()]) {
			cons--;
		}

		float x = getX() + 0.5f;
		float y = getY() + 0.02f;
		float z = getZ() + 0.5f;
		Vec3f v = new Vec3f(1, 1, 1);
		if (!powered) {
			v.scale(0.2f);
		} else {
			v.scale(COLOR_INTENSITY_LOOKUP_TABLE[power]);
		}
		System.out.printf("Store Redstone (%b, %b, %b, %b)%n", connectedL, connectedR, connectedF, connectedB);
		if ((cons == 1 && (connectedL || connectedR)) || (cons == 2 && (connectedL && connectedR))) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);
		} else if ((cons == 1 && (connectedF || connectedB)) || (cons == 2 && (connectedF && connectedB))) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 2 && (connectedL && connectedB)) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (cons == 2 && (connectedR && connectedB)) {
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (cons == 2 && (connectedR && connectedF)) {
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 2 && (connectedL && connectedF)) {
			put3f(vbo, x - 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 3 && (connectedR && connectedL && connectedB)) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (cons == 3 && (connectedR && connectedL && connectedF)) {
			put3f(vbo, x - 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 3 && (connectedR && connectedF && connectedB)) {
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 3 && (connectedL && connectedF && connectedB)) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (cons == 4 || cons == 0) {
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		}

	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public AABB getAABB() {
		return aabb;
	}

	@Override
	public void smash(InventoryItem item) {
		destroy();
	}

	@Override
	public void neighborChanged(Side side) {
		Vec3i n = side.getNormal();
		byte bType = chunk.getBlockTypeAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z(), false, false, false);
		if (bType <= 0) {
			disconnect(side);
			return;
		}
		BlockType type = BlockManager.getInstance().getBlockType(bType);
		if (type.hasRedstoneLogic()) {
			Block block = chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
			RedstoneLogic rl = (RedstoneLogic) block;
			rl.connect(Side.getOppositeSide(side));
			connect(side);
		} else {
			disconnect(side);
		}
	}

	@Override
	public void destruct() {
		if (powered) {
			/* Unspread the light */
			chunk.unspreadLight(getX(), getY(), getZ(), LUMINOSITY, LightType.BLOCK);
		}
		disconnect(Side.BACK);
		disconnect(Side.FRONT);
		disconnect(Side.LEFT);
		disconnect(Side.RIGHT);
		disconnect(Side.TOP);
		disconnect(Side.BOTTOM);
	}

	@Override
	public void checkVisibility() {
		visible = true;
	}

	@Override
	public int getVertexCount() {
		return 4;
	}

	@Override
	public void connect(Side side) {
		if (!connections[side.ordinal()]) {
			connectionCount++;
			connections[side.ordinal()] = true;
			if (isPowered()) {
				feedNeighbor(side, power - 1);
			}
		}
	}

	@Override
	public void disconnect(Side side) {
		if (connections[side.ordinal()]) {
			connectionCount--;
			connections[side.ordinal()] = false;
			if (isPowered()) {
				unfeedNeighbor(side, power - 1);
			}
		}
	}

	private void feedNeighbor(Side side, int power) {
		Vec3i n = side.getNormal();
		Block bl = chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
		if (bl == null) {
			return;
		}
		if (bl instanceof RedstoneLogic) {
			RedstoneLogic rl = (RedstoneLogic) bl;
			rl.feed(power);
		}
	}

	private void unfeedNeighbor(Side side, int power) {
		Vec3i n = side.getNormal();
		Block bl = chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
		if (bl == null) {
			return;
		}
		if (bl instanceof RedstoneLogic) {
			RedstoneLogic rl = (RedstoneLogic) bl;
			rl.unfeed(power);
		}
	}
}
