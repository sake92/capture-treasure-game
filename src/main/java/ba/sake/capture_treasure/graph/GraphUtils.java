package ba.sake.capture_treasure.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Row;

public class GraphUtils {

	/**
	 * Builds a new graph of connected fields, GRASS and MOUNTAIN type.
	 * 
	 * @param board
	 * @return graph with vertices (visited=false)
	 */
	public static Graph newGraph(Board board) {
		// building a graph of connected fields
		Graph graph = new Graph();
		List<Row> rows = board.getRows();
		// add ALL VERTICES
		for (Row row : rows) {
			for (Field field : row.getFields()) {
				if (!field.getType().isWater()) {
					graph.addVertice(field);
				}
			}
		}
		// add EDGES
		for (int i = 0; i < rows.size(); i++) {
			List<Field> fields = rows.get(i).getFields();
			for (int j = 0; j < fields.size(); j++) {
				Field field = fields.get(j);
				if (i > 0) {
					Field fieldUp = rows.get(i - 1).getFields().get(j);
					graph.addEdge(field, fieldUp);
				}
				if (i < rows.size() - 1) {
					Field fieldDown = rows.get(i + 1).getFields().get(j);
					graph.addEdge(field, fieldDown);
				}
				if (j > 0) {
					Field fieldLeft = fields.get(j - 1);
					graph.addEdge(field, fieldLeft);
				}
				if (j < fields.size() - 1) {
					Field fieldRight = fields.get(j + 1);
					graph.addEdge(field, fieldRight);
				}
			}
		}
		return graph;
	}

	/**
	 * Visits every vertex reachable from given vertex(up,down,left,right). <br>
	 * Then we check if there are any unvisited vertices, then we know that there is
	 * a water island.
	 * 
	 * @param vertex
	 *            Beginning vertice.
	 * @param graph
	 */
	public static void floodVisit(Vertex vertex, Graph graph) {
		vertex.setVisited(true);
		Set<Vertex> outgoingVertices = new HashSet<>();
		for (Edge e : graph.getEdges()) {
			if (e.getNode1().equals(vertex) && !e.getNode2().isVisited()) {
				outgoingVertices.add(e.getNode2());
			} else if (e.getNode2().equals(vertex) && !e.getNode1().isVisited()) {
				outgoingVertices.add(e.getNode1());
			}
		}
		for (Vertex v : outgoingVertices) {
			floodVisit(v, graph);
		}
	}

	public static Map<DistanceSelector, Distance> shortestDistances(Graph graph) {
		// Floyd–Warshall_algorithm, find ALL SHORTEST PATHS
		// https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
		Map<DistanceSelector, Distance> distances = new HashMap<>();
		for (Vertex v1 : graph.getVertices()) {
			for (Vertex v2 : graph.getVertices()) {
				// if same vertice -> distance is ZERO
				// if different vertices -> initialize to INFINITE,
				// here 10 000 because of overflow handling...
				int w = (v1.equals(v2)) ? 0 : Graph.MAX_WEIGHT;
				Distance d = new Distance(w);
				DistanceSelector ds = new DistanceSelector(v1, v2);
				distances.put(ds, d);
			}
		}
		for (Edge e : graph.getEdges()) {
			// this is a UNDIRECTED graph, both ways are valid!
			DistanceSelector ds12 = new DistanceSelector(e.getNode1(), e.getNode2());
			Distance d12 = distances.get(ds12);
			d12.setWeight(e.getWeight());
			d12.setNext(e.getNode2());

			DistanceSelector ds21 = new DistanceSelector(e.getNode2(), e.getNode1());
			Distance d21 = distances.get(ds21);
			d21.setNext(e.getNode1());
			d21.setWeight(e.getWeight());
		}
		ArrayList<Vertex> vertices = new ArrayList<>(graph.getVertices());
		for (int k = 0; k < vertices.size(); k++) {
			for (int i = 0; i < vertices.size(); i++) {
				for (int j = 0; j < vertices.size(); j++) {
					Vertex kVertex = vertices.get(k);
					Vertex iVertex = vertices.get(i);
					Vertex jVertex = vertices.get(j);
					DistanceSelector ijDS = new DistanceSelector(iVertex, jVertex);
					DistanceSelector ikDS = new DistanceSelector(iVertex, kVertex);
					DistanceSelector kjDS = new DistanceSelector(kVertex, jVertex);
					Distance ijDist = distances.get(ijDS);
					Distance ikDist = distances.get(ikDS);
					Distance kjDist = distances.get(kjDS);
					if (ijDist.getWeight() > ikDist.getWeight() + kjDist.getWeight()) {
						ijDist.setWeight(ikDist.getWeight() + kjDist.getWeight());
						ijDist.setNext(ikDist.getNext());
					}
				}
			}
		}
		return distances;
	}

