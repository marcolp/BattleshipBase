package edu.utep.cs.cs4330.battleship;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * This is the controller in the MVC
 */
public class MainActivity extends AppCompatActivity{

    private Board board;
    private BoardView boardView;

    private SoundPool soundPool;
    private SparseIntArray soundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int boardSize = 10;

        board = new Board(boardSize);
        boardView = (BoardView) findViewById(R.id.boardView);
        boardView.setBoard(board);
        boardView.setShotsTextView((TextView) findViewById(R.id.numShots));
        configureSounds();


        boardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {
                Place paceShot = board.getPlace(x,y);
                if(!paceShot.isHit()) {
                    /**Make a shot on the board*/
                    board.makeShot(x, y);

                    if (paceShot.isShip()) {
                        soundPool.play(2, 1, 1, 1, 0, 1.0f);
                        if (board.getShip(paceShot).isSunk()) {
                            soundPool.play(3, 1, 1, 1, 0, 1.0f);
                        }
                    }
                    /**Update the number of shots that have been made in the view*/
                    boardView.updateShotNumber(board.getNumOfShots());

                    if (board.isGameOver()) {
                        soundPool.play(1, 1, 1, 1, 0, 1.0f);
                        boardView.createGameOverDialog();
                        board = new Board(boardSize);
                        boardView.setBoard(board);
                        boardView.redraw();
                        boardView.updateShotNumber(0);
                    } else {
                        toast(String.format("Touched: %d, %d", x, y));
                    }
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

    private void configureSounds(){
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundMap = new SparseIntArray(3);
        soundMap.put(1, soundPool.load(this, R.raw.game_over_win,1));
        soundMap.put(2, soundPool.load(this, R.raw.cannon_shot,1));
        soundMap.put(3, soundPool.load(this, R.raw.ship_sink,1));
    }

    /** Show a toast message. */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
