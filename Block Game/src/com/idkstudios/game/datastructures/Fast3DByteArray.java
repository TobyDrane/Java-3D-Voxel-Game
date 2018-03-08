package com.idkstudios.game.datastructures;

public class Fast3DByteArray {

	private final byte array[];
	private final int lX, lY, lZ;
	private final int size;

	/**
	 * Init. a new 3D array with the given dimensions.
	 */
	public Fast3DByteArray(int x, int y, int z) {
		lX = x;
		lY = y;
		lZ = z;

		size = lX * lY * lZ;
		array = new byte[size];
	}

	/**
	 * Returns the byte value at the given position.
	 */
	public byte get(int x, int y, int z) {

		int pos = (x * lX * lY) + (y * lX) + z;

		if (x >= lX || y >= lY || z >= lZ || x < 0 || y < 0 || z < 0)
			return 0;

		return array[pos];
	}

	/**
	 * Sets the byte value for the given position.
	 */
	public void set(int x, int y, int z, byte b) {
		int pos = (x * lX * lY) + (y * lX) + z;

		if (x >= lX || y >= lY || z >= lZ || x < 0 || y < 0 || z < 0)
			return;

		array[pos] = b;
	}

	/**
	 * Returns the raw byte at the given index.
	 */
	public byte getRawByte(int i) {
		return array[i];
	}

	/**
	 * Sets the raw byte for the given index.
	 */
	public void setRawByte(int i, byte b) {
		array[i] = b;
	}

	/**
	 * Returns the size of this array.
	 */
	public int size() {
		return size;
	}
}
