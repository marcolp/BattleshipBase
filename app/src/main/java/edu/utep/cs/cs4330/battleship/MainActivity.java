package edu.utep.cs.cs4330.battleship;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 */
public class MainActivity extends AppCompatActivity {

    private Board board;
    private BoardView boardView;
    private TextView shots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        board = new Board(10);
        boardView = (BoardView) findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.setShotsTextView((TextView) findViewById(R.id.numShots));
        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                toast(String.format("Touched: %d, %d", x, y));
            }
        });

    }

    /** Show a toast message. */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
