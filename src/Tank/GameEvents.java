package Tank;


import java.util.Observable;

public class GameEvents extends Observable {

    Object event;

    public void setValue(String msg) {
        event = msg;
        setChanged();

        notifyObservers(this);
    }
}