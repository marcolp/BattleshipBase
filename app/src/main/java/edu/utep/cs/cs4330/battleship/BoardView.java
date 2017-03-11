package edu.utep.cs.cs4330.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** Marco Lopez
 * CS 5390 - Mobile Application Development
 * 2/14/2017
 *
 * A special view class to display a battleship board as a2D grid.
 *
 * This is the view class in MVC
 * @see Board
 */
public class BoardView extends View {
    /**=====================================BoardListener STUFF===========================================================*/
    /** Callback interface to listen for board touches. */
    public interface BoardTouchListener {

        /**
         * Called when a place of the board is touched.
         * The coordinate of the touched place is provided.
         *
         * @param x 0-based column index of the touched place
         * @param y 0-based row index of the touched place
         */
        void onTouch(int x, int y);
    }

    /**
     * Overridden here to detect a board touch. When the board is
     * touched, the corresponding place is identified,
     * and registered listeners are notified.
     *
     * @see BoardTouchListener
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int xy = locatePlace(event.getX(), event.getY());
                if (xy >= 0) {
                    notifyBoardTouch(xy / 100, xy % 100);
                    redraw();
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }



    /** Notify all registered listeners. */
    private void notifyBoardTouch(int x, int y) {
        for (BoardTouchListener listener: listeners) {
            listener.onTouch(x, y);
        }
    }
    /** Listeners to be notified upon board touches. */
    private final List<BoardTouchListener> listeners = new ArrayList<>();

    /** Register the given listener. */
    public void addBoardTouchListener(BoardTouchListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Unregister the given listener. */
    public void removeBoardTouchListener(BoardTouchListener listener) {
        listeners.remove(listener);
    }
    /**=====================================BoardListener STUFF===========================================================*/

    /**=====================================PAINT STUFF===========================================================*/
    /** Board background color. */
    private final int boardColor = Color.rgb(102, 163, 255);

    /** Board background paint. */
    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);{
        boardPaint.setColor(boardColor);
    }

    /** Shot color*/
    private final int shotColor = Color.rgb(0, 25, 255);

    /**Shot paint*/
    private final Paint shotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);{
        shotPaint.setColor(shotColor);
    }

    /** Ship color*/
    private final int shipColor = Color.rgb(255, 25, 0);

    /**Ship paint*/
    private final Paint shipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);{
        shipPaint.setColor(shipColor);
    }

    /** Board grid line color. */
    private final int boardLineColor = Color.WHITE;

    /** Board grid line paint. */
    private final Paint boardLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);{
        boardLinePaint.setColor(boardLineColor);
        boardLinePaint.setStrokeWidth(2);
    }

    /** Green color*/
    private final int greenColor = Color.rgb(100, 200, 50);
    private final Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);{
        greenPaint.setColor(greenColor);
        greenPaint.setStrokeWidth(2);
    }


    /**=====================================PAINT STUFF===========================================================*/

    /**=====================================CLASS FIELDS===========================================================*/
    /**TextView displaying the amount of shots made.*/
    private TextView shots;

    /**Button for new game to be created.*/
    private Button newGame;

    /** Size of the board. */
    private int boardSize;
    /**=====================================CLASS FIELDS===========================================================*/

    /**=====================================CLASS CONSTRUCTORS===========================================================*/
    /** Create a new board view to be run in the given context. */
    public BoardView(Context context) {
        super(context);
    }

    /** Create a new board view with the given attribute set. */
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Create a new board view with the given attribute set and style. */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**=====================================CLASS CONSTRUCTORS===========================================================*/

    /**=====================================BUTTON LISTENER STUFF===========================================================*/

    public boolean setNewButton(Button newGameButton){
        this.newGame = newGameButton;
        if (this.newGame == null) return false;
        else return true;
    }

    public void setButtonListener(OnClickListener buttonListener) {
        this.newGame.setOnClickListener(buttonListener);
    }
    /**=====================================BUTTON LISTENER STUFF===========================================================*/

    /**=====================================DRAWING STUFF===========================================================*/
    /** Overridden here to draw a 2-D representation of the board. */
    private Boolean firstActivity;

    public Boolean getFirstActivity() {
        return firstActivity;
    }

    public void setFirstActivity(Boolean firstActivity) {
        this.firstActivity = firstActivity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawPlaces(canvas);
    }

    protected void redraw(){
        invalidate();
    }

    /** Draw all the places of the board. */
    private void drawPlaces(Canvas canvas) {

        if(firstActivity){
            drawShips(canvas);
        }

        else {
//         check the state of each place of the board and draw it.
            for (Place currentPlace : board.places()) {
                boolean flag = currentPlace.isHit();
                if (flag) {
                    Paint toPaint = new Paint(0);
                    if (currentPlace.isShip()) {
                        toPaint = shipPaint;
                    } else {
                        toPaint = shotPaint;
                    }
                    canvas.drawRect((currentPlace.getX()) * lineGap() + 2, (currentPlace.getY()) * lineGap() + 2, (currentPlace.getX() + 1) * lineGap(), (currentPlace.getY() + 1) * lineGap(), toPaint);
                }
            }
        }
    }

    /**
     * Only draw ships on grid.
     * Green if they are in a valid position, red if they overlap with another ship
     * @param canvas
     */
    private void drawShips(Canvas canvas){
        for (Place currentPlace : board.places()) {
            if (currentPlace.isShip()) {
                canvas.drawRect((currentPlace.getX()) * lineGap() + 2, (currentPlace.getY()) * lineGap() + 2, (currentPlace.getX() + 1) * lineGap(), (currentPlace.getY() + 1) * lineGap(), greenPaint);

                //TODO ship outline
//                canvas.drawLine(currentPlace.getX(), currentPlace.getY(), currentPlace.getX()*lineGap(), currentPlace.getY()+2, shotPaint); // horizontal line
//                canvas.drawLine(xy, 0, xy, maxCoord, boardLinePaint); // vertical line
            }
        }
    }


    /** Draw horizontal and vertical lines. */
    private void drawGrid(Canvas canvas) {
        final float maxCoord = maxCoord();
        final float placeSize = lineGap();
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        for (int i = 0; i < numOfLines(); i++) {
            float xy = i * placeSize;
            canvas.drawLine(0, xy, maxCoord, xy, boardLinePaint); // horizontal line
            canvas.drawLine(xy, 0, xy, maxCoord, boardLinePaint); // vertical line
        }
    }
    /**=====================================DRAWING STUFF===========================================================*/

    /**=====================================DIALOG STUFF============================================================*/
    private AlertDialog gameOverDialog;
    private AlertDialog newGameDialog;

