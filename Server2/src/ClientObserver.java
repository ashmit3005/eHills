
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends ObjectOutputStream implements ServerObserver {

    public ClientObserver(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            System.out.println("Sending to client: " + arg);
            writeObject(arg);
            this.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}