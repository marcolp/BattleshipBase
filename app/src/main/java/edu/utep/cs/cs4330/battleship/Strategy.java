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

            if (col + 1 < attackBoard.getSize() - 1) {
                targetPlaces.add(attackBoard.getPlace(row, col + 1));
            }

            if (col - 1 > 0) {
                targetPlaces.add(attackBoard.getPlace(row, col - 1));
            }

            if (row + 1 < attackBoard.getSize() - 1) {
                targetPlaces.add(attackBoard.getPlace(row + 1, col));
            }

            if (!(row - 1 > 0)) {
                targetPlaces.add(attackBoard.getPlace(row - 1, col));
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
