package edu.utep.cs.cs4330.battleship;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LopezMarcoA on 3/9/2017.
 *
 * Implement Serializable to be able to pass through intent
 *
 */

public class Player implements Serializable{

    private ArrayList<Ship> myShips;
    private Board myBoard;
    private int playerNumber; //int value indicating which player this is

    Player(int number){
        Ship aircraftCarrier = new Ship(5);
        Ship battleship = new Ship(4);
        Ship frigate = new Ship(3);
        Ship submarine = new Ship(3);
        Ship minesweeper = new Ship(2);

        aircraftCarrier.setShipType(Ship.Type.AIRCRAFT_CARRIER);
        battleship.setShipType(Ship.Type.BATTLESHIP);
        frigate.setShipType(Ship.Type.FRIGATE);
        submarine.setShipType(Ship.Type.SUBMARINE);
        minesweeper.setShipType(Ship.Type.MINESWEEPER);

        myShips = new ArrayList<Ship>();
        myShips.add(aircraftCarrier);
        myShips.add(battleship);
        myShips.add(frigate);
        myShips.add(submarine);
        myShips.add(minesweeper);

        playerNumber = number;
    }

    Player(){
        Ship aircraftCarrier = new Ship(5);
        Ship battleship = new Ship(4);
        Ship frigate = new Ship(3);
        Ship submarine = new Ship(3);
        Ship minesweeper = new Ship(2);

        aircraftCarrier.setShipType(Ship.Type.AIRCRAFT_CARRIER);
        battleship.setShipType(Ship.Type.BATTLESHIP);
        frigate.setShipType(Ship.Type.FRIGATE);
        submarine.setShipType(Ship.Type.SUBMARINE);
        minesweeper.setShipType(Ship.Type.MINESWEEPER);

        myShips = new ArrayList<Ship>();
        myShips.add(aircraftCarrier);
        myShips.add(battleship);
        myShips.add(frigate);
        myShips.add(submarine);
        myShips.add(minesweeper);

        playerNumber = 0;
    }
    /**
     * Update ship's places to see if it is sunk
     */
    public void updateShips(){
        /**Traverse ships to look for one that has the parameter place*/
        for(Ship currentShip : myShips){
            boolean sunkFlag = true;
            /**Traverse ship's places to see which one has the parameter place*/
            for(Place currentPlace : currentShip.getLocation()){
                if(!currentPlace.isHit()){
                    sunkFlag = false;
                    break;
                }
            }
            if(sunkFlag) currentShip.setSunk(true);
        }
    }

    public void makeShot(int x, int y){
        Place placeShot = myBoard.getPlace(x,y);
        if(!placeShot.isHit()){
            placeShot.setHit(true);
            updateShips();
        }
    }

    public boolean allSunk(){
        for(Ship currentShip : myShips){
            if(!currentShip.isSunk()) return false;
        }
        return true;
    }

//    public Ship getShipByType;

    public ArrayList<Ship> getMyShips() {
        return myShips;
    }

    public void setMyShips(ArrayList<Ship> myShips) {
        this.myShips = myShips;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public void setMyBoard(Board myBoard) {
        this.myBoard = myBoard;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }
}
