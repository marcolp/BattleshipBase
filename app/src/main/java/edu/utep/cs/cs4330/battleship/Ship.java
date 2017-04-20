package edu.utep.cs.cs4330.battleship;

import java.io.Serializable;
import java.util.ArrayList;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 */

public class Ship implements Serializable{


    public enum Type {
        AIRCRAFT_CARRIER("Aircraft Carrier"), BATTLESHIP("Battleship"), FRIGATE("Frigate"), SUBMARINE("Submarine"), MINESWEEPER("Minesweeper");
        private String name;
        private Type(String value){
            this.name = value;
        }
    }


    private boolean orientation; //true for horizontal, false for vertical. Default true
    private Type shipType;
    private int size;
    private ArrayList<Place> location;
    private boolean sunk;

    public Ship(){
        this.size = -1;
        this.location = new ArrayList<Place>();
        this.sunk = false;
        shipType = null;
    }

    public Ship(int size){
        this.size = size;
        this.location = new ArrayList<Place>();
        this.sunk = false;
        orientation = true;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public Type getShipType() {
        return shipType;
    }

    public void setShipType(Type shipType) {
        this.shipType = shipType;
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
        //Make the old places not have a place anymore
        for(Place currentPlace : this.location){
            currentPlace.setHit(false);
        }

        //Make the new places a ship
        for(Place currentPlace : location){
            currentPlace.setShip(true);
        }

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
