package edu.utep.cs.cs4330.battleship;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An abstraction of sending and receiving messages over a TCP/IP
 * or Bluetooth socket. This class allows two devices to communicate
 * with each other to play Battleship games.
 * It is assumed that a TCP/IP or Bluetooth connection is already
 * established between the two devices.
 *
 * <p>This class supports several types of messages.
 * Each message is a single line of text---a sequence of characters
 * ended by an end-of-line character---and consists of a header and a body.
 * A message header identifies a message type and ends with a ":", e.g.,
 * "shot:". A message body contains the content of a message.
 * If it contains more than one element, they are separated by a ",",
 * e.g., "4,5"; a complete message is "shot:4,5".
 * Listed below are eight different message types supported.
 * </p>
 *
 * <ul>
 *     <li>game: - request of a new game</li>
 *     <li>game_ack: n - acknowledgement of a game message, where n is
 *         either 0 or 1. If n is 1, the request is accepted; otherwise,
 *         it is declined.</li>
 *     <li>fleet: s1,x1,y1,d1,...,s5,x5,y5,d5 - fleet of five ships.
 *         This message denotes a deployment of the fleet of ships.
 *         Each ship's deployment is represented by a tuple of
 *         four numbers, si,xi,yi,di, where si: ship's size,
 *         (xi,yi): 0-based row and column indexes of the place,
 *         and di: direction (1 for horizontal and 0 for vertical).</li>
  *    <li>fleet_ack: n - acknowledgement of a fleet message, where n is
 *         either 0 (rejected) and 1 (accepted).</li>
 *     <li>turn: n - turn, where n is either 0 or 1. If n is 1,
 *         the opponent (receiver) has the first turn; otherwise, the
 *         player (sender) plays first.</li>
 *     <li>shot: x, y - shot, where (x,y) denotes the row and column
 *         indices of the place; 0-based indices are used.</li>
 *     <li>shot_ack: n, x, y - acknowledgement of a shot.
 *         If n is 1, it is accepted; otherwise, it is rejected.</li>
 *     <li>quit: - quit the game.</li>
 * </ul>
 *
 * <p>
 * The communication protocol between peers can be very simple.
 * For example, the client makes a play request to the server.
 * If the request is accepted by the server, they exchange fleet
 * deployments. One of the peers, say the server, determines
 * the turn, and the game proceeds by sending and receiving a series
 * of shot and shot_ack messages until one of the players wins or
 * quits.
 * </p>
 *
 * 1. Playing a game (the client has the first turn)
 * <pre>
 *  Client        Server
 *    |------------>| (connect)
 *    |------------>| fleet
 *    |<------------| fleet_ack
 *    |<------------| fleet
 *    |------------>| fleet_ack
 *    |<------------| turn: 1 - receiver plays first
 *    |------------>| shot
 *    |<------------| shot_ack
 *    |<------------| shot
 *    |------------>| shot_ack
 *    ...
 * </pre>
 *
 * 2. Playing a game (the server has the first turn)
 *  <pre>
 *  Client        Server
 *    |------------>| (connect)
 *    |------------>| fleet
 *    |<------------| fleet_ack
 *    |<------------| fleet
 *    |------------>| fleet_ack
 *    |<------------| turn: 0 - sender plays first
 *    |<------------| shot
 *    |------------>| shot_ack
 *    |------------>| shot
 *    |<------------| shot_ack
 *    ...
 * </pre>
 *
 * 3. Requesting a new game during play (accepted)
 * <pre>
 *  Client        Server
 *    | ...         |
 *    |------------>| shot
 *    |<------------| shot_ack
 *    |------------>| game: - request a new game
 *    |<------------| game_ack: 1 - accepted
 *    |------------>| fleet
 *    |<------------| fleet_ack
 *    ...
 * </pre>
 *
 * 4. Requesting a new game during play (declined)
 * <pre>
 *  Client        Server
 *    | ...         |
 *    |------------>| shot
 *    |<------------| shot_ack
 *    |<------------| game: - request a new game
 *    |------------>| game_ack: 0 - declined
 *    ...
 * </pre>
 *
 * 5. Quiting a game
 * <pre>
 *  Client        Server
 *    | ...         |
 *    |<------------| shot
 *    |------------>| shot_ack
 *    |------------>| quit
 * </pre>
 *
 * To receive messages from the peer, register a {@link MessageListener}
 * and then call the {@link #receiveMessagesAsync()} method as shown below.
 * This method creates a new thread to receive messages asynchronously.
 *
 * <p/>
 * <pre>
 *  Socket socket = ...; // or BluetoothSocket socket = ...
 *  NetworkAdapter network = new NetworkAdapter(socket);
 *  network.setMessageListener(new NetworkAdapter.MessageListener() {
 *      public void messageReceived(NetworkAdapter.MessageType type, int x, int y, int[] others) {
 *        switch (type) {
 *          case SHOT: ...
 *          case SHOT_ACK: ...
 *          case GAME: ...
 *          case GAME_ACK: ...
 *          ...
 *        }
 *      }
 *    });
 *
 *  // receive messages asynchronously
 *  network.receiveMessagesAsync();
 * </pre>
 *
 * To send messages to the peer, call the writeXXX messages. These
 * methods run asynchronously.
 *
 * <p/>
 * <pre>
 *  network.writeGame();
 *  network.writeShot(1,2);
 *  ...
 *  network.close();
 * </pre>
 *
 * @author cheon
 * @see MessageType
 * @see MessageListener
 */
public class NetworkAdapter {

    /** Different type of game messages. */
    public enum MessageType {

        /** Request of a new game. This message has the form "game:". */
        GAME ("game:"),

        /**
         * Acknowledgement of a game message. This message has the form
         * "game_ack: n", where n is either 0 or 1. If n is 1, then
         * the request is accepted; otherwise, it is declined.
         */
        GAME_ACK ("game_ack:"),

        /**
         * Feet deployment. This message has the form
         * "fleet: s1,x1,y1,d1,...,s5,x5,y5,d5".
         */
        FLEET ("fleet:"),

        /**
         * Acknowledgement of a fleet message. This message has the form,
         * "fleet: n", where n is either 1 or 0. If n is 1, the fleet
         * message is accepted; otherwise, it is rejected.
         */
        FLEET_ACK ("fleet_ack:"),

        /**
         * Player's turn. This message has the form "turn: n", where
         * n is either 0 or 1. If n is 1, the receiver plays first;
         * otherwise, the sender has the first turn.
         */
        TURN ("turn:"),

        /**
         * Player's shot. This message has the form "shot: x, y",
         * where x and y are 0-based column and row indices of the
         * place shot.
         */
        SHOT ("shot:"),

        /**
         * Acknowledgement of a shot message.
         * This message has the form "shot_ack: x, y, n", where x and
         * y are 0-based column and row indices, and n is either 0
         * or 1. If n is 1, the shot message is accepted; otherwise, it
         * is rejected.
         */
        SHOT_ACK ("shot_ack:"),

        /** Game quited. This message has the form "quit:". */
        QUIT ("quit:"),

        /**
         * Connection closed. This is not an actual message being
         * sent or received. It is used to inform the listener
         * {@link MessageListener} that the network connection is closed.
         */
        CLOSE ("close:"),

        /**
         * Unknown message. This is not an actual message being
         * sent or received. It is used to inform the listener
         * {@link MessageListener} that an unknown or invalid message
         * is received.
         */
        UNKNOWN ("unknown:");

        /** Message header. */
        public final String header;

        MessageType(String header) {
            this.header = header;
        }
    };

    /** Listener to be called when a message is received. */
    public interface MessageListener {

        /**
         * To be called when a message is received.
         * The type of the received message along with optional contents,
         * i.e., x, others, and z, are provided as arguments.
         */
        void messageReceived(MessageType type, int x, int y, int[] others);
    }

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    /** Listener to be called when a message is received. */
    private MessageListener listener;

    /**
     * Asynchronous message writer. Outgoing messages are written
     * asynchronously in a FIFO fashion, and their orders are
     * preserved. For this, a background thread is used.
     *
     * @see MessageWriter
     */
    private MessageWriter messageWriter;

    /** Reader to read messages from the connected peer. */
    private BufferedReader in;

    /** Writer to write messages to the connected peer. */
    private PrintWriter out;

    /** If not null, log all messages messages. */
    private PrintStream logger;

    /**
     * Create a new network adapter to read messages from and to write
     * messages to the given TCP/IP socket.
     */
    public NetworkAdapter(Socket socket) {
        this(socket, null);
    }

    /**
     * Create a new network adapter to read messages from and to write
     * messages to the given Bluetooth socket.
     */
    public NetworkAdapter(BluetoothSocket socket) {
        this(socket, null);
    }

    /**
     * Create a new network adapter. Messages are to be read from and
     * written to the given TCP/IP socket. All incoming and outgoing
     * messages will be logged on the given logger.
     */
    public NetworkAdapter(Socket socket, PrintStream logger) {
        this.logger = logger;
        messageWriter = new MessageWriter();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
        }
    }

    /**
     * Create a new network adapter. Messages are to be read from and
     * written to the given Bluetooth socket. All incoming and outgoing
     * messages will be logged on the given logger.
     */
    public NetworkAdapter(BluetoothSocket socket, PrintStream logger) {
        this.logger = logger;
        messageWriter = new MessageWriter();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
        }
    }

    /** Close the IO streams of this adapter. Note that the socket
     * to which the streams are attached is not closed by
     * this method. */
    public void close() {
        // close the output stream first to break a circular
        // dependency between the peers.
        out.close();
        try {
            in.close();
        } catch (Exception e) {
        }
        messageWriter.stop();
    }

    /**
     * Register the given messageListener to be notified when a message
     * is received.
     *
     * @see MessageListener
     * @see #receiveMessages()
     * @see #receiveMessagesAsync()
     */
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    /**
     * Start accepting messages from this network adapter and
     * notifying them to the registered listener. This method blocks
     * the caller. To receive messages synchronously, use the
     * {@link #receiveMessagesAsync()} method that creates a new
     * background thread.
     *
     * @see #setMessageListener(MessageListener)
     * @see #receiveMessagesAsync()
     */
    public void receiveMessages() {
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                if (logger != null) {
                    logger.format(" < %s\n", line);
                }
                parseMessage(line);
            }
        } catch (IOException e) {
        }
        notifyMessage(MessageType.CLOSE);
    }

    /**
     * Start accepting messages asynchronously from this network
     * adapter and notifying them to the registered listener.
     * This method doesn't block the caller. Instead, a new
     * background thread is created to read incoming messages.
     * To receive messages synchronously, use the
     * {@link #receiveMessages()} method.
     *
     * @see #setMessageListener(MessageListener)
     * @see #receiveMessages()
     */
    public void receiveMessagesAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMessages();
            }
        }).start();
    }

    /** Parse the given message and notify to the registered listener. */
    private void parseMessage(String msg) {
        if (msg.startsWith(MessageType.SHOT_ACK.header)) {
            parseShotMessage(MessageType.SHOT_ACK, msgBody(msg));
        } else if (msg.startsWith(MessageType.SHOT.header)) {
            parseShotMessage(MessageType.SHOT, msgBody(msg));
        } else if (msg.startsWith(MessageType.GAME_ACK.header)) {
            parseGameAckMessage(msgBody(msg));
        } else if (msg.startsWith(MessageType.GAME.header)) {
            notifyMessage(MessageType.GAME);
        } else if (msg.startsWith(MessageType.FLEET_ACK.header)) {
            parseFleetAckMessage(msgBody(msg));
        } else if (msg.startsWith(MessageType.FLEET.header)) {
            parseFleetMessage(msgBody(msg));
        } else if (msg.startsWith(MessageType.TURN.header)) { ///MADE IT ELSE IF
            parseTurnMessage(msgBody(msg));
        } else if (msg.startsWith(MessageType.QUIT.header)) {
            notifyMessage(MessageType.QUIT);
        } else {
            notifyMessage(MessageType.UNKNOWN);
        }
    }

    /** Parse and return the body of the given message. */
    private String msgBody(String msg) {
        int i = msg.indexOf(':');
        if (i > -1) {
            msg = msg.substring(i + 1);
        }
        return msg;
    }

    /** Parse and notify the given game_ack message body. */
    private void parseGameAckMessage(String msgBody) {
        String[] parts = msgBody.split(",");
        int response = parseInt(parts[0].trim()) == 0 ? 0 : 1;
        notifyMessage(MessageType.GAME_ACK, response);
    }

    /**
     * Parse the given string as an int; return -1 if the input
     * is not well-formed.
     */
    private int parseInt(String txt) {
        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return -1; // an error value
        }
    }

    /** Parse and notify the given fleet message body. */
    private void parseFleetMessage(String msgBody) {
        String[] parts = msgBody.split(",");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = parseInt(parts[i].trim());
        }
        notifyMessage(MessageType.FLEET, 0, 0, values);
    }

    /** Parse and notify the given fleet_ack message body. */
    private void parseFleetAckMessage(String msgBody) {
        String[] parts = msgBody.split(",");
        int response = Boolean.parseBoolean(parts[0].trim()) ? 1 : 0;
        notifyMessage(MessageType.FLEET_ACK, response);
    }

    /** Parse and notify the given shot or shot_ack message. */
    private void parseShotMessage(MessageType type, String msgBody) {
        String[] parts = msgBody.split(",");
        if (parts.length >= 2) {
            int x = parseInt(parts[0].trim());
            int y = parseInt(parts[1].trim());
            notifyMessage(type, x, y);
        }
        notifyMessage(MessageType.UNKNOWN);
    }

    /** Parse and notify the given turn message. */
    private void parseTurnMessage(String msgBody) {
        String[] parts = msgBody.split(",");
        int x = parseInt(parts[0].trim());
        notifyMessage(MessageType.TURN, x);
    }

    /** Write the given message asynchronously. */
    private void writeMsg(String msg) {
        messageWriter.write(msg);
    }

    /**
     * Write a game message asynchronously.
     *
     * @see #writeGameAck(boolean)
     */
    public void writeGame() {
        writeMsg(MessageType.GAME.header);
    }

    /**
     * Write a game_ack message asynchronously. If response is
     * true, the game message is accepted; otherwise, it is
     * rejected.
     *
     * @see #writeGame()
     */
    public void writeGameAck(boolean response) {
        writeMsg(MessageType.GAME_ACK.header + toInt(response));
    }

    /** Convert the given boolean value to an int. */
    private int toInt(boolean flag) {
        return flag ? 1: 0;
    }

    /**
     * Write a fleet message asynchronously. The argument is
     * of the form: s1,x1,y1,d1,...,s5,x5,y5,dn.
     * Each (si,xi,yi,di) tuple denotes a deployment of a ship,
     * where si: ship size, xi: 0-based row index,
     * yi: column index, and di: direction (1 for horizontal
     * and 0 for vertical).
     *
     * @see #writeFleetAck(boolean)
     */
    public void writeFleet(int[] ships) {
        StringBuilder builder = new StringBuilder();
        builder.append(MessageType.FLEET.header);
        for (int n: ships) {
            builder.append(n);
            builder.append(",");
        }
        if (ships.length > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        writeMsg(builder.toString());
    }

    /**
     * Write a fleet_ack message asynchronously. If response is
     * true, the fleet message is accepted; otherwise, it is
     * rejected.
     *
     * @see #writeFleet(int[])
     */
    public void writeFleetAck(boolean response) {
        writeMsg(MessageType.FLEET_ACK.header + toInt(response));
    }

    /**
     * Write a shot message asynchronously. The arguments are
     * the coordinate of the place that is shot; they are 0-based
     * row and column indices.
     *
     * @see #writeShotAck(boolean, int, int)
     */
    public void writeShot(int x, int y) {
        writeMsg(MessageType.SHOT.header + x + "," + y);
    }

    /**
     * Write a shot_ack message asynchronously. If response is true,
     * the specified shot is accepted; otherwise, it is rejected.
     *
     * @see #writeShot(int, int)
     */
    public void writeShotAck(boolean response, int x, int y) {
        writeMsg(MessageType.SHOT_ACK.header + toInt(response) + "," + x + "," + y);
    }

    /**
     * Write a turn message asynchronously. If turn is true,
     * the opponent (receiver) plays first; otherwise,
     * the player (sender) plays first.
     */
    public void writeTurn(boolean turn) {
        writeMsg(MessageType.TURN.header + toInt(turn));
    }

    /** Write a quit (gg) message asynchronously. */
    public void writeQuit() {
        writeMsg(MessageType.QUIT.header);
    }

    /** Notify the listener the receipt of the given message type. */
    private void notifyMessage(MessageType type) {
        listener.messageReceived(type, 0, 0, EMPTY_INT_ARRAY);
    }

    /** Notify the listener the receipt of the given message. */
    private void notifyMessage(MessageType type, int x) {
        listener.messageReceived(type, x, 0, EMPTY_INT_ARRAY);
    }

    /** Notify the listener the receipt of the given message. */
    private void notifyMessage(MessageType type, int x, int y) {
        listener.messageReceived(type, x, y, EMPTY_INT_ARRAY);
    }

    /** Notify the listener the receipt of the given message. */
    private void notifyMessage(MessageType type, int x, int y, int[] others) {
        listener.messageReceived(type, x, y, others);
    }

    /**
     * Write messages asynchronously. This class uses a single
     * background thread to write all outgoing messages asynchronously
     * in a FIFO fashion. To stop the background thread,
     * call the stop() method.
     */
    private class MessageWriter {

        /** Background thread to write messages asynchronously. */
        private Thread writerThread;

        /** Store messages to be written asynchronously. */
        private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        /** Write the given message asynchronously in a new thread. */
        public void write(final String msg) {
            if (writerThread == null) {
                writerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                String m = messages.take();
                                out.println(m);
                                out.flush();
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                });
                writerThread.start();
            }

            synchronized (messages) {
                try {
                    messages.put(msg);
                    if (logger != null) {
                        logger.format(" > %s\n", msg);
                    }
                } catch (InterruptedException e) {
                }
            }
        }

        /** Stop this message writer. */
        public void stop() {
            if (writerThread != null) {
                writerThread.interrupt();
            }
        }
    }
}