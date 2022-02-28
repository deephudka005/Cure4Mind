package com.cureya.cure4mind.relaxation.game.ticTacToe.model;

import com.cureya.cure4mind.relaxation.game.ticTacToe.util.StringUtility;

public class Cell {

    public Player player;

    public Cell(Player player) {
        this.player = player;
    }

    public boolean isEmpty() {
        return player == null || StringUtility.isNullOrEmpty(player.value);
    }
}
