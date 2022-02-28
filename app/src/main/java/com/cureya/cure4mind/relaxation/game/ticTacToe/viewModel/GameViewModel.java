package com.cureya.cure4mind.relaxation.game.ticTacToe.viewModel;

import static com.cureya.cure4mind.relaxation.game.ticTacToe.util.StringUtility.stringFromNumbers;

import androidx.databinding.ObservableArrayMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.cureya.cure4mind.relaxation.game.ticTacToe.model.Cell;
import com.cureya.cure4mind.relaxation.game.ticTacToe.model.Game;
import com.cureya.cure4mind.relaxation.game.ticTacToe.model.Player;

public class GameViewModel extends ViewModel {

    public ObservableArrayMap<String, String> cells;
    private Game game;

    public void init(String player1, String player2) {
        game = new Game(player1, player2);
        cells = new ObservableArrayMap<>();
    }

    public void onClickedCellAt(int row, int column) {
        if (game.cells[row][column] == null) {
            game.cells[row][column] = new Cell(game.currentPlayer);
            cells.put(stringFromNumbers(row, column), game.currentPlayer.value);
            if (game.hasGameEnded())
                game.reset();
            else
                game.switchPlayer();
        }
    }

    public LiveData<Player> getWinner() {
        return game.winner;
    }
}
