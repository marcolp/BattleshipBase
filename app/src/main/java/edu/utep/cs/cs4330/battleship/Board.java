package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;
import java.util.Random;

/**
 * Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
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

    //2D matrix of place objects representing the board
    private ArrayList<Place> places;

    private ArrayList<Ship> ships;

    /** Create a new board of the given size. */
    public Board(int size) {
        this.size = size;
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
    }

    public int getSize() {
        return size;
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






    protected Ship getShip(Place place){
        /**Traverse ships to look for one that has the parameter place*/
        for(Ship currentShip : ships){
            boolean sunkFlag = true;
            /**Traverse ship's places to see which one has the parameter place*/
            for(Place currentPlace : currentShip.getLocation()){
                if(currentPlace.getX() == place.getX() && currentPlace.getY() == place.getY()){
                    return currentShip;
                }
            }
        }
        return null;
    }

    public ArrayList<Place> places(){
        return this.places;
    }


    /**
     * Return a place object based on the coordinates
     * @param x
     * @param y
     * @return
     */
    public Place getPlace(int x, int y){
        for(Place currentPlace : places){
            if(currentPlace.getX() == x && currentPlace.getY() == y) {
                return currentPlace;
            }
        }
        return null;
    }
}
