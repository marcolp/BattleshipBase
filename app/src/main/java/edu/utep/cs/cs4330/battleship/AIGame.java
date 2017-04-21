package edu.utep.cs.cs4330.battleship;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * This is the controller in the MVC
 */
public class AIGame extends AppCompatActivity implements Observer{

    private TextView turnText;
    private TextView shotText;

    private Board playerBoard;
    private BoardView playerBoardView;

    private Board opponentBoard;
    private BoardView opponentBoardView;

    private SoundPool soundPool;
    private SparseIntArray soundMap;

    private boolean soundOption; //Boolean value to indicate if sound option is on or off

    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("Game Window", "This is the onCreate method");
        setContentView(R.layout.activity_game_window);

        final int boardSize = 10;
        soundOption = true;

        game = Game.getInstance();

        game.currentTurn = 1;
        game.numShots = 0;

        turnText = (TextView) findViewById(R.id.turnText);
        shotText = (TextView) findViewById(R.id.numShots);

        Player player = game.getPlayer1();
        Board playerBoard = player.getMyBoard();

        playerBoardView = (BoardView) findViewById(R.id.playerView);

        ComputerPlayer opponent = game.getComputerPlayer();
        opponent.placeShips();

        opponentBoard = opponent.myBoard;

        opponentBoardView = (BoardView) findViewById(R.id.opponentView);
        opponentBoardView.setOpponentBoard(false);

        //Views must have other player's board in order to indicate they are shooting at opponent
        opponentBoardView.setBoard(playerBoard);

        game.addObserver(this);

        playerBoardView.setBoard(opponentBoard);
        playerBoardView.setOpponentBoard(true);

        configureSounds();

        playerBoardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {

                //Only allow show it if it the player's turn
                if(game.hasTurn(player)){

                    turnText.setText("Current turn: \nPlayer "+game.currentTurn);
                    Place placeShot = opponentBoard.getPlace(x, y);

                    //If the player shoots a place already shot then do nothing.
                    if(!placeShot.isHit()) {

                        /**Make a shot on the board*/
                        boolean hitShip = game.makePlayerShot(placeShot);
                        String playerTurn = "";
                        playerTurn = "Current turn: \nPlayer "+game.currentTurn;
                        turnText.setText(playerTurn);

                        shotText.setText("Number of shots: "+game.numShots);

                        //If sound option is on play sounds
                        if(soundOption) {

                            //If it hit a ship play a sound
                            if (hitShip) {
                                soundPool.play(2, 1, 1, 1, 0, 1.0f);

                                /**If the shot sunk a ship play another sound
                                 * and check if the game is over.
                                 */
                                if (opponent.getShip(placeShot).isSunk()) {
                                    soundPool.play(3, 1, 1, 1, 0, 1.0f);

                                    if (game.isGameOver()) {
                                        if(soundOption) {
                                            soundPool.play(1, 1, 1, 1, 0, 1.0f);
                                        }

                                        playerBoardView.createGameOverDialog("All ships sunk! You Win!");
                                        playerBoardView.gameOverDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                finish();
                                                System.exit(0);
                                            }
                                        });


                                        //TODO RESET game by going back to activity 1;
//                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                                        startActivity(intent);
//                                        board = new Board(boardSize);
//                                        boardView.setBoard(board);
//                                        boardView.redraw();
//                                        boardView.updateShotNumber(0);
                                    }
                                    else {
                                        toast(String.format("Touched: %d, %d", x, y));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

//        boardView.setNewButton((Button)findViewById(R.id.newButton));
//        boardView.setButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boardView.createNewGameDialog();
//                board = new Board(boardSize);
//                boardView.setBoard(board);
//                boardView.redraw();
//                boardView.updateShotNumber(0);
//            }
//        });
        Log.d("Game window", "This is the onPause method");
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

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Sound OFF")){
            item.setTitle("Sound ON");
            item.setIcon(R.drawable.sound_optoin_off);
            soundOption = false;
        }

        else if(item.getTitle().equals("Sound ON")){
            item.setTitle("Sound OFF");
            item.setIcon(R.drawable.sound_option_on);
            soundOption = true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause(){
        Log.d("Main Activity", "This is the onPause method");
        super.onPause();

    }

    @Override
    protected void onResume(){
        Log.d("Main Activity", "This is the onResume method");
        super.onResume();
    }

    @Override
    protected void onStart(){
        Log.d("Main Activity", "This is the onStart method");
        super.onStart();

    }

    @Override
    protected void onStop(){
        Log.d("Main Activity", "This is the onStop method");
        super.onStop();
    }

    @Override
    protected void onRestart(){
        Log.d("Main Activity", "This is the onRestart method");
        super.onRestart();
    }

    @Override
    protected void onDestroy(){
        Log.d("Main Activity", "This is the onDestroy method");
        super.onDestroy();
    }

    public void newGame(View view){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**=======================Observer stuff===============================*/
    @Override
    public void update() {
        if(!((Activity) this).isFinishing())
        {
            //show dialog
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    turnText.setText("Current turn: \nPlayer "+game.currentTurn);
                    opponentBoardView.redraw();
                    if(game.isGameOver()){
                        opponentBoardView.createGameOverDialog("All ships sunk! You Lose!");
                        opponentBoardView.gameOverDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                                System.exit(0);
                            }
                        });
                    }
                }
            });
        }

    }
}