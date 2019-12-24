$(function() {

	var btnNewGameEnabled = false;

	var game = null;
	var board = null;

	var gameRefresh = null;

	var btnNewGame = $('#btnNewGame');
	var divCurrentGame = $('#divCurrentGame');

	// draw the board, if game already started
	getBoardData();

	$(btnNewGame).click(function() {
		btnNewGameEnabled = false;
		$.post('/api/games', function(data1, status) {
			game = data1;
			console.log('game ' + JSON.stringify(game));
			getBoardData();
		});
	});

	function initGameRefresh() {
		gameRefresh = window.setInterval(function() {
			getGameData();
		}, 300);
	}

	function getBoardData() {
		if (!game)
			return;
		$.get('/api/games/' + game.id + '/board', function(data, status) {
			if (data) {
				board = data;
				drawBoard();
				initGameRefresh();
			} else {
				// enable if no game started
				btnNewGameEnabled = true;
			}
		});
	}

	function getGameData() {
		$.get('/api/games/' + game.id, function(data, status) {
			game = data;
			drawAll();
		}).fail(function() {
			alert("Error while fetching current game data.");
			clearInterval(gameRefresh);
		});
	}

	function drawAll() {
		// drawBoard();
		// var oldImgSelector = 'img[src*="warrior' + num + '"]';
		// remove ALL IMAGES and draw them again
		$('img').remove();
		// player1
		drawCastle(1);
		drawGold(1);
		drawPlayer(1);
		// player2
		drawCastle(2);
		drawGold(2);
		drawPlayer(2);
		// game status
		$('#status').html(game.status);
		if (game.status.indexOf('FINISHED') !== -1) {
			btnNewGameEnabled = true;
		}
		$('#numMoves').html(game.numMoves);
		$('#moves').html(game.moves);
	}

	function drawBoard() {
		var currentGameBoard = '<table>';
		for (var rowIndex = 0; rowIndex < board.rows.length; rowIndex++) {
			var row = board.rows[rowIndex];
			currentGameBoard += '<tr>';
			for (var colIndex = 0; colIndex < row.fields.length; colIndex++) {
				var field = row.fields[colIndex];
				var cls = "";
				if (field.type == "GRASS") {
					cls += 'grass';
				} else if (field.type == "MOUNTAIN") {
					cls += 'mountain';
				} else if (field.type == "WATER") {
					cls += 'water';
				}
				var id = 'field-' + rowIndex + '-' + colIndex;
				currentGameBoard += '<td id="' + id + '" class="' + cls
						+ '"></td>';
			}
			currentGameBoard += '</tr>';
		}
		currentGameBoard += '</table>';
		$(divCurrentGame).html(currentGameBoard);
	}

	function drawPlayer(num) {
		var p = game['player' + num].position;
		var fieldSelector = '#field-' + p.row + '-' + p.column;
		var playerHTML = '<img src="images/warrior' + num + '.png">';
		$(fieldSelector).html(playerHTML);
	}

	function drawCastle(num) {
		var p = game['player' + num].castlePosition;
		var fieldSelector = '#field-' + p.row + '-' + p.column;
		var castleHTML = '<img src="images/castle' + num + '.png">';
		$(fieldSelector).html(castleHTML);
	}

	function drawGold(num) {
		var p = board['player' + num + 'GoldPosition'];
		var fieldSelector = '#field-' + p.row + '-' + p.column;
		var castleHTML = '<img src="images/gold.png">';
		$(fieldSelector).html(castleHTML);
	}

});
