package ba.sake.capture_treasure.graph;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import ba.sake.capture_treasure.graph.Vertex;
import ba.sake.capture_treasure.model.Field;

public class VertexTests {

	@Test
	public void testVisitedFalse() {
		Field field = new Field(1, 1);
		Vertex vertex = new Vertex(field);
		assertFalse(vertex.isVisited());
	}

}
