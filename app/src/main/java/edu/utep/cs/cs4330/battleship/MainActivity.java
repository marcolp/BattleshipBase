package edu.utep.cs.cs4330.battleship;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * This is the controller in the MVC
 */
public class MainActivity extends AppCompatActivity {

    private Board board;
    private BoardView boardView;
    private TextView shots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int boardSize = 10;

        board = new Board(boardSize);
        boardView = (BoardView) findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.setShotsTextView((TextView) findViewById(R.id.numShots));


        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {


            @Override
            public void onTouch(int x, int y) {
                /**If the game is over, prevent the user from making more shots.*/

                    /**Make a shot on the board*/
                    board.makeShot(x, y);

                    /**Update the number of shots that have been made in the view*/
                    boardView.updateShotNumber(board.getNumOfShots());

                    if (board.isGameOver()) {
                        boardView.createGameOverDialog();
                        board = new Board(boardSize);
                        boardView.setBoard(board);
                        boardView.redraw();
                        boardView.updateShotNumber(0);
                    } else {
                        toast(String.format("Touched: %d, %d", x, y));
                    }
            }
        });

        boardView.setNewButton((Button)findViewById(R.id.newButton));
        boardView.setButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardView.createNewGameDialog();
                board = new Board(boardSize);
                boardView.setBoard(board);
                boardView.redraw();
                boardView.updateShotNumber(0);
            }
        });


    }

    /** Show a toast message. */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
