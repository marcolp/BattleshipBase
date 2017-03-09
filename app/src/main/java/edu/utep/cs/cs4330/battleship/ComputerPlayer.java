package edu.utep.cs.cs4330.battleship;

import java.util.Random;

/**
 * Created by LopezMarcoA on 3/9/2017.
 */

public class ComputerPlayer extends Player{

    Strategy strat;

    ComputerPlayer(){

    }

    /**
     * Method to randomly place ships
     * @return
     */
    private boolean placeShips(){
        Random rand = new Random();

        //Traverse all ships to place them all
        for(Ship currentShip : myShips){

            //Flag to determine whether the ship was placed
            boolean placed = false;
            while(!placed) {

                //Random direction for the ship
                boolean direction = rand.nextBoolean();

                //Random location
                int x = rand.nextInt(myBoard.getSize()-1);
                int y = rand.nextInt(myBoard.getSize()-1);

                placed = placeShip(currentShip, x, y, direction);
            }
        }
        return true;
    }

    /**
     *
     * @param ship - ship to place
     * @param x - x coordinate
     * @param y - y coordinate
     * @param dir - Direction of ship. True for horizontal, false for vertical
     * @return True if the ship was successfully placed, false otherwise
     */
    private boolean placeShip(Ship ship, int x, int y, boolean dir){
        Place startingPlace = myBoard.getPlace(x,y);

        //Verify that there isn't a ship where we are trying to place
        for(int i = 0; i < ship.getSize(); i++){
            if(dir){
                if(x+i >= myBoard.getSize()) return false;
                else if (myBoard.getPlace(x + i, y).isShip()) return false;
            }
            else{
                if(y+i >= myBoard.getSize()) return false;
                if(myBoard.getPlace(x,y+i).isShip()) return false;
            }
        }

        //Actually place the ship
        for(int i = 0; i < ship.getSize(); i++){
            if(dir){
                ship.getLocation().add(myBoard.getPlace(x+i,y));
                myBoard.getPlace(x+i,y).setShip(true);
            }
            else{
                ship.getLocation().add(myBoard.getPlace(x,y+i));
                myBoard.getPlace(x,y+i).setShip(true);
            }
        }
        return true;
    }

}
