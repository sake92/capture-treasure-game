package ba.sake.capture_treasure.graph;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import ba.sake.capture_treasure.graph.Distance;
import ba.sake.capture_treasure.graph.DistanceSelector;
import ba.sake.capture_treasure.graph.Edge;
import ba.sake.capture_treasure.graph.Graph;
import ba.sake.capture_treasure.graph.GraphUtils;
import ba.sake.capture_treasure.graph.Path;
import ba.sake.capture_treasure.graph.Vertex;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Row;
import ba.sake.capture_treasure.model.Field.Type;

public class GraphUtilsTests {

	@Test
	public void testCreateGraph() {
		Board board = makeBoard();
		Graph graph = GraphUtils.newGraph(board);
		assertThat(graph.getVertices().size(), is(14));
		Edge grassToGrassEdge = graph.getEdge(new Field(0, 1), new Field(1, 1));
		assertThat(grassToGrassEdge.getWeight(), is(1));
		Edge grassToMountainEdge = graph.getEdge(new Field(1, 0), new Field(2, 0));
		assertThat(grassToMountainEdge.getWeight(), is(2));
		Edge mountainToMountainEdge = graph.getEdge(new Field(2, 2), new Field(2, 3));
		assertThat(mountainToMountainEdge.getWeight(), is(4));
	}

	@Test
	public void testFloodGraph() {
		Board board = makeBoard();
		Graph graph = GraphUtils.newGraph(board);
		GraphUtils.floodVisit(new Vertex(new Field(1, 1)), graph);
		Set<Vertex> unvisited = graph.getVertices().stream().filter(v -> !v.isVisited()).collect(Collectors.toSet());
		assertThat(unvisited.size(), is(0));
	}

	@Test
	public void testShortestDistances() {
		Board board = makeBoard();
		Graph graph = GraphUtils.newGraph(board);
		Map<DistanceSelector, Distance> shortestDistances = GraphUtils.shortestDistances(graph);
		Distance distance1 = shortestDistances
				.get(new DistanceSelector(new Vertex(new Field(1, 0)), new Vertex(new Field(2, 0))));
		assertThat(distance1.getWeight(), is(2));
		Distance distance2 = shortestDistances
				.get(new DistanceSelector(new Vertex(new Field(0, 1)), new Vertex(new Field(2, 3))));
		assertThat(distance2.getWeight(), is(5));
		assertThat(distance2.getNext(), is(new Vertex(new Field(0, 2))));
	}

	@Test
	public void testPath() {
		Board board = makeBoard();
		Graph graph = GraphUtils.newGraph(board);
		Map<DistanceSelector, Distance> shortestDistances = GraphUtils.shortestDistances(graph);
		Field f1 = new Field(0, 1);
		f1.setType(Type.GRASS);
		Field f2 = new Field(2, 3);
		f2.setType(Type.MOUNTAIN);
		Path path = GraphUtils.path(new Vertex(f1), new Vertex(f2), shortestDistances);
		assertThat(path.getWeight(), is(5));
		assertThat(path.getVertices().size(), is(5));
	}

	/////////////////////////////
	private static Board makeBoard() {
		Board board = new Board(4, 3, 1, 1);
		board.init();
		List<Row> rows = board.getRows();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			List<Field> fields = row.getFields();
			for (int j = 0; j < fields.size(); j++) {
				Field field = fields.get(j);
				if ((i == 0 && j == 0) || (i == 2 && j == 1)) {
					field.setType(Type.WATER);
				} else if (i == 2) {
					field.setType(Type.MOUNTAIN);
				} else {
					field.setType(Type.GRASS);
				}
			}
		}
		return board;
	}

}
