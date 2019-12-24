package ba.sake.capture_treasure.graph;

import java.util.HashSet;
import java.util.Set;
import ba.sake.capture_treasure.model.Field;

public class Graph {

	public static final int MAX_WEIGHT = 10_000;

	private Set<Vertex> vertices;
	private Set<Edge> edges;

	public Graph() {
		this.vertices = new HashSet<>();
		this.edges = new HashSet<>();
	}

	public Set<Vertex> getVertices() {
		return vertices;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public Vertex getVertex(Field f) {
		for (Vertex v : vertices) {
			if (v.getField().equals(f)) {
				return v;
			}
		}
		return null;
	}

	public Edge getEdge(Field f1, Field f2) {
		for (Edge e : edges) {
			Field n1Field = e.getNode1().getField();
			Field n2Field = e.getNode2().getField();
			if (n1Field.equals(f1) && n2Field.equals(f2)) {
				return e;
			}
			if (n1Field.equals(f2) && n2Field.equals(f1)) {
				return e;
			}
		}
		return null;
	}

	public void addVertice(Field field) {
		Vertex vertex = new Vertex(field);
		for (Vertex v : getVertices()) {
			if (v.getField().equals(field)) {
				// must be SAME INSTANCE, so when setVisited is called it is valid for all edges
				vertex = v;
			}
		}
		getVertices().add(vertex);
	}

	public void addEdge(Field field1, Field field2) {
		Vertex node1 = null;
		Vertex node2 = null;
		for (Vertex v : getVertices()) {
			if (v.getField().equals(field1)) {
				node1 = v;
			}
			if (v.getField().equals(field2)) {
				node2 = v;
			}
		}
		// edges are ADDED if there is a CONNECTION, e.g grass->mountain etc.
		int weight = getWeight(field1, field2);
		if (weight < MAX_WEIGHT) {
			Edge edge = new Edge(node1, node2, weight);
			edges.add(edge);
		}
	}

	public static int getWeight(Field field1, Field field2) {
		if (field1.equals(field2))
			return 0;
		int weight = MAX_WEIGHT;
		if (field1.getType().isGrass()) {
			if (field2.getType().isGrass()) {
				weight = 1;
			} else if (field2.getType().isMountain()) {
				weight = 2;
			}
		} else if (field1.getType().isMountain()) {
			if (field2.getType().isGrass()) {
				weight = 2;
			} else if (field2.getType().isMountain()) {
				weight = 4;
			}
		}
		return weight;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Graph [edges=");
		/*
		 * for (Vertex v : vertices) { stringBuilder.append("\n  " + v); }
		 */
		for (Edge e : edges) {
			stringBuilder.append("\n  " + e);
		}
		stringBuilder.append("\n]");
		return stringBuilder.toString();
	}

}
