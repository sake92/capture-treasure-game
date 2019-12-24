package ba.sake.capture_treasure.graph;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import ba.sake.capture_treasure.graph.Edge;
import ba.sake.capture_treasure.graph.Graph;
import ba.sake.capture_treasure.graph.Vertex;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Field.Type;

public class GraphTests {

	@Test
	public void testCreateGraph() {
		Graph graph = new Graph();
		Field f1 = new Field(0, 0);
		f1.setType(Type.GRASS);
		graph.addVertice(f1);
		Field f2 = new Field(0, 1);
		f2.setType(Type.GRASS);
		graph.addVertice(f2);
		graph.addEdge(f1, f2);
		assertThat(graph.getVertices().size(), is(2));
		assertThat(graph.getEdges().size(), is(1));
	}

	@Test
	public void testAddVertice() {
		Graph graph = new Graph();
		Field f1 = new Field(0, 0);
		f1.setType(Type.GRASS);
		graph.addVertice(f1);
		graph.addVertice(f1); // redundant, should be ignored!
		Field f2 = new Field(0, 1);
		f2.setType(Type.GRASS);
		graph.addVertice(f2);
		assertThat(graph.getVertices().size(), is(2));
	}

	@Test
	public void testGetVertice() {
		Graph graph = new Graph();
		Field f1 = new Field(0, 0);
		f1.setType(Type.GRASS);
		graph.addVertice(f1);
		Field f2 = new Field(0, 1);
		f2.setType(Type.GRASS);
		graph.addVertice(f2);
		Vertex v1 = graph.getVertex(f1);
		assertThat(v1.getField(), is(f1));
	}

	@Test
	public void testAddEdge() {
		Graph graph = new Graph();
		Field f1 = new Field(0, 0);
		f1.setType(Type.GRASS);
		graph.addVertice(f1);
		Field f2 = new Field(0, 1);
		f2.setType(Type.GRASS);
		graph.addVertice(f2);
		graph.addEdge(f1, f2);
		graph.addEdge(f1, f2); // ignored :)
		assertThat(graph.getEdges().size(), is(1));
	}

	@Test
	public void testGetEdge() {
		Graph graph = new Graph();
		Field f1 = new Field(0, 0);
		f1.setType(Type.GRASS);
		graph.addVertice(f1);
		Field f2 = new Field(0, 1);
		f2.setType(Type.GRASS);
		graph.addVertice(f2);
		graph.addEdge(f1, f2);
		assertThat(graph.getEdges().size(), is(1));
		Edge e1 = graph.getEdge(f1, f2);
		assertThat(e1.getNode1().getField(), is(f1));
		assertThat(e1.getNode2().getField(), is(f2));
	}

}
