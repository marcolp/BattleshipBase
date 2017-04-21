package edu.utep.cs.cs4330.battleship;

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

import java.util.ArrayList;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * This is the controller in the MVC
 */
public class GameWindow extends AppCompatActivity implements Observer{

    private TextView turnText;
    private TextView shotText;

    private Board playerBoard;
    private BoardView playerBoardView;

    private Board opponentBoard;
    private BoardView opponentBoardView;

    private SoundPool soundPool;
    private SparseIntArray soundMap;

    private boolean soundOption; //Boolean value to indicate if sound option is on or off
    private NetworkAdapter netAdapter;

    private Game game = Game.getInstance();

    private Message receivedMessage;

    private Player player1;
    private Player player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("Game Window", "This is the onCreate method");
        setContentView(R.layout.activity_game_window);

        final int boardSize = 10;
        soundOption = true;

        game.currentTurn = 1;
//        game.numShots = 0;

        netAdapter = game.getPlayerConnection();

        turnText = (TextView) findViewById(R.id.turnText);
        shotText = (TextView) findViewById(R.id.numShots);

        player1 = game.getPlayer1();
        playerBoard = player1.getMyBoard();

        playerBoardView = (BoardView) findViewById(R.id.playerView);

        player2 = game.getPlayer2();
        opponentBoard = player2.myBoard;

        opponentBoardView = (BoardView) findViewById(R.id.opponentView);
        opponentBoardView.setOpponentBoard(false);

        //Views must have other player's board in order to indicate they are shooting at opponent
        opponentBoardView.setBoard(playerBoard);

        game.addObserver(this);

        playerBoardView.setBoard(opponentBoard);
        playerBoardView.setOpponentBoard(true);

        configureSounds();

        NetworkAdapter netAdapter = game.getPlayerConnection();
        netAdapter.setMessageListener(new NetworkAdapter.MessageListener() {
            public void messageReceived(NetworkAdapter.MessageType type, int x, int y, int[] others) {
                switch (type) {
                    case SHOT:
                        receivedMessage = new Message(Message.MessageType.SHOT, x, y, others);
                        processShot(receivedMessage);
                        break;

                    case SHOT_ACK:
                        receivedMessage = new Message(Message.MessageType.SHOT_ACK, x, y, others);
                        processShotACK(receivedMessage);
                        break;

                    case GAME:
                        receivedMessage = new Message(Message.MessageType.GAME, x, y, others);
//                        processGame(receivedMessage);
                        break;

                    case GAME_ACK:
                        receivedMessage = new Message(Message.MessageType.GAME_ACK, x, y, others);
//                        processGameACK(receivedMessage);
                        break;

                    case FLEET:
                        Log.d("RECEIVING", "FLEET");
                        receivedMessage = new Message(Message.MessageType.FLEET, x, y, others);
                        processFleetMessage(receivedMessage);
                        break;

                    case FLEET_ACK:
                        receivedMessage = new Message(Message.MessageType.FLEET_ACK, x, y, others);
                        processFleetACK(receivedMessage);
                        break;

                    case TURN:
                        receivedMessage = new Message(Message.MessageType.TURN, x, y, others);
                        processTurnMessage(receivedMessage);
                        break;

                    case QUIT:
                        receivedMessage = new Message(Message.MessageType.QUIT, x, y, others);
//                        processQuitMessage(receivedMessage);
                        break;

                    case CLOSE:
                        receivedMessage = new Message(Message.MessageType.CLOSE, x, y, others);
//                        processCloseMessag(receivedMessage);
                        break;

                    case UNKNOWN:
                        receivedMessage = new Message(Message.MessageType.UNKNOWN, x, y, others);
//                        processUnknownMessage(receivedMessage);
                        break;

                }
            }
        });

        game.chooseFirstPlayer();

        if(!game.userFirst) {
            game.getInstance().changeTurn();
        }

        turnText.setText("Current turn: \nPlayer "+game.currentTurn);


        //If we are the client then we send the fleet first
        if (game.getUserClient()) {
            netAdapter.writeFleet(game.makeFleetMessage());
        }

        netAdapter.receiveMessagesAsync();////////////


        playerBoardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
            @Override
            public void onTouch(int x, int y) {

                player1HitPlace(x,y);

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
        Log.d("Game window", "This is the onCreate method");
    }


    /**
     * Makes new ships from the fleet message and assigns them to the opponent.
     *
     * @param message
     */
    private boolean setOpponentShips(int[] message) {

        ArrayList<Ship> opponentShips = game.getPlayer2().getMyShips();

        int index = 0;

        int shipCount = 0;
        int nextShipSize = -1;
        int nextShipStartingX = -1;
        int nextShipStartingY = -1;
        boolean isHorizontal = false;

        Ship nextShip = null;

        //Traverse the message
        for (int i : message) {

            //We are looking at the size of a ship
            if (index == 0) {
                //Reset for next ship
                nextShipStartingX = -1;
                nextShipStartingY = -1;
                isHorizontal = false;

                nextShipSize = i;

                //Make a new ship based on the size
                switch (nextShipSize) {
                    case 5:
                        nextShip = new Ship(5);
                        break;
                    case 4:
                        nextShip = new Ship(4);
                        break;
                    case 3:
                        nextShip = new Ship(3);
                        break;
                    case 2:
                        nextShip = new Ship(2);
                        break;

                    default:
                        System.out.println("NOT A SHIP");
                        nextShip = null;
                        return false;
                }

                //Replace previous ship with the new ship
                opponentShips.set(shipCount , nextShip);
                shipCount++;

                index = 1;
            }

            //We are looking at the starting X of the ship
            else if (index == 1) {
                nextShipStartingX = i;
                index = 2;
            }

            //We are looking at the starting Y of the ship
            else if (index == 2) {
                nextShipStartingY = i;
                index = 3;
            }

            //We are looking at the direction of a ship
            else if (index == 3) {

                if (i == 1) isHorizontal = true;

                ArrayList<Place> newPlaces = new ArrayList<Place>();

                //Make new places for the new ship
                for (int j = 0; j < nextShipSize; j++) {
                    Place newPlace;
                    if (isHorizontal) {
                        newPlace = game.getPlayer2().getMyBoard().getPlace(nextShipStartingX + j, nextShipStartingY);
                    } else {
                        newPlace = game.getPlayer2().getMyBoard().getPlace(nextShipStartingX, nextShipStartingY + j);
                    }
//                    newPlace.setShip(true); //Not needed, handled in SHIP class (setLocation method)
//                    newPlaces.remove(shipCount-1);
                    newPlaces.add(newPlace);
                }

//                nextShip.setPlaces(newPlaces);
//                nextShip.setDirection(!isHorizontal);

                nextShip.setLocation(newPlaces);
                nextShip.setOrientation(isHorizontal);

                index = 0;
            }
        }

        return true;
    }

