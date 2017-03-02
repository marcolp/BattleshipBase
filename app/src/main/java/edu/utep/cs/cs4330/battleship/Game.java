package edu.utep.cs.cs4330.battleship;

/**
 * Created by marcolopez on 2/28/17.
 */

public class Game {
    public void makePlayerShot(Place place){
        place.hit();
        if(!isGameOveR() && !place.hasShip()){
            changeTurn();
            new Thread(this::makeComputerShot).start();

        }
    }

    private void makeComputerShot(){
        try{
            Thread.sleep(500);
        }catch (InterruptedException e){}

        boolean hit = opponent().makeMove();
}
