package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 */

public class Ship {
    private int size;
    private ArrayList<Place> location;
    private boolean sunk;

    public Ship(){
        this.size = -1;
        this.location = new ArrayList<Place>();
        this.sunk = false;
    }

    public Ship(int size){
        this.size = size;
        this.location = new ArrayList<Place>();
        this.sunk = false;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Place> getLocation() {
        return this.location;
    }

    public void setLocation(ArrayList<Place> location) {
        this.location = location;
    }

    public boolean isSunk() {
        //Traverse all the coordinates the ship is in
        for(Place currentPlace : location){
            //If one of the coordinates has not been hit yet, then the ship is not sunk
            if(!currentPlace.isHit()) return false;
        }
        //If we traverse all the coordinates then it means the ship has been sunk
        return true;
    }
    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }
}
