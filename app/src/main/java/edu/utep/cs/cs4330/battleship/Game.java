//package edu.utep.cs.cs4330.battleship;
//
///**
// * Created by marcolopez on 2/28/17.
// */
//
//public class Game {
//
//    int numShots; //integer representing the number of shots human player has made
//    Player player1;
//    ComputerPlayer player2;
//    int currentTurn;
//
//    Game(){
//        player1 = new Player();
//        player2 = new ComputerPlayer();
//        numShots = 0;
//        currentTurn = 0;
//    }
//
//    public void makePlayerShot(Place place){
//        place.setHit(true);
//        if(!isGameOver() && !place.isShip()){
//            changeTurn();
//            new Thread(this::makeComputerShot).start();
//        }
//    }
//
//    private void makeComputerShot() {
//        try {
//            Thread.sleep(500);
//        }   catch (InterruptedException e) {
//        }
//
//        boolean hit = opponent().makeMove();
//    }
//
//    private boolean isGameOver(){
//        if(player1.allSunk() || player2.allSunk())
//            return true;
//        else return false;
//    }
//}
