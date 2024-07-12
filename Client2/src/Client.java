
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    // only going to write to server in this class
    // incoming information from server will be handled in a new thread initialized for every Client member

    private String name = " "; // name of the client (customer)
    
    // I/0 and networking objects
    private final Socket socket;
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private final GUI clientGUI; // user interface

    private ArrayList<Item> items; // client local items database
    
    private Boolean loginStatus = null;

 

    // constructor
    public Client(String name, Socket socket, ObjectOutputStream send, ObjectInputStream receive, GUI gui) {
        this.name = name;
        this.socket = socket;
        writer = send;
        reader = receive;
        items = new ArrayList<Item>();
        this.clientGUI = gui;
        // start thread to check for communication from server
        // new thread for each customer (client)
       new Thread(new ReadIncoming(this, receive)).start(); // anonymous thread for reading

    }

    public void setLoginStatus(Boolean incomingBool){
        String consoleMessage = this.getName() + "'s login attempt was unsuccessful";;
        this.loginStatus = incomingBool;
        if (incomingBool){
            consoleMessage = this.getName() + "'s login attempt was successful";
        }
        System.out.print(consoleMessage);
    }

    public String getName() {
        return this.name;
    }

    // update clientside local items database after comm with server
    // to be called in ReadIncoming
    public void handleItemChange(ArrayList<Item> incomingItems){
        if (items.isEmpty()) { // if first time hearing back from server (observed change in server)
            items = incomingItems;
        }
        else {
            int count = 0;
            for (Item i : items){
                i.update(incomingItems.get(count)); // update all attributes of each item using the item method update
                count++;
            }
        }
        // update GUI
        clientGUI.updateItems();
    }


    public void flushItemsData() throws IOException {
        System.out.println("To server: " + items);
        writer.writeObject(items);
        writer.flush();
        writer.reset();
    }


    public void flushLoginAttempt(SecureObject login) {
        try {
            writer.writeObject(login);
            writer.flush();
            writer.reset(); // reset
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void resetLogin() {
        loginStatus = null;
    }


    public synchronized String handleBid (Item item, String input){

        // check if item is still available
        boolean itemStatus = item.isSold();
        if (itemStatus){
            return "Bid Unsuccessful: Item no longer available.";
        }
        else{ // item not sold yet
            try{
                double bidVal;
                // Define a pattern for extracting numeric values
                Pattern pattern = Pattern.compile("\\d+\\.?\\d*");

                // Create a matcher with the input text
                Matcher matcher = pattern.matcher(input);

                // Find the numeric value in the text
                if (matcher.find()) {
                    // Parse the matched value to double
                    bidVal = Double.parseDouble(matcher.group());
                }
                else {
                    // Handle the case when no numeric value is found
                    throw new IllegalArgumentException("No numeric value found in the text");
                }
                if (!(bidVal > item.getCurrBidPrice())){
                    return "Bid unsuccessful: attempted bid value is too low.";
                }
                else if (!(bidVal < item.getValue())){
                    return "Bid unsuccessful: attempted bid value is too high.";
                }
                else{ // valid bid, update server

                    item.setHighestBidder(this.name);
                    item.setCurrBidPrice(bidVal); // new bid price is the last successful bid
                    if (item.getTimeLeft() < 15){ // if less than 15 seconds were remaining, reset countdown back to 15
                        item.setTimeLeft(15);
                    }
                    this.flushItemsData();
                    return "Bid Placed: " + item.getName() + " for " + input;
                }

            }catch(NumberFormatException e) {
                return "Bid Unsuccessful: Unsupported input.";
            }
            catch(IOException e){
                return "Bid Unsuccessful: Write to server failed.";
            }

        }

    }

    static DecimalFormat priceFormat = new DecimalFormat("0.00");

    public synchronized String handleBuyNow(Item item){
        boolean itemStatus = item.isSold();
        if (itemStatus){ // item already sold
            return "Buy Now unsuccessful: item is no longer available.";
        }
        else{
            try{ // item not yet sold, valid buy now, update server

                item.setSold();
                item.setHighestBidder(this.name);
                item.setCurrBidPrice(item.getValue());
                this.flushItemsData();
                return "Buy now successful: " + item.getName() + " sold for $" + priceFormat.format(item.getValue());
            }
            catch(IOException e){
                return "Buy Now unsuccessful: Write to server failed.";
            }
        }

    }

    // Assorted setters/getters
    public ArrayList<Item> getItems() {
        return items;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getLoginStatus() {
        return loginStatus;
    }



}