    /**
     * Called when we receive a FLEET message in the NetworkAdapter message receiver
     *
     * @param received
     */
    private void processFleetMessage(Message received) {

        setOpponentShips(received.others);

        //Send an ACK for the FLEET we received
        netAdapter.writeFleetAck(true);

        //If we are the Server
        if (!game.getUserClient()) {
            //Send our own FLEET message
            int[] message = game.makeFleetMessage();
            netAdapter.writeFleet(message);
        }

        //TODO when to do false
        //TODO difference between server and client
    }

    /**
     * Called when we receive a FLEET_ACK message in the NetworkAdapter message listener
     *
     * @param receivedMessage
     */
    private void processFleetACK(Message receivedMessage) {
        //If we are the client
        if (game.getUserClient()) {

        }

        //If we are the server
        //Server is always the last to receive a FLEET_ACK so we move on to starting the game from here
        else {
//            assignBoards();
//            setBoards();

            //Tell the opponent who has the first turn.
            //We send the opposite of our own game because if we are
            //first, then the opponent receives a message saying they are not
            //first (false)
            netAdapter.writeTurn(!game.getUserFirst());

            //If we do go first, then send the first shot
            if (game.getUserFirst()) {
                //TODO handle in the board listener????
            }

            //If we go second we will receive a SHOT message
            //Which will be handled by the message listener
            else {
                //TODO ?????
            }
        }
    }

    /**
     * Called when we receive a SHOT message in the NetworkAdapter message listener
     *
     * @param messageGot
     */
    private void processShot(Message messageGot) {


        /**
         * ALWAYS acknolwedge the SHOT message.
         * The reason being that I didn't find a way to extract the boolean
         * from the message. Since the boolean acknowleding the message is part
         * of the header and the network adapter extracts that into a type, int x, int y,
         * and int[] others. Even though the boolean is parsed as an int, the listener in
         * the example only receives 2 integers, but a SHOT_ACK would require 3: one for
         * the actual ACK (1 for accept, 2 for reject), int x and y for where the place was shot.
         */
        netAdapter.writeShotAck(true, messageGot.getX(), messageGot.getY());
//        Place placeShotbyOpponent = game.player1.getMyBoard().getPlace(messageGot.getX(), messageGot.getY());
//        game.makePlayerShot(placeShotbyOpponent);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                player2HitPlace(messageGot.getX(), messageGot.getY());
                opponentBoardView.invalidate();
            }
        });
    }

    /**
     * @param messageGot
     */
    private void processShotACK(Message messageGot) {

        /**
         * ALWAYS acknolwedge the SHOT message.
         * The reason being that I didn't find a way to extract the boolean
         * from the message. Since the boolean acknowleding the message is part
         * of the header and the network adapter extracts that into a type, int x, int y,
         * and int[] others. Even though the boolean is parsed as an int, the listener in
         * the example only receives 2 integers, but a SHOT_ACK would require 3: one for
         * the actual ACK (1 for accept, 2 for reject), int x and y for where the place was shot.
         */
        //        setBoards();
    }

    /**
     * Called when we receive a TURN message
     */
    private void processTurnMessage(Message gotMessage){

        //If it is 1 then we go first
        if(gotMessage.getX() == 1){
            if(game.currentTurn != 1) game.changeTurn();
        }

        else{
            if(game.currentTurn != 2) game.changeTurn();
        }
    }

    private void player2HitPlace(int x, int y){
        //Only allow show it if it the player's turn
        if(game.hasTurn(player2)){

            turnText.setText("Current turn: \nPlayer "+game.currentTurn);
            Place placeShot = playerBoard.getPlace(x, y);

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
//                        netAdapter.writeShot(x,y);
                        soundPool.play(2, 1, 1, 1, 0, 1.0f);

                        /**If the shot sunk a ship play another sound
                         * and check if the game is over.
                         */
                        if (player1.getShip(placeShot).isSunk()) {
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
                                toast(String.format("Opponent Sunk a ship at: %d, %d!", x, y));
                            }
                        }
                    }
                }
            }
        }
    }

    private void player1HitPlace(int x, int y){
        //Only allow show it if it the player's turn
        if(game.hasTurn(player1)){

            turnText.setText("Current turn: \nPlayer "+game.currentTurn);
            Place placeShot = opponentBoard.getPlace(x, y);

            //If the player shoots a place already shot then do nothing.
            if(!placeShot.isHit()) {

                netAdapter.writeShot(x,y);

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
                        if (player2.getShip(placeShot).isSunk()) {
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
                                toast(String.format("You sunk a ship at: %d, %d!", x, y));
                            }
                        }
                    }
                }
            }
        }
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
