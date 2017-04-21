package edu.utep.cs.cs4330.battleship;

import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Using singleton so both activities can access the game
 * Created by marcolopez on 2/28/17.
 */

public class Game implements Observable {


    int numShots = 0; //integer representing the number of shots human player has made

    Player player1;
    Player player2;

    int currentTurn;

    boolean userClient;
    boolean userFirst;
    NetworkAdapter playerConnection;

    public static final Game singletonGame = new Game();

    public static Game getInstance() {
        return singletonGame;
    }


    public void addPlayer1(Player newPlayer) {
        player1 = newPlayer;

    }

    public void addPlayer2(Player newPlayer2) {
        player2 = newPlayer2;

    }

    public void addComputer(ComputerPlayer newComputer) {
        if (player2 == null) {
            player2 = newComputer;
        } else {
            player2 = newComputer;
        }
    }

    /**
     * Get player1
     *
     * @return Player1
     */
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    /**
     * Get computer player
     *
     * @return computerPlayer
     */
    public ComputerPlayer getComputerPlayer() {
        String className = player2.getClass().getCanonicalName().toString();
        if (className.equals("edu.utep.cs.cs4330.battleship.ComputerPlayer")) {
            return (ComputerPlayer) player2;
        } else {
            return null;
        }
    }

    /**
     * Creates a NetworkAdapter based on a TCP socket
     *
     * @param wifiSocket
     */
    public void initializeAdapter(Socket wifiSocket) {
        playerConnection = new NetworkAdapter(wifiSocket);
    }

    /**
     * Return the NetworkAdapter
     *
     * @return
     */
    public NetworkAdapter getPlayerConnection() {
        return playerConnection;
    }

    /**
     * Creates a fleet message encoded for the NetworkAdapter
     *
     * @return int array containing encoded message of the player's ships
     */
    public int[] makeFleetMessage() {
        //Create new fleet message (4 things per ship, 5 ships)
        int[] fleetMessage = new int[4 * 5];

        int index = 0;

        /**Traverse all the ships*/
        for (Ship currentShip : Game.getInstance().player1.getMyShips()) {

            //4 entries per ship
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    //Adding the size of the current ship
                    case 0:
                        fleetMessage[index] = currentShip.getSize();
                        break;

                    //Adding the starting x position
                    case 1:
                        fleetMessage[index] = currentShip.getLocation().get(0).getX();
                        break;


                    //Adding the starting y postition
                    case 2:
                        fleetMessage[index] = currentShip.getLocation().get(0).getY();
                        break;

                    //Adding the direction of the current ship
                    case 3:
                        if (currentShip.isOrientation())
                            fleetMessage[index] = 1;

                        else
                            fleetMessage[index] = 0;

                        break;
                }
                index++;
            }
        }

        return fleetMessage;
    }

    /**
     * Make player's shot
     *
     * @param place - location to shoot
     * @return - true if the player hit a ship, false otherwise
     */
    public boolean makePlayerShot(Place place) {
        place.setHit(true);
        numShots++;
        if (!isGameOver() && !place.isShip()) {
            changeTurn();
            if(player2.getClass().getName().equals("edu.utep.cs.cs4330.battleship.ComputerPlayer"))
                new Thread(this::makeComputerShot).start(); //No need to call this in a WIFI game
            return false;
        }
        return true;
    }


    private void makeComputerShot() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        boolean hit = this.getComputerPlayer().makeMove();
        notifyObserver();
        if (!isGameOver()) {
            if (hit) {
                makeComputerShot();
            } else {
                changeTurn();
            }
        }
    }

    public boolean isGameOver() {
        if (player1.allSunk() || player2.allSunk())
            return true;
        else return false;
    }

    /**
     * Check if it is the passed player's turn
     *
     * @param player
     * @return true if it is the player's true, false otherwise
     */
    public boolean hasTurn(Player player) {
        if (player.getPlayerNumber() == currentTurn) return true;
        else return false;
    }

    /**
     * Change the turn to the other player
     *
     * @return the current turn indicator
     */
    public int changeTurn() {
        if (currentTurn == 1) currentTurn = 2;
        else if (currentTurn == 2) currentTurn = 1;
        return currentTurn;
    }

    /**
     * Returns whether the user is the first player or not
     *
     * @return
     */
    public boolean getUserFirst() {
        return userFirst;
    }

    /**
     * Randomly determines which player goes first
     *
     * @return
     */
    public boolean chooseFirstPlayer() {
        Random rand = new Random();

        int n = rand.nextInt(2) + 1;

        //If the user is the first player
        //Since the user is initialized as being first (see <initPlayers(int b, int s)>)
        //there is no need to change anything
        if (n == 1) {
            userFirst = true;
            return true;
        }


        //If the user goes second, then we switch
        //both players' turns in order for the opponent
        //to go first and the user to go second.
        else {
            userFirst = false;
            return false;
        }
    }

    public void setUserClient(boolean userClient) {
        this.userClient = userClient;
    }

    public boolean getUserClient() {
        return userClient;
    }

    public int getNumShots() {
        return numShots;
    }

    public void setNumShots(int numShots) {
        this.numShots = numShots;
    }

    /**
     * =================================Observer stuff======================================
     */

    private ArrayList<Observer> activities = new ArrayList<Observer>();

    @Override
    public void addObserver(Observer o) {
        activities.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        activities.remove(o);
    }

    @Override
    public void notifyObserver() {
        for (Observer current : activities) {
            current.update();
        }
    }
}
