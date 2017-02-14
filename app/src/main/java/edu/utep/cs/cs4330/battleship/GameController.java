package edu.utep.cs.cs4330.battleship;

import android.view.View;

/**
 * Created by LopezMarcoA on 2/13/2017.
 */

public class GameController {
    private BoardView boardView;
    private Board  board;

    public GameController(BoardView newBoardView, Board newBoard){
        this.boardView = newBoardView;
        this.board = newBoard;

        //Create onClickListener
        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = new Board(board.size());
            }
        };

        this.boardView.setOnClickListener(buttonListener);
    }
}
