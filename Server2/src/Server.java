/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.Gson;

public class Server extends Observable {
    
    private ArrayList<Item> items; // serverside local database of items

    private ArrayList <SecureObject> logins; // serverside local database of logins

    public static void main(String[] args) {
        Server server = new Server();
        server.initItems();
        server.initLogins();
        server.configureNetworkConnection();
    }

    private void configureNetworkConnection() {
        final int port = 5000;
        try {
            ServerSocket serverSocket = new ServerSocket(port, 100, null);
            System.out.println("Server started at IP:" + serverSocket.getInetAddress().getHostAddress());
            while (true) {
                Socket clientSocket = serverSocket.accept(); // blocking statement
                ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
                InputStream in = clientSocket.getInputStream();
                ObjectInputStream reader = new ObjectInputStream(in);
                new Thread(()-> handleClient(this, clientSocket, reader, writer)).start();
                addObserver(writer);
                writer.update(this, "Hi! Welcome to EHills!");
                System.out.println("Got a connection");
            }
        } catch (IOException e) {
            System.out.println("Networking unsuccessful");
        }
    }

    private void handleClient(Server serv, Socket cs, ObjectInputStream reader, ClientObserver writer){
        Object obj;
        try{
            while (true) {
                if ((obj = reader.readObject()) != null){
                    System.out.println("From Client: " + obj);


                    if (obj instanceof ArrayList<?> && ((ArrayList <?>) obj).get(0) instanceof Item) { // the object received is an arrayList of items
                        serv.handleComplexRequest((ArrayList<Item>) obj); // can typecast as list of items now that we're inside the if block
                        // for complex requests such as processing incoming bids
                    }
                    else if (obj instanceof SecureObject){ // if receiving secure information (login)
                        // one-time communication, so observer pattern not necessary
                        // no need to notify, just write back
                        // we will send a boolean indicating whether the login attempt was successful
                        boolean loginSuccess = false;
                        ((SecureObject)obj).decode();
                        for  (SecureObject login : serv.getLogins()){
                            if (login.compareTo(obj) == 0){ // found within list of registered logins
                                loginSuccess = true;
                            }
                        }
                        writer.update(serv, new Boolean(loginSuccess));
                        if (loginSuccess){ // client has logged onto the server
                            // login successful, so give client initial item info (first store in local database)
                            writer.update(serv, serv.getItems());
                        }
                    }
                }

            }
        }
        catch(SocketException e){
            serv.deleteObserver(writer); // OOP??
            System.out.println("Lost connection with client");
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public synchronized void handleComplexRequest(ArrayList<Item> incomingItems){
        int idxIncoming = 0;
        for (Item i : items){
            if (i.getCurrBidPrice() <= incomingItems.get(idxIncoming).getCurrBidPrice()){
                items = incomingItems; // update server-local record of items
                for (Item j: items){
                    j.startBidTimer();
                }
                this.setChanged(); // Observable object has been changed. Notify registered observers. Raise the flag!!
                this.notifyObservers(this.items);
            }
            else{
                System.out.println("Bid rejected"); // handle outdated bids
            }
            idxIncoming++;
        }
    }



    private void initLogins() {
        logins = new ArrayList<SecureObject>();
        Gson gson = new Gson();
        File directoryPath = new File("members");
        String[] filenames = directoryPath.list();
        for (int i = 0; i < filenames.length; i++) {
            String filename = "members/" + filenames[i];
            try (Reader reader = new FileReader(filename)) {
                SecureObject login = gson.fromJson(reader, SecureObject.class);
               logins.add(login);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(logins);
    }

  
    private void initItems() {
        // init items from file
        items = new ArrayList<Item>();
        Gson gson = new Gson();
        File directoryPath = new File("items");
        String[] filenames = directoryPath.list();
        for (int i = 0; i < filenames.length; i++) {
            String filename = "items/" + filenames[i];
            try (Reader reader = new FileReader(filename)) {
                Item newItem = gson.fromJson(reader, Item.class);
                newItem.init();
                newItem.startBidTimer();
                items.add(newItem);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(items);
    }


    // Assorted getters/setters
    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<SecureObject> getLogins() {
        return logins;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void removeObserver(Observer writer) {
        this.deleteObserver(writer);
    }
}