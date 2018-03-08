package com.idkstudios.game.rendering;

import com.idkstudios.game.rendering.ChunkMeshBuilder.MeshType;

public class ChunkMesh {

	private int vertexCount[];
	private int[] vbos;

	public ChunkMesh(int[] vertexCount, int[] vbos) {
		super();
		this.vertexCount = vertexCount;
		this.vbos = vbos;
	}

	public ChunkMesh() {
		vbos = new int[MeshType.values().length];
		vertexCount = new int[MeshType.values().length];
	}

	public void setVBO(MeshType meshType, int vbo) {
		this.vbos[meshType.ordinal()] = vbo;
	}

	public void setVertexCount(MeshType meshType, int vertexCount) {
		this.vertexCount[meshType.ordinal()] = vertexCount;
	}

	public int getVBO(MeshType meshType) {
		return vbos[meshType.ordinal()];
	}

	public int getVertexCount(MeshType meshType) {
		return vertexCount[meshType.ordinal()];
	}

	public synchronized void destroy(MeshType meshType) {
		if (vbos[meshType.ordinal()] != 0 && vbos[meshType.ordinal()] != -1) {
			BufferManager.getInstance().deleteBuffer(vbos[meshType.ordinal()]);
			vbos[meshType.ordinal()] = 0;
			vertexCount[meshType.ordinal()] = 0;
		}
	}

	public void destroyAllMeshes() {
		destroy(MeshType.OPAQUE);
		destroy(MeshType.TRANSLUCENT);
	}
}
