package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * A game board consisting of <code>size</code> * <code>size</code> places
 * where battleships can be placed. A place of the board is denoted
 * by a pair of 0-based indices (x, y), where x is a column index
 * and y is a row index. A place of the board can be shot at, resulting
 * in either a hit or miss.
 *
 * This is the model class in the MVC
 */
public class Board {

    /**
     * Size of this board. This board has
     * <code>size*size </code> places.
     */
    private final int size;

    //Amount of shots the player has made on the board.
    //Shots on the same coordinate will not increase this value.
    private int numOfShots;

    //2D matrix of place objects representing the board
    private ArrayList<Place> places;

    private ArrayList<Ship> ships;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;
        this.numOfShots = 0;
        this.places = new ArrayList<Place>();
        for(int col = 0; col < size; col++){
            for(int row = 0; row < size; row++){
                Place newPlace = new Place(col, row);
                places.add(newPlace);
            }
        }

        Ship aircraftCarrier = new Ship(5);
        Ship battleship = new Ship(4);
        Ship frigate = new Ship(3);
        Ship submarine = new Ship(3);
        Ship minesweeper = new Ship(2);

        ships = new ArrayList<Ship>();
        ships.add(aircraftCarrier);
        ships.add(battleship);
        ships.add(frigate);
        ships.add(submarine);
        ships.add(minesweeper);
        placeShips();
    }

    /** Return the size of this board. */
    public int size() {
        return size;
    }

    // Suggestions:
    // 1. Consider using the Observer design pattern so that a client,
    //    say a BoardView, can observe changes on a board, e.g.,
    //    hitting a place, sinking a ship, and game over.
    //
    // 2. Introduce methods including the following:
    //    public boolean placeShip(Ship ship, int x, int y, boolean dir)
    //    public void hit(Place place)
    //    public Place at(int x, int y)
    //    public Place[] places()
    //    public int numOfShots()
    //    public boolean isGameOver()
    //    ...

    /**
     *
     * @param ship - ship to place
     * @param x - x coordinate
     * @param y - y coordinate
     * @param dir - Direction of ship. True for horizontal, false for vertical
     * @return True if the ship was successfully placed, false otherwise
     */
    public boolean placeShip(Ship ship, int x, int y, boolean dir){
        Place startingPlace = getPlace(x,y);

        //Verify that there isn't a ship where we are trying to place
        for(int i = 0; i < ship.getSize(); i++){
            if(dir){
                if(x+i >= size) return false;
                else if (getPlace(x + i, y).isShip()) return false;
            }
            else{
                if(y+i >= size) return false;
                if(getPlace(x,y+i).isShip()) return false;
            }
        }

        //Actually place the ship
        for(int i = 0; i < ship.getSize(); i++){
            if(dir){
                ship.getLocation().add(getPlace(x+i,y));
                getPlace(x+i,y).setShip(true);
            }
            else{
                ship.getLocation().add(getPlace(x,y+i));
                getPlace(x,y+i).setShip(true);
            }
        }
        return true;
    }


    /**
     * Method to randomly place ships
     * @return
     */
    public boolean placeShips(){
        Random rand = new Random();

        //Traverse all ships to place them all
        for(Ship currentShip : ships){

            //Flag to determine whether the ship was placed
            boolean placed = false;
            while(!placed) {

                //Random direction for the ship
                boolean direction = rand.nextBoolean();

                //Random location
                int x = rand.nextInt(this.size-1);
                int y = rand.nextInt(this.size-1);

                placed = placeShip(currentShip, x, y, direction);
            }
        }
        return true;
    }

    /**
     * Return true if the place has not been hit
     * before, false otherwise.
     * @param place
     * @return
     */
    public boolean hit(Place place){
        if(!place.isHit()){
            place.setHit(true);
            increaseShots();
            return true;
        }
        return false;
    }

    public Place at(int x, int y){
        return null;
    }

    public ArrayList<Place> places(){
        return this.places;
    }

    public int getNumOfShots(){
        return numOfShots;
    }

    public void increaseShots(){
        numOfShots++;
    }

    /**
     * Return a place object based on the coordinates
     * @param x
     * @param y
     * @return
     */
    public Place getPlace(int x, int y){
        for(Place currentPlace : places){
            if(currentPlace.getX() == x && currentPlace.getY() == y) return currentPlace;
        }
        return null;
    }

    /**
     * Game is over when all ships are sunk.
     * @return
     */
    public boolean isGameOver(){
        for(Ship currentShip : ships){
            if(!currentShip.isSunk()) return false;
        }
        return true;
    }
}
