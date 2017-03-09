package edu.utep.cs.cs4330.battleship;

/**
 * Created by marcolopez on 2/28/17.
 */

public class Game {

    Player player1;
    ComputerPlayer player2;

    Game(){
        player1 = new Player();
        player2 = new ComputerPlayer();
    }

    public void makePlayerShot(Place place){
        place.hit();
        if(!isGameOveR() && !place.isShip()){
            changeTurn();
            new Thread(this::makeComputerShot).start();

        }
    }

    private void makeComputerShot() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        boolean hit = opponent().makeMove();
    }

    private boolean isGameOver(){
        if(player1.is)
    }
}