	public static Path path(Vertex from, Vertex to, Map<DistanceSelector, Distance> distances) {
		Distance distance = distances.get(new DistanceSelector(from, to));
		if (distance == null || distance.getNext() == null) {
			return new Path();
		}
		// real work
		ArrayList<Vertex> pathVertices = new ArrayList<>();
		pathVertices.add(from);
		int weight = 0;
		while (from != null && !from.equals(to)) {
			distance = distances.get(new DistanceSelector(from, to));
			weight += Graph.getWeight(from.getField(), distance.getNext().getField());
			from = distance.getNext();
			pathVertices.add(from);
		}
		return new Path(pathVertices, weight);
	}

	/**
	 * @param from
	 * @param to
	 * @param mustPass
	 * @param distances
	 * @return
	 */
	public static Path pathThroughAllGrass(Vertex from, Vertex to, Set<Vertex> mustPass,
			Map<DistanceSelector, Distance> distances) {
		// https://stackoverflow.com/questions/222413/find-the-shortest-path-in-a-graph-which-visits-certain-nodes
		Path solution = null;
		List<Vertex> mustPassList = new ArrayList<>(mustPass);
		List<List<Vertex>> permutations = permutations(mustPassList);
		for (int i = 0; i < permutations.size(); i++) {
			List<Vertex> perm = permutations.get(i);
			perm.add(0, from);
			perm.add(to);
			Path currPermPath = pathThrough(perm, distances);
			if (solution == null || currPermPath.getWeight() < solution.getWeight()) {
				solution = currPermPath;
			}
		}
		return solution;
	}

	/* HELPERS */
	private static Path pathThrough(List<Vertex> through, Map<DistanceSelector, Distance> distances) {
		List<Vertex> totalPathVertices = new ArrayList<>();
		int weight = 0;
		for (int i = 0; i < through.size() - 1; i++) {
			Path p = path(through.get(i), through.get(i + 1), distances);
			List<Vertex> pVertices = p.getVertices();
			if (pVertices.size() > 0) {
				totalPathVertices.addAll(pVertices.subList(1, pVertices.size()));
				weight += p.getWeight();
			}
		}
		return new Path(totalPathVertices, weight);
	}

	private static <T> List<List<T>> permutations(List<T> items) {
		// https://www.programcreek.com/2013/02/leetcode-permutations-java/
		// start from an empty list
		List<List<T>> result = new ArrayList<>();
		result.add(new ArrayList<>());

		for (int i = 0; i < items.size(); i++) {
			// list of list in current iteration of the array num
			List<List<T>> current = new ArrayList<>();
			for (List<T> l : result) {
				// # of locations to insert is largest index + 1
				for (int j = 0; j < l.size() + 1; j++) {
					// + add num[i] to different locations
					l.add(j, items.get(i));

					List<T> temp = new ArrayList<>(l);
					current.add(temp);
					// - remove num[i] add
					l.remove(j);
				}
			}
			result = new ArrayList<>(current);
		}
		return result;
	}

}
