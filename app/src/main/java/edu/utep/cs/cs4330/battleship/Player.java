package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;

/**
 * Created by LopezMarcoA on 3/9/2017.
 */

public class Player {


    ArrayList<Ship> myShips;
    Board myBoard;
    int playerNumber; //int value indicating which player this is

    Player(){
        Ship aircraftCarrier = new Ship(5);
        Ship battleship = new Ship(4);
        Ship frigate = new Ship(3);
        Ship submarine = new Ship(3);
        Ship minesweeper = new Ship(2);

        myShips = new ArrayList<Ship>();
        myShips.add(aircraftCarrier);
        myShips.add(battleship);
        myShips.add(frigate);
        myShips.add(submarine);
        myShips.add(minesweeper);

        playerNumber = 0;
    }

    public boolean allSunk(){
        for(Ship currentShip : myShips){
            if(!currentShip.isSunk()) return false;
        }
        return true;
    }

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
