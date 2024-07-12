
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import java.io.*;
import java.util.ArrayList;

public class ReadIncoming implements Runnable {
    Client client;
    ObjectInputStream reader;

    // constructor
    public ReadIncoming (Client c, ObjectInputStream reader){
        client = c;
        this.reader = reader;
    }

    @Override
    public void run(){
        Object incoming; // we expect any object type
        try{
            while ((incoming = reader.readObject()) != null){

                System.out.println("From Server: " + incoming); // print to console for debugging

                if (incoming instanceof ArrayList<?>){
                    ArrayList<?> list = (ArrayList<?>) incoming;
                    if (!list.isEmpty()) {
                        Object firstElement = list.get(0);
                        if (firstElement != null && firstElement instanceof Item) {
                            client.handleItemChange((ArrayList<Item>)incoming); // typecasting necessary to call method
                        }
                    }
                }
                else if (incoming instanceof Boolean){ // if incoming yes/no
                    client.setLoginStatus((Boolean) incoming);
                }

            }
        }
        catch (IOException e){
            System.out.println("Lost connection to server");
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid input");
            throw new RuntimeException(e);
        }
    }
}