//    public class NewGameDialogFragment extends DialogFragment {
//
//        /* The activity that creates an instance of this dialog fragment must
//         * implement this interface in order to receive event callbacks.
//         * Each method passes the DialogFragment in case the host needs to query it. */
//        public interface NewGameDialogFragment {
//            public void onDialogPositiveClick(DialogFragment dialog);
//            public void onDialogNegativeClick(DialogFragment dialog);
//        }
//
//        // Use this instance of the interface to deliver action events
//        NewGameDialogFragment mListener;
//
//        // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            // Verify that the host activity implements the callback interface
//            try {
//                // Instantiate the NoticeDialogListener so we can send events to the host
//                mListener = (NewGameDialogFragment) activity;
//            } catch (ClassCastException e) {
//                // The activity doesn't implement the interface, throw exception
//                throw new ClassCastException(activity.toString()
//                        + " must implement NoticeDialogListener");
//            }
//        }
//
//    }
    public void createGameOverDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this.getContext());
        builder1.setTitle("Game Over!");
        builder1.setMessage("All ships sunk! You Win!");
        builder1.setCancelable(true);

        gameOverDialog = builder1.create();
        gameOverDialog.show();
    }

    public void createNewGameDialog(){

//        AlertDialog.Builder builder2 = new AlertDialog.Builder(this.getContext());
//        builder2.setTitle("New Game");
//        builder2.setMessage("All progress will be lost. Continue?");
//        builder2.setCancelable(true);
//
//        builder2.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        builder2.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        newGameDialog = builder2.create();
//        newGameDialog.show();
    }
    /**=====================================DIALOG STUFF===========================================================*/

    private Board board;



    /** Set the board to to be displayed by this view. */
    public void setBoard(Board board) {
        this.board = board;
        this.boardSize = board.size();
    }



    /** Calculate the gap between two horizontal/vertical lines. */
    protected float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }

    /** Calculate the number of horizontal/vertical lines. */
    private int numOfLines() {
        return boardSize + 1;
    }

    /** Calculate the maximum screen coordinate. */
    protected float maxCoord() {
        return lineGap() * (numOfLines() - 1);
    }

    /**
     * Given screen coordinates, locate the corresponding place in the board
     * and return its coordinates; return -1 if the screen coordinates
     * don't correspond to any place in the board.
     * The returned coordinates are encoded as <code>x*100 + y</code>.
     */
    private int locatePlace(float x, float y) {
        if (x <= maxCoord() && y <= maxCoord()) {
            final float placeSize = lineGap();
            int ix = (int) (x / placeSize);
            int iy = (int) (y / placeSize);
            return ix * 100 + iy;
        }
        return -1;
    }
}