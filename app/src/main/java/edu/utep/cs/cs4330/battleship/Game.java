package edu.utep.cs.cs4330.battleship;

import android.widget.Toast;

import java.util.ArrayList;

/**
 * Using singleton so both activities can access the game
 * Created by marcolopez on 2/28/17.
 */

public class Game {

    int numShots; //integer representing the number of shots human player has made

    Player player1;
    ComputerPlayer computerPlayer;

    int currentTurn;


    public static final Game singletonGame = new Game();

    public static Game getInstance(){
        return singletonGame;
    }


    public void addPlayer(Player newPlayer){
        if(player1 == null) {
            player1 = newPlayer;
        }

        else{
            //TODO something here?
        }
    }

    public void addComputer(ComputerPlayer newComputer){
        if(computerPlayer == null){
            computerPlayer = newComputer;
        }

        else{
            //TODO something here?
        }
    }
    /**
     * Get player1
     *
     * @return Player1
     */
    public Player getPlayer(){
        return player1;
    }

    /**
     * Get computer player
     * @return computerPlayer
     */
    public ComputerPlayer getComputerPlayer(){
        return computerPlayer;
    }

    /**
     * Make player's shot
     *
     * @param place - location to shoot
     * @return - true if the player hit a ship, false otherwise
     */
    public boolean makePlayerShot(Place place){
        place.setHit(true);
        if(!isGameOver() && !place.isShip()){
            changeTurn();
            new Thread(this::makeComputerShot).start();
            numShots++;
            return false;
        }
        numShots++;
        return true;
    }

    private void makeComputerShot() {
        try {
            Thread.sleep(500);
        }   catch (InterruptedException e) {
        }
        boolean hit = computerPlayer.makeMove();
        if (!isGameOver()) {
            if (hit) {
                makeComputerShot();
            } else {
                changeTurn();
            }
        }
        numShots++;
    }

    public boolean isGameOver(){
        if(player1.allSunk() || computerPlayer.allSunk())
            return true;
        else return false;
    }

    /**
     * Check if it is the passed player's turn
     *
     * @param player
     * @return true if it is the player's true, false otherwise
     */
    public boolean hasTurn(Player player){
        if(player.getPlayerNumber() == currentTurn) return true;
        else return false;
    }

    /**
     * Change the turn to the other player
     * @return the current turn indicator
     */
    private int changeTurn(){
        if(currentTurn == 1) currentTurn = 2;
        else if(currentTurn == 2) currentTurn = 1;
        return currentTurn;
    }
}
