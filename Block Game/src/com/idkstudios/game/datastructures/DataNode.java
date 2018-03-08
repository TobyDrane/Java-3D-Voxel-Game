package com.idkstudios.game.datastructures;

import java.util.ArrayList;
import java.util.List;

public class DataNode<Data> {

	private Data data;
	private List<DataNode<Data>> childs;

	public DataNode(Data d) {
		data = d;
		childs = new ArrayList<DataNode<Data>>();
	}

	public Data getData() {
		return data;
	}

	public void addChild(DataNode<Data> node) {
		childs.add(node);
	}

	public int childCount() {
		return childs.size();
	}

	public DataNode<Data> getChild(int i) {
		return childs.get(i);
	}
}
