package ba.sake.capture_treasure.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ba.sake.capture_treasure.graph.Distance;
import ba.sake.capture_treasure.graph.DistanceSelector;
import ba.sake.capture_treasure.graph.Edge;
import ba.sake.capture_treasure.graph.Graph;
import ba.sake.capture_treasure.graph.GraphUtils;
import ba.sake.capture_treasure.graph.Vertex;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Game;
import ba.sake.capture_treasure.model.Player;
import ba.sake.capture_treasure.model.Game.Status;
import ba.sake.capture_treasure.repository.GameRepository;
import ba.sake.capture_treasure.repository.PlayerRepository;

@Component
public class GameExecutor {

	private static final Logger logger = LoggerFactory.getLogger(GameExecutor.class);

	@Autowired
	GameRepository gameRepository;
	@Autowired
	PlayerRepository playerRepository;

	// http://www.baeldung.com/spring-async
	// https://spring.io/guides/gs/async-method/
	@Async
	public void startGame(Game game) throws InterruptedException {
		logger.info("Starting game " + game.getId() + " ...");
		// finding ALL shortest paths
		Board board = game.getBoard();
		Graph graph = GraphUtils.newGraph(board);
		Map<DistanceSelector, Distance> distances = GraphUtils.shortestDistances(graph);

		Player player1 = game.getPlayer1();
		Player player2 = game.getPlayer2();

		Vertex p1StartVert = new Vertex(player1.getPosition());
		Vertex p1CastleVert = new Vertex(player1.getCastlePosition());
		Vertex p2StartVert = new Vertex(player2.getPosition());
		Vertex p2CastleVert = new Vertex(player2.getCastlePosition());

		Set<Vertex> player1GrassVertices = new HashSet<>();
		Set<Vertex> player2GrassVertices = new HashSet<>();
		for (Vertex vertex : graph.getVertices()) {
			Field f = vertex.getField();
			if (f.getType().isGrass()) {
				if (f.getRow() < board.getSize() / 2) {
					player1GrassVertices.add(vertex);
				} else {
					player2GrassVertices.add(vertex);
				}
			}
		}

		int maxMoves = 200;

		int numMoves = 0;
		int player1Moves = 0;
		int player2Moves = 0;

		boolean player1Won = false;
		boolean player2Won = false;
		boolean isPlayer1Move = true; // whos turn is it
		List<Vertex> player1Path = GraphUtils
				.pathThroughAllGrass(p1StartVert, p1CastleVert, player1GrassVertices, distances).getVertices();
		List<Vertex> player2Path = GraphUtils
				.pathThroughAllGrass(p2StartVert, p2CastleVert, player2GrassVertices, distances).getVertices();

		while (numMoves < maxMoves && !player1Won && !player2Won) {
			// move player
			if (isPlayer1Move) {
				player1Moves++;
				Field nextPosition = player1Path.get(0).getField();
				Edge edge = graph.getEdge(player1.getPosition(), nextPosition);
				logger.debug("player1 " + player1);
				logger.debug("player1 path " + player1Path);
				logger.debug("next position " + nextPosition);
				logger.debug("edge " + edge);
				if (player1Moves == edge.getWeight()) {
					game.setMoves(game.getMoves() + moveString(player1.getPosition(), nextPosition, 1));
					player1.setPosition(nextPosition);
					updatePlayerPath(board, distances, player1, player2, board.getPlayer1GoldPosition(), player1Path);
					player1Moves = 0;
				}
			} else {
				player2Moves++;
				Field nextPosition = player2Path.get(0).getField();
				Edge edge = graph.getEdge(player2.getPosition(), nextPosition);
				logger.debug("player2 " + player2);
				logger.debug("player2 path " + player2Path);
				logger.debug("next position " + nextPosition);
				logger.debug("edge " + edge);
				int edgeWeight = edge.getWeight();
				if (player2Moves == edgeWeight) {
					game.setMoves(game.getMoves() + moveString(player2.getPosition(), nextPosition, 2));
					player2.setPosition(nextPosition);
					updatePlayerPath(board, distances, player2, player1, board.getPlayer2GoldPosition(), player2Path);
					player2Moves = 0;
				}
			}

			numMoves++;
			game.setNumMoves(numMoves);
			isPlayer1Move = !isPlayer1Move;

			// check if someone won
			if (player1.isFoundGold() && player1.getPosition().equals(player2.getCastlePosition()))
				player1Won = true;
			if (player2.isFoundGold() && player2.getPosition().equals(player1.getCastlePosition()))
				player2Won = true;
			// check if fell in water
			if (player1.getPosition().getType().isWater())
				player2Won = true;
			if (player2.getPosition().getType().isWater())
				player1Won = true;

			playerRepository.save(player1);
			playerRepository.save(player2);
			gameRepository.save(game);
			Thread.sleep(500); // wait half-second
		}

		if (player1Won) {
			game.setStatus(Status.FINISHED_PLAYER1_WON);
		} else if (player2Won) {
			game.setStatus(Status.FINISHED_PLAYER2_WON);
		} else {
			game.setStatus(Status.FINISHED_TIE);
		}
		gameRepository.save(game);
	}

	private void updatePlayerPath(Board board, Map<DistanceSelector, Distance> distances, Player player,
			Player playerOther, Field goldPosition, List<Vertex> playerPath) {
		if (!player.isFoundGold() && player.getPosition().equals(goldPosition)) {
			player.setFoundGold(true);
			playerPath.clear();
			List<Vertex> pathToOponentCastle = GraphUtils
					.path(new Vertex(player.getPosition()), new Vertex(playerOther.getCastlePosition()), distances)
					.getVertices();
			// without player's position!!!
			pathToOponentCastle = pathToOponentCastle.subList(1, pathToOponentCastle.size());
			playerPath.addAll(pathToOponentCastle);
		} else {
			playerPath.remove(0);
		}
	}

	private String moveString(Field from, Field to, int player) {
		String cls = (player == 1) ? "text-success" : "text-info";
		String res = "";
		res += "<span class='" + cls + "'" + player + "Move'>Player" + player + "[";
		res += from.getRow() + ":" + from.getColumn();
		res += "->";
		res += to.getRow() + ":" + to.getColumn();
		res += "] </span>";
		return res;
	}

}
