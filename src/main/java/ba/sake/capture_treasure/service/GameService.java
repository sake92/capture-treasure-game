package ba.sake.capture_treasure.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ba.sake.capture_treasure.model.Board;
import ba.sake.capture_treasure.model.Field;
import ba.sake.capture_treasure.model.Game;
import ba.sake.capture_treasure.model.Player;
import ba.sake.capture_treasure.model.Row;
import ba.sake.capture_treasure.model.Game.Status;
import ba.sake.capture_treasure.repository.GameRepository;

@Service
@Transactional
public class GameService {

	private static final Logger logger = LoggerFactory.getLogger(GameService.class);

	@Autowired
	GameRepository gameRepository;

	@Autowired
	GameExecutor gameExecutor;

	public List<Game> findAll() {
		return gameRepository.findAll();
	}

	public Game findOne(@PathVariable Long id) {
		return gameRepository.findOne(id);
	}

	public Game create() {

		Game currentGame = gameRepository.findOneByStatus(Status.STARTED);
		if (currentGame != null) {
			throw new IllegalStateException("There is already a game that is not finished: " + currentGame.getId());
		}

		// create game
		Game game = new Game();
		game = gameRepository.save(game);
		// create board
		Board board = new Board();
		game.setBoard(board);
		// create players
		Player player1 = new Player(true, board);
		Player player2 = new Player(false, board);
		game.setPlayer1(player1);
		game.setPlayer2(player2);

		/* initialization */
		// populate board
		do {
			board.init();
			player1.populateBoardHalf();
			player2.populateBoardHalf();
			logger.info("Trying board: ");
			printBoard(board);
		} while (!board.isOk(player1.getCastlePosition()));

		// place players and their respective gold
		game.placePlayersAndGold();
		game = gameRepository.save(game);

		try {
			gameExecutor.startGame(game);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return game;
	}

	/* HELPERS */
	private static void printBoard(Board board) {
		for (Row row : board.getRows()) {
			String rowString = "";
			for (Field field : row.getFields()) {
				if (field.getType().isGrass())
					rowString += "G";
				else if (field.getType().isMountain())
					rowString += "M";
				else if (field.getType().isWater())
					rowString += " ";
			}
			logger.debug(rowString);
		}
	}
}
