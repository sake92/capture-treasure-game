package ba.sake.capture_treasure.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ba.sake.capture_treasure.graph.Edge;
import ba.sake.capture_treasure.graph.Graph;
import ba.sake.capture_treasure.graph.GraphUtils;
import ba.sake.capture_treasure.graph.Vertex;

@Entity
public class Board {

	private static final Logger logger = LoggerFactory.getLogger(Board.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int size;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Row> rows;

	@OneToOne(cascade = CascadeType.ALL)
	private Field player1GoldPosition;
	@OneToOne(cascade = CascadeType.ALL)
	private Field player2GoldPosition;

	private int minGrass;
	private int minMountain;
	private int minWater;

	public Board() {
		// defaults are: 8x8 board, 5 grass, 3 mountain, 4 water
		this(8, 5, 3, 4);
	}

	public Board(int size, int minGrass, int minMountain, int minWater) {
		if (size < 4) {
			throw new IllegalArgumentException("Board size too small: " + size);
		}
		if (size % 2 != 0) {
			throw new IllegalArgumentException("Board size must not be odd: " + size);
		}
		this.size = size;
		this.minGrass = minGrass;
		this.minMountain = minMountain;
		this.minWater = minWater;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Field getPlayer1GoldPosition() {
		return player1GoldPosition;
	}

	public void setPlayer1GoldPosition(Field player1GoldPosition) {
		this.player1GoldPosition = player1GoldPosition;
	}

	public Field getPlayer2GoldPosition() {
		return player2GoldPosition;
	}

	public void setPlayer2GoldPosition(Field player2GoldPosition) {
		this.player2GoldPosition = player2GoldPosition;
	}

	public int getMinGrass() {
		return minGrass;
	}

	public void setMinGrass(int minGrass) {
		this.minGrass = minGrass;
	}

	public int getMinMountain() {
		return minMountain;
	}

	public void setMinMountain(int minMountain) {
		this.minMountain = minMountain;
	}

	public int getMinWater() {
		return minWater;
	}

	public void setMinWater(int minWater) {
		this.minWater = minWater;
	}

	/* METHODS */
	/**
	 * Clears all fields, so they can be initialized by players.
	 */
	public void init() {
		// initialize fields
		this.rows = new ArrayList<>(size);
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			List<Field> rowFields = new ArrayList<>(size);
			for (int colIndex = 0; colIndex < size; colIndex++) {
				Field field = new Field(rowIndex, colIndex);
				rowFields.add(field);
			}
			Row row = new Row(rowFields);
			rows.add(row);
		}
	}

	public boolean isOk(Field grassField) {
		/* check border for WATER (max 3 by player), so players can pass freely */
		int player1NumWaters = 0;
		int player2NumWaters = 0;
		int player1BorderIndex = size / 2 - 1;
		int player2BorderIndex = size / 2;
		for (int i = 0; i < size; i++) {
			Field player1BorderField = getRows().get(player1BorderIndex).getFields().get(i);
			Field player2BorderField = getRows().get(player2BorderIndex).getFields().get(i);
			if (player1BorderField.getType().isWater()) {
				player1NumWaters++;
			}
			if (player2BorderField.getType().isWater()) {
				player2NumWaters++;
			}
		}
		boolean waterBorderOk = player1NumWaters <= 3 && player2NumWaters <= 3;
		if (!waterBorderOk)
			return false; // prevent graph calculation...

		/* check that there are NO ISLANDS, with flood algorithm */
		// it starts from player1 castle, since it it 100% grass field :)
		Graph graph = GraphUtils.newGraph(this);

		Optional<Edge> grassEdgeFirst = graph.getEdges().stream()
				.filter(e -> e.getNode1().getField().equals(grassField) || e.getNode2().getField().equals(grassField))
				.findFirst();
		if (!grassEdgeFirst.isPresent()) {
			logger.debug("There is a single-field island: " + grassField);
			return false;
		} else {
			Edge castleEdge = grassEdgeFirst.get();
			GraphUtils.floodVisit(castleEdge.getNode1(), graph);
		}
		for (Vertex v : graph.getVertices()) {
			if (!v.isVisited()) {
				logger.debug("There is an island, field: " + grassField);
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Board [size=");
		stringBuilder.append(size);
		stringBuilder.append(", fields=");
		for (Row fieldsRow : rows) {
			stringBuilder.append("\n    " + fieldsRow);
		}
		stringBuilder.append("\n, player1GoldPosition=");
		stringBuilder.append(player1GoldPosition);
		stringBuilder.append(", player2GoldPosition=");
		stringBuilder.append(player2GoldPosition);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}
