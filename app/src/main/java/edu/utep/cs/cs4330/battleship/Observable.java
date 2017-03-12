package edu.utep.cs.cs4330.battleship;

/**
 * Created by LopezMarcoA on 3/12/2017.
 */

public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObserver();
}
