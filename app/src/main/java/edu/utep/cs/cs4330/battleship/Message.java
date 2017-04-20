package edu.utep.cs.cs4330.battleship;

/**
 * Created by malopez25 on 4/17/2017.
 */

public class Message {

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    /** Different type of game messages. */
    public enum MessageType {

        GAME ("game:"),

        GAME_ACK ("game_ack:"),

        FLEET ("fleet:"),

        FLEET_ACK ("fleet_ack:"),

        TURN ("turn:"),

        SHOT ("shot:"),

        SHOT_ACK ("shot_ack:"),

        QUIT ("quit:"),

        CLOSE ("close:"),

        UNKNOWN ("unknown:");

        /** Message header. */
        public final String header;

        MessageType(String header) {
            this.header = header;
        }
    };




    MessageType type;
    int x;
    int y;
    int [] others;

    public Message(){
        type = MessageType.UNKNOWN;
        x = 0;
        y = 0;
        others = EMPTY_INT_ARRAY;
    }

    public Message(MessageType newType){
        type = newType;
        x = 0;
        y = 0;
        others = EMPTY_INT_ARRAY;
    }

    public Message(MessageType newType, int x){
        type = newType;
        this.x = x;
        y = 0;
        others = EMPTY_INT_ARRAY;
    }

    public Message(MessageType newType, int x, int y){
        type = newType;
        this.x = x;
        this.y = y;
        others = EMPTY_INT_ARRAY;
    }

    public Message(MessageType newType, int x, int y, int [] other){
        type = newType;
        this.x = x;
        this.y = y;
        this.others = other;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
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

    public int[] getOthers() {
        return others;
    }

    public void setOthers(int[] others) {
        this.others = others;
    }

}
