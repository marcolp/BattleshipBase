package edu.utep.cs.cs4330.battleship;

import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * This is the controller in the MVC
 */
public class MainActivity extends AppCompatActivity{

    Ship currentShip;
    Player humanPlayer;
    BoardView humanBoardView;
    Board humanBoard;
    final int boardSize = 10;
    RectangleDrawableView rectView;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main Activity", "This is the onCreate method");

        setContentView(R.layout.activity_main);

        humanPlayer = new Player();

        humanBoard = new Board(boardSize);


        humanPlayer.setMyBoard(humanBoard);

        humanBoardView = (BoardView) findViewById(R.id.boardView);
        humanBoardView.setBoard(humanBoard);
        humanBoardView.setFirstActivity(true);
        initialPlacing();
        rectView = new RectangleDrawableView(this);

//        humanBoardView.addBoardTouchListener(new BoardView.BoardTouchListener() {
//            @Override
//            public void onTouch(int x, int y) {
//                Place paceShot = humanBoard.getPlace(x,y);
//                if(!paceShot.isHit()) {
//                    /**Make a shot on the humanBoard*/
//                    humanBoard.makeShot(x, y);
//
//
//                        if (paceShot.isShip()) {
//                            soundPool.play(2, 1, 1, 1, 0, 1.0f);
//                            if (humanBoard.getShip(paceShot).isSunk()) {
//                                soundPool.play(3, 1, 1, 1, 0, 1.0f);
//                            }
//                        }
//
//
//                        boardView.createGameOverDialog();
//                        humanBoard = new Board(boardSize);
//                        boardView.setBoard(humanBoard);
//                        boardView.redraw();
//                        boardView.updateShotNumber(0);
//
//                        toast(String.format("Touched: %d, %d", x, y));
//
//                }
//            }
//        });

        spinner = (Spinner) findViewById(R.id.ship_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ships_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    //Aircraft carrier
                    case 0:
                        Toast.makeText(getApplicationContext(), "Selected Aircraft Carrier", Toast.LENGTH_SHORT).show();
                        currentShip = humanPlayer.getMyShips().get(0);
                        break;

                    //Battleship
                    case 1:
                        Toast.makeText(getApplicationContext(), "Selected Battleship", Toast.LENGTH_SHORT).show();
                        currentShip = humanPlayer.getMyShips().get(1);
                        break;

                    //Frigate
                    case 2:
                        Toast.makeText(getApplicationContext(), "Selected Frigate", Toast.LENGTH_SHORT).show();
                        currentShip = humanPlayer.getMyShips().get(2);
                        break;

                    //Submarine
                    case 3:
                        Toast.makeText(getApplicationContext(), "Selected Submarine", Toast.LENGTH_SHORT).show();
                        currentShip = humanPlayer.getMyShips().get(3);
                        break;

                    //Minesweeper
                    case 4:
                        Toast.makeText(getApplicationContext(), "Selected Mineseeper", Toast.LENGTH_SHORT).show();
                        currentShip = humanPlayer.getMyShips().get(4);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        spinner;



    }

    /**
     * Move buttons method
     * @param view
     */
    public void moveShip(View view){
        switch(view.getId()){
            case R.id.move_left:

                break;

            case R.id.move_right:
                break;

            case R.id.move_up:
                break;

            case R.id.move_down:
                break;

            case R.id.rotateLeft:
                rotate(currentShip);
                humanBoardView.redraw();
                break;

            case R.id.rotateRight:
                rotate(currentShip);
                humanBoardView.redraw();
                break;
        }
    }

    /**
     * Called when user pushes the "DONE" button
     * @param view
     */
    public void placeShips(View view){

    }

    /**
     * Place ships in original position
     */
    private void initialPlacing(){
        int count = 0;
        for(Ship currentShip : humanPlayer.myShips){
            for(int i = 0; i < currentShip.getSize(); i++){
                Place currentPlace = humanBoard.getPlace(i,count);
                currentPlace.setShip(true);
                currentShip.getLocation().add(currentPlace);
            }
            count++;
        }
    }

    /**
     * Change ship orientation
     * @return
     */
    public boolean rotate(Ship rotatingShip){

        int amount = rotatingShip.getSize()/2;

        //If the ship is horizontal then we will add the amount to the places of the ship
        //Otherwise we will subtract it
        if(!rotatingShip.isOrientation()){
            amount = -amount;
        }

            //Traverse each place checking whether or not each place will go out of bounds from the board
            for(Place currentPlace : rotatingShip.getLocation()){
                int row = currentPlace.getX();
                int col = currentPlace.getY();

                //If the new coordinates for the place go out of bounds return true and display a message
                if(row + amount > humanBoard.getSize()-1 || row + amount < 0 || col + amount > humanBoard.getSize()-1 || col + amount < 0) {
                    Toast.makeText(getApplicationContext(), "Invalid Rotation, out of board.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                //Check to see if the new place has a colliding ship meaning it cannot be rotated that way.
                //Don't check when amount is 0 though because it would say that it collided with itself.
                else if(amount != 0) {
                    if (humanBoard.getPlace(row + amount, col + amount).isShip()) {
                        Toast.makeText(getApplicationContext(), "Invalid Rotation, other ship in the way.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                if (rotatingShip.isOrientation())
                    amount--;
                else
                    amount++;
                }


        //Actually change the places
        amount = rotatingShip.getSize()/2;
        if(!rotatingShip.isOrientation()){
            amount = -amount;
        }
            ArrayList<Place> newLocations = new ArrayList<Place>();

            for(Place currentPlace : rotatingShip.getLocation()){
                currentPlace.setShip(false);
                Place newPlace = humanBoard.getPlace(currentPlace.getX() + amount, currentPlace.getY() + amount);
                newPlace.setShip(true);
                newLocations.add(newPlace);
                if(rotatingShip.isOrientation())
                    amount--;

                else
                    amount++;
            }

            currentShip.setLocation(newLocations);
            rotatingShip.setOrientation(!rotatingShip.isOrientation());
            return true;
    }
}
