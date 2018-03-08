package com.idkstudios.game.world.generators;

import java.util.Random;

import com.idkstudios.game.blocks.BlockConstructor;
import com.idkstudios.game.blocks.BlockManager;
import com.idkstudios.game.math.MathHelper;
import com.idkstudios.game.utils.SmartRandom;
import com.idkstudios.game.world.Biome;
import com.idkstudios.game.world.Chunk;
import com.idkstudios.game.world.World;
import com.idkstudios.game.world.WorldProvider;
import com.idkstudios.game.world.WorldProvider.TreeDefinition;

public class ChunkGenerator extends Generator {

	private WorldProvider worldProvider;
	private static int SAMPLE_RATE_HORIZONTAL_DENSITY = 2;
	private static int SAMPLE_RATE_VERTICAL_DENSITY = 2;
	private int x;
	private int z;
	private int[][] heightMap;
	private int[][] temperatureMap;
	private int[][] humidityMap;

	public ChunkGenerator(World world, int x, int z) {
		worldProvider = world.getWorldProvider();
		this.x = x;
		this.z = z;
	}

	public Chunk generateChunk() {
		System.out.println("---------- Generate chunk: " + x + ", " + z);

		SmartRandom random = new SmartRandom(new Random(generateSeedForChunk(worldSeed, x, z)));

		/* Access the new chunk */
		Chunk chunk = chunkManager.getChunk(x, z, true, false, false);
		chunk.setGenerated(true);
		chunk.setLoading(true);
		
		System.out.println("");
		
		/* Build the height map */
		heightMap = new int[Chunk.CHUNK_SIZE_HORIZONTAL][Chunk.CHUNK_SIZE_HORIZONTAL];
		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL; ++x) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL; ++z) {
				heightMap[x][z] = worldProvider.getHeightAt(x + chunk.getAbsoluteX(), z + chunk.getAbsoluteZ());
			}
		}

		/* Build the temperature map */
		temperatureMap = new int[Chunk.CHUNK_SIZE_HORIZONTAL][Chunk.CHUNK_SIZE_HORIZONTAL];
		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL; ++x) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL; ++z) {
				temperatureMap[x][z] = worldProvider.getTemperatureAt(x + chunk.getAbsoluteX(), z + chunk.getAbsoluteZ());
			}
		}

		/* Build the humidity map */
		humidityMap = new int[Chunk.CHUNK_SIZE_HORIZONTAL][Chunk.CHUNK_SIZE_HORIZONTAL];
		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL; ++x) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL; ++z) {
				humidityMap[x][z] = worldProvider.getHumidityAt(x + chunk.getAbsoluteX(), z + chunk.getAbsoluteZ());
			}
		}

		/* Build a density map */
		float densityMap[][][] = new float[Chunk.CHUNK_SIZE_HORIZONTAL + 1][Chunk.CHUNK_SIZE_VERTICAL + 1][Chunk.CHUNK_SIZE_HORIZONTAL + 1];

		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL + 1; x += SAMPLE_RATE_HORIZONTAL_DENSITY) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL + 1; z += SAMPLE_RATE_HORIZONTAL_DENSITY) {
				for (int y = 0; y < Chunk.CHUNK_SIZE_VERTICAL + 1; y += SAMPLE_RATE_VERTICAL_DENSITY) {
					densityMap[x][y][z] = generateDensity(random, x, y, z);
				}
			}
		}

		/* Trilerp the density map */
		triLerpDensityMap(densityMap);

		/* Create the blocks using the density map */
		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL; z++) {
				int baseLevel = heightMap[x][z];

				Biome topBiome = worldProvider.calculateBiome(worldProvider.calculateTemperature(temperatureMap[x][z], baseLevel), worldProvider.getHumidityAt(x + chunk.getAbsoluteX(), baseLevel, z + chunk.getAbsoluteZ()));

				for (int y = 0; y < Chunk.CHUNK_SIZE_VERTICAL && y <= baseLevel; y++) {
					if (y < 4) {
						/* Create a bedrock layer */
						chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("bedrock"), (byte) 0, true, false, false);
						continue;
					}
					int depth = baseLevel - y;
					if (topBiome == Biome.DESERT && y >= baseLevel - 3) {
						chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("sand"), (byte) 0, true, false, false);
					} else if (topBiome == Biome.SNOW && y == baseLevel) {
						chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("snow"), (byte) 0, true, false, false);
					} else if ((topBiome == Biome.FOREST || topBiome == Biome.FIELDS) && y == baseLevel) {
						chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("grass"), (byte) 0, true, false, false);
					} else {
						/**
						 * Here you can set what ores can be at what level and
						 * what types of blocks you want for what type of biome
						 * you are in
						 */
						float density = densityMap[x][y][z];
						if (density < 7.3f && depth > 8) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("gravel"), (byte) 0, true, false, false);
						} else if (density < 6.3f) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("dirt"), (byte) 0, true, false, false);
						} else if (density < 9.0f) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("stone"), (byte) 0, true, false, false);
						} else if (density < 9.5f && depth > 5) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("coal_ore"), (byte) 0, true, false, false);
						} else if (density < 10.0f && depth > 10) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("iron_ore"), (byte) 0, true, false, false);
						} else if (y < 16 && density < 13.0f) {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("redstone_ore"), (byte) 0, true, false, false);
						} else {
							chunk.setDefaultBlockRelative(x, y, z, BlockManager.getInstance().getBlockType("stone"), (byte) 0, true, false, false);
						}
					}
				}
			}

		}

		/* Generate trees */
		{
			int treeCount = MathHelper.round((1.0f - random.exponentialRandom(1.0f, 3)) * 12.0f);
			TreeGenerator gen = new TreeGenerator(random.randomLong());
			trees: for (int i = 0; i < treeCount; ++i) {
				int x = chunk.getAbsoluteX() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL);
				int z = chunk.getAbsoluteZ() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL);

				/* Check for enough distance from the other trees */
				for (TreeDefinition treeDef : worldProvider.getTrees()) {
					float xDiff = x - treeDef.x;
					float zDiff = z - treeDef.z;

					float distSq = xDiff * xDiff + zDiff * zDiff;
					if (distSq < 60) {
						continue trees;
					}
				}
				int type = -1;
				int y = heightMap[x - chunk.getAbsoluteX()][z - chunk.getAbsoluteZ()];

				/*
				 * Check if the root of the tree is INSIDE THIS blockchunk, to
				 * prevent generating trees for one chunk multiple times
				 */
				// if (chunk.contains(x, y, z))
				{
					Biome biome = worldProvider.getBiomeAt(x, y, z);
					if (biome == Biome.FIELDS) {
						continue;
					}
					if (biome == Biome.FOREST) {
						gen.generateNiceBroadLeavedTree(chunk, x, y, z);
						// gen.generateBroadLeavedTree(chunk, x, y, z, true);
						type = 0;
					} else if (biome == Biome.DESERT) {
						if (random.randomBoolean()) {
							gen.generateCactus(chunk, x, y + 1, z);

							type = 1;
						}
					} else if (biome == Biome.SNOW) {
						gen.generatePinophyta(chunk, x, y, z);
						type = 2;
					}
					if (type != -1) {
						worldProvider.getTrees().add(new TreeDefinition(x, y, z, type));
					}
				}
			}
		}

		/* Generate flora */
		{
			int grassCount = random.randomInt(5, 10);

			for (int i = 0; i < grassCount; ++i) {
				int x = chunk.getAbsoluteX() + random.randomInt(0, Chunk.CHUNK_SIZE_HORIZONTAL);
				int z = chunk.getAbsoluteZ() + random.randomInt(0, Chunk.CHUNK_SIZE_HORIZONTAL);
				int y = heightMap[x - chunk.getAbsoluteX()][z - chunk.getAbsoluteZ()];

				Biome biome = worldProvider.getBiomeAt(x, y, z);
				if (biome == Biome.FIELDS || biome == Biome.FOREST) {
					if (chunk.getBlockTypeAbsolute(x, y + 1, z, false, false, false) == 0) {
						chunk.setSpecialBlockAbsolute(x, y + 1, z, BlockConstructor.construct(x, y + 1, z, chunk, blockManager.blockID("tallgrass"), (byte) random.randomInt(6)), false, false, false);
					}
				}
			}
		}
		{
			int flowerCount = random.randomInt(5, 10);

			for (int i = 0; i < flowerCount; ++i) {
				int x = chunk.getAbsoluteX() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL);
				int z = chunk.getAbsoluteZ() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL);
				int y = heightMap[x - chunk.getAbsoluteX()][z - chunk.getAbsoluteZ()];

				Biome biome = worldProvider.getBiomeAt(x, y, z);
				if (biome == Biome.FIELDS || biome == Biome.FOREST) {
					if (chunk.getBlockTypeAbsolute(x, y + 1, z, false, false, false) == 0) {
						if (random.randomBoolean()) {
							chunk.setSpecialBlockAbsolute(x, y + 1, z, BlockConstructor.construct(x, y + 1, z, chunk, blockManager.blockID("redflower"), (byte) 0), false, false, false);
						} else {
							chunk.setSpecialBlockAbsolute(x, y + 1, z, BlockConstructor.construct(x, y + 1, z, chunk, blockManager.blockID("yellowflower"), (byte) 0), false, false, false);
						}
					}
				}
			}
		}

		/* Generate some floating islands */
		if (random.randomInt(35) == -1) {
			FloatingIslandGenerator gen = new FloatingIslandGenerator(worldProvider);
			int x = chunk.getAbsoluteX() + random.randomInt(1);// *
																// Chunk.CHUNK_SIZE_HORIZONTAL);
			int z = chunk.getAbsoluteZ() + random.randomInt(1);// *
																// Chunk.CHUNK_SIZE_HORIZONTAL);
			int y = heightMap[x - chunk.getAbsoluteX()][z - chunk.getAbsoluteZ()];

			gen.generateNiceFloatingIsland(chunk, x, y + random.randomInt(40, 80), z);
		}

		/* Generate a structure */
		if (random.randomInt(10) == -1) {
			BuildingGenerator gen = new BuildingGenerator();
			int x = chunk.getAbsoluteX() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL / 2);
			int z = chunk.getAbsoluteZ() + random.randomInt(Chunk.CHUNK_SIZE_HORIZONTAL / 2);
			int y = heightMap[x - chunk.getAbsoluteX()][z - chunk.getAbsoluteZ()] + 1;

			if (worldProvider.getBiomeAt(x, y, z) == Biome.FIELDS) {
				System.out.println("Generate building at (" + x + ", " + y + ", " + z + ")");
				gen.generateHouseAt(chunk, x, y, z);
			}
		}

		/* Make it accessible for the game */
		chunk.setLoading(false);
		/* Make sure the neighbors are assigned correctly */
		chunkManager.assignNeighbors(chunk);

		return chunk;
	}

	/**
	 * Based on the code of Benjamin Glatzel in his project Terasology.
	 * 
	 * @author Benjamin Glatzel
	 * @author martijncourteaux
	 */
	protected void triLerpDensityMap(float[][][] densityMap) {
		for (int x = 0; x < Chunk.CHUNK_SIZE_HORIZONTAL; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE_HORIZONTAL; z++) {
				for (int y = 0; y < heightMap[x][z]; y++) {
					if (!(x % SAMPLE_RATE_HORIZONTAL_DENSITY == 0 && y % SAMPLE_RATE_VERTICAL_DENSITY == 0 && z % SAMPLE_RATE_HORIZONTAL_DENSITY == 0)) {
						int offsetX = (x / SAMPLE_RATE_HORIZONTAL_DENSITY) * SAMPLE_RATE_HORIZONTAL_DENSITY;
						int offsetY = (y / SAMPLE_RATE_VERTICAL_DENSITY) * SAMPLE_RATE_VERTICAL_DENSITY;
						int offsetZ = (z / SAMPLE_RATE_HORIZONTAL_DENSITY) * SAMPLE_RATE_HORIZONTAL_DENSITY;
						densityMap[x][y][z] = MathHelper.triLerp(x, y, z, densityMap[offsetX][offsetY][offsetZ], densityMap[offsetX][SAMPLE_RATE_VERTICAL_DENSITY + offsetY][offsetZ], densityMap[offsetX][offsetY][offsetZ + SAMPLE_RATE_HORIZONTAL_DENSITY], densityMap[offsetX][offsetY + SAMPLE_RATE_VERTICAL_DENSITY][offsetZ + SAMPLE_RATE_HORIZONTAL_DENSITY], densityMap[SAMPLE_RATE_HORIZONTAL_DENSITY + offsetX][offsetY][offsetZ], densityMap[SAMPLE_RATE_HORIZONTAL_DENSITY + offsetX][offsetY + SAMPLE_RATE_VERTICAL_DENSITY][offsetZ], densityMap[SAMPLE_RATE_HORIZONTAL_DENSITY + offsetX][offsetY][offsetZ + SAMPLE_RATE_HORIZONTAL_DENSITY], densityMap[SAMPLE_RATE_HORIZONTAL_DENSITY + offsetX][offsetY + SAMPLE_RATE_VERTICAL_DENSITY][offsetZ + SAMPLE_RATE_HORIZONTAL_DENSITY], offsetX, SAMPLE_RATE_HORIZONTAL_DENSITY + offsetX, offsetY, SAMPLE_RATE_VERTICAL_DENSITY + offsetY, offsetZ, offsetZ + SAMPLE_RATE_HORIZONTAL_DENSITY);
					}
				}
			}
		}
	}

	private float generateDensity(SmartRandom random, int x, int y, int z) {
		int baseLevel;
		if (x >= Chunk.CHUNK_SIZE_HORIZONTAL || z >= Chunk.CHUNK_SIZE_HORIZONTAL) {
			baseLevel = heightMap[Chunk.CHUNK_SIZE_HORIZONTAL - 1][Chunk.CHUNK_SIZE_HORIZONTAL - 1];
		} else {
			baseLevel = heightMap[x][z];
		}
		float depth = baseLevel - y;
		if (depth < 0) {
			return 0.0f;
		}
		return random.randomFloat(0.1f, 1.4f) * (float) Math.sqrt(random.randomFloat(1.8f * depth, 2.2f * depth) * depth);
	}

	private long generateSeedForChunk(long worldSeed, int x, int z) {
		return (((worldSeed << 3L) * x) ^ 0xF37C1E94L) + (worldSeed >> 1) * z;
	}
}
