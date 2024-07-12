
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Item implements Serializable {


    private String name; // name of item
    private double startBid; // starting bid price for the item
    private Integer timeLeft; // remaining time to place a bid
    private boolean sold; // item sold? T/F
    private String soldTo; // name of customer who item is sold to
    private double currBidPrice; //
    private double value; // price to buy item at any moment
    private String highestBidder; // name of highest bidding customer


    // constructor
    public Item (String name, int timeLeft, double startBid, double value){
        this.name = name;
        this.startBid = startBid;
        this.value = value; //essentially final variables


        this.timeLeft = timeLeft;
        this.currBidPrice = startBid;
        this.sold = false;
        this.soldTo = "N/A";
        this.highestBidder = "N/A";
    }


    public void startBidTimer(){
        Timer timer = new Timer();
        // 1 second delay and 1 second period so timer resets every second and maintains state for exactly 1 sec
        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                } else {
                    setSold(); // item has been sold
                }
            }}, 1000, 1000);
    }




    public void update(Item item) {
        this.sold = item.isSold();
        this.currBidPrice = item.getCurrBidPrice();
        this.timeLeft = item.getTimeLeft();
        this.highestBidder = item.getHighestBidder();
    }


    // assorted setters/ getters
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public double getCurrBidPrice() {
        return currBidPrice;
    }


    public void setCurrBidPrice(double currBidPrice) {
        this.currBidPrice = currBidPrice;
    }


    public double getStartBid() {
        return startBid;
    }


    public void setStartBid(double startBid) {
        this.startBid = startBid;
    }


    public double getValue() {
        return value;
    }


    public void setValue(double value) {
        this.value = value;
    }


    public Integer getTimeLeft() {
        return timeLeft;
    }


    public void setTimeLeft(int timeLeft) {this.timeLeft = timeLeft;}


    public String getHighestBidder() {
        return highestBidder;
    }


    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }


    public boolean isSold() {
        return sold;
    }


    public void setSold() {
        this.sold = true;
    }


    @Override
    public String toString() {
        return "ITEM: " + "name= " + name + " | "
                + "sold= " + sold + " | "
                + "timeLeft= " + timeLeft + " | "
                + "startBid= " + startBid + " | "
                + "value= " + value + " | "
                + "currBidPrice= " + currBidPrice + " | "
                + "highestBidder= "+ highestBidder;
    }

    // Server initialization called after GSON parsing
    public void init() {
        this.currBidPrice = this.startBid - 0.01;
        this.sold = false;
        this.highestBidder = "NA";
    }

}
