package edu.utep.cs.cs4330.battleship;

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
            //TODO may need an else here
        }
    }

    public void addComputer(ComputerPlayer newComputer){
        if(computerPlayer == null){
            computerPlayer = newComputer
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

    public void makePlayerShot(Place place){
        place.setHit(true);
        if(!isGameOver() && !place.isShip()){
            changeTurn();
            new Thread(this::makeComputerShot).start();
        }
    }
//
//    private void makeComputerShot() {
//        try {
//            Thread.sleep(500);
//        }   catch (InterruptedException e) {
//        }
//
//        boolean hit = opponent().makeMove();
//    }
//
    private boolean isGameOver(){
        if(player1.allSunk() || player2.allSunk())
            return true;
        else return false;
    }
}
