package edu.utep.cs.cs4330.battleship;

/**
 * Created by marcolopez on 2/2/17.
 */

public class Place {
    private int x;
    private int y;
    private boolean hit;
    private boolean ship; //Is there a part of a ship in this coordinate

    public Place(){
        this.x = -1;
        this.y = -1;
        this.hit = false;
        this.ship = false;
    }

    public Place(int col, int row){
        this.x = col;
        this.y = row;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isShip() {
        return ship;
    }

    public void setShip(boolean ship) {
        this.ship = ship;
    }
}
