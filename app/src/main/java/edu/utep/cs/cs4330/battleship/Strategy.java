package edu.utep.cs.cs4330.battleship;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * Created by LopezMarcoA on 3/9/2017.
 */

public class Strategy {



    Stack<Place> targetPlaces;
    ArrayList<Place> toAttack;

    Board attackBoard;

    public Strategy(Board attackBoard) {
        targetPlaces = new Stack<Place>();
        toAttack = new ArrayList<Place>();
        this.attackBoard = attackBoard;
    }

    /**
     * Look for a random location to attack or use target
     *
     * @return
     */
    public boolean makeMove(){

        Place toShoot;

        Boolean hit = false;

        if(targetPlaces.isEmpty()) {
            Random rand = new Random();
            int attackIndex = rand.nextInt(toAttack.size() - 1);
            toShoot = toAttack.get(attackIndex);
        }

        else {
            toShoot = targetPlaces.pop();
        }

        if (toShoot.isShip()) {
            hit = true;
            int col = toShoot.getX();
            int row = toShoot.getY();

            if (col + 1 <= attackBoard.getSize() - 1 && toAttack.contains(attackBoard.getPlace(col + 1, row))) {
                Place possibleShip = attackBoard.getPlace(col + 1, row);
                targetPlaces.add(possibleShip);
                toAttack.remove(possibleShip);
            }

            if (col - 1 >= 0 && toAttack.contains(attackBoard.getPlace(col - 1, row))) {
                Place possibleShip = attackBoard.getPlace(col - 1, row);
                targetPlaces.add(possibleShip);
                toAttack.remove(possibleShip);            }

            if (row + 1 <= attackBoard.getSize() - 1 && toAttack.contains(attackBoard.getPlace(col, row + 1))) {
                Place possibleShip = attackBoard.getPlace(col, row + 1);
                targetPlaces.add(possibleShip);
                toAttack.remove(possibleShip);            }

            if (row - 1 >= 0 && toAttack.contains(attackBoard.getPlace(col, row - 1))) {
                Place possibleShip = attackBoard.getPlace(col, row - 1);
                targetPlaces.add(possibleShip);
                toAttack.remove(possibleShip);
            }
        }

        toShoot.setHit(true);
        toAttack.remove(toShoot);

        return hit;
    }

    public void setUp(){
        getToAttack().addAll(attackBoard.places());
    }

    public Stack<Place> getTargetPlaces() {
        return targetPlaces;
    }

    public void setTargetPlaces(Stack<Place> targetPlaces) {
        this.targetPlaces = targetPlaces;
    }

    public ArrayList<Place> getToAttack() {
        return toAttack;
    }

    public void setToAttack(ArrayList<Place> toAttack) {
        this.toAttack = toAttack;
    }


    public Board getAttackBoard() {
        return attackBoard;
    }

    public void setAttackBoard(Board attackBoard) {
        this.attackBoard = attackBoard;
    }
}
