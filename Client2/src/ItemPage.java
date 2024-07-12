
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */


import javafx.animation.AnimationTimer;;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;



import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Optional;

import javafx.scene.paint.Color;
//import org.controlsfx.control.Notifications;
//import org.controlsfx.control.textfield.CustomTextField;
//import org.controlsfx.control.textfield.TextFields;


public class ItemPage {


    // client member to communicate back and forth
    private final Client client;


    // item this GUI represents
    private final Item item;


    // decimal formatter
    private static final DecimalFormat priceFormat = new DecimalFormat("0.00");


    // JavaFX fields to be changed when updated
    private TextField highestBidderField;
    private TextField currBidPriceField;
    private TextField bidCountdownTimerField;
    private TextField userInputBidField;

    private TextArea chatTextArea;

    private Image backgroundImage;

    private ObservableList<String> historyList = FXCollections.observableArrayList();
    private ListView<String> historyListView;

    private final Object historyLock = new Object(); // Object for synchronization

    // Import the confetti library
    private static final int NUM_CONFETTI = 100;
//    private final Confetti confetti = new Confetti(NUM_CONFETTI);


    // Buttons to be disabled when sold
    private Button bid;
    private Button buyNow;


    // constructor
    public ItemPage(Item currentItem, Client client) {
        this.item = currentItem;
        this.client = client;
    }


    public GridPane initDisplay() {



        bid = new Button("Bid");
        buyNow = new Button("Buy Now: $" + priceFormat.format(item.getValue()));


        GridPane maingridpane = new GridPane();
        maingridpane.setHgap(10);

        historyListView = new ListView<>(historyList);
        historyListView.setPrefHeight(300);
        maingridpane.add(historyListView, 4, 0, 1, 14);
        
        // prepare to display section 1
        Label itemInfo = new Label("Item Information: ");

        // prepare to display section 2
        Label startBidPrice = new Label("Starting Price: $" + priceFormat.format(item.getStartBid()));
        Label buyNowPrice = new Label("Buy Now Price: $" + priceFormat.format(item.getValue()));
        Label itemLabel = new Label("Name: " + item.getName());
        Label softMargin = new Label("====================================");
        
        // prepare to display section 3
        Label currItemInfo = new Label("Current Information:");


        // section 2
        Label highestBidderFieldLabel = new Label("Highest Bidder: ");
        highestBidderField = new TextField(item.getHighestBidder());
        highestBidderField.setEditable(false);
        
        Label currBidPriceFieldLabel = new Label("Current Bid: ");
        currBidPriceField = new TextField("$N/A");
        if (item.getCurrBidPrice() >= item.getStartBid()) {
            currBidPriceField.setText("$" + priceFormat.format(item.getCurrBidPrice()));
        }
        currBidPriceField.setEditable(false);


        Label timerLabel = new Label("Time left: ");
        bidCountdownTimerField = new TextField("" + item.getName() + "s");
        bidCountdownTimerField.setEditable(false);



        // timer animation countdown
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!item.isSold()) {
                    bidCountdownTimerField.setText(item.getTimeLeft() + " s");
                } else {
                    bidCountdownTimerField.setText("Item is sold!");


                    // If item is sold, disable buttons and reset the field taking user bid inputs.
                    bid.setDisable(true);
                    buyNow.setDisable(true);
                    userInputBidField.clear();


                }
            }
        }.start();


        Label custActivityLabel = new Label("Your move: ");
        userInputBidField = new TextField();

        double bidVal = item.getCurrBidPrice() + item.getValue() / 10.0;
        if (bidVal >= item.getValue()) {
            bidVal = item.getValue() - 0.01;
        } else if (item.getCurrBidPrice() < item.getStartBid()) {
            bidVal = item.getStartBid();
        }

        userInputBidField.setText("$" + priceFormat.format(bidVal));


        Button exit = new Button("Exit");


        bid.setOnAction(event -> {

            String displayText = client.handleBid(item, userInputBidField.getText());

            Stage bidResultStage = new Stage();
            bidResultStage.getIcons().add(new Image(getClass().getResourceAsStream("")));

            BorderPane borderpane =new BorderPane(new Label(displayText));

            bidResultStage.setScene(new Scene(borderpane, 400, 100));
            bidResultStage.getScene().getStylesheets().add("stylesheet/ GUIStyles.css"); // customize using css
            bidResultStage.setTitle("Bid Sent: " + client.getName() + " | " + item.getName());
            bidResultStage.show();

            synchronized (historyLock) {
                historyList.add(displayText);
            }





        });

        buyNow.setOnAction(event -> {

            // Display item image and confetti on successful purchase

                showItemBoughtStage();
                //showConfettiAnimation();

            String displayText = client.handleBuyNow(item);
            Stage buynowResultStage = new Stage();
            buynowResultStage.getIcons().add(new Image(getClass().getResourceAsStream("")));

            BorderPane borderpane = new BorderPane(new Label (displayText));
            buynowResultStage.setScene(new Scene (borderpane, 400, 100));
            buynowResultStage.getScene().getStylesheets().add("stylesheet/GUIStyles.css");
            buynowResultStage.setTitle("Buy Now Request Sent: " + client.getName() + " | " + item.getName());
            buynowResultStage.show();

            synchronized (historyLock) {
                historyList.add(displayText);
            }

        });


        exit.setOnAction(event -> System.exit(0));



        // Add all the child nodes to the GridPane
        // align using column indexing

        maingridpane.add(itemInfo, 0, 0, 1, 1);
        maingridpane.add(itemLabel, 0, 1, 3, 1);
        maingridpane.add(startBidPrice, 0, 2, 3, 1);
        maingridpane.add(buyNowPrice, 0, 3, 3, 1);


        maingridpane.add(softMargin, 0, 5, 4, 1);


        maingridpane.add(currItemInfo, 0, 6, 1, 1);
        maingridpane.add(timerLabel, 0, 7, 1, 1);
        maingridpane.add(bidCountdownTimerField, 1, 7, 2, 1);
        maingridpane.add(highestBidderFieldLabel, 0, 8, 1, 1);
        maingridpane.add(highestBidderField, 1, 8, 2, 1);
        maingridpane.add(currBidPriceFieldLabel, 0, 9, 1, 1);
        maingridpane.add(currBidPriceField, 1, 9, 2, 1);

        Label softMargin2 = new Label(softMargin.getText());
        maingridpane.add(softMargin2, 0, 10, 4, 1);


        maingridpane.add(custActivityLabel, 0, 11, 1, 1);
        maingridpane.add(userInputBidField, 0, 12, 2, 1);
        maingridpane.add(bid, 2, 12, 1, 1);
        maingridpane.add(buyNow, 0, 13, 2, 1);
        maingridpane.add(exit, 2, 13);


        chatTextArea = new TextArea("Help");
        chatTextArea.setPromptText("Ask me anything...");
        chatTextArea.setPrefRowCount(3);
        chatTextArea.setEditable(true);

        Button askButton = new Button("Ask");
        askButton.setOnAction(event -> askChatGPT());

//        maingridpane.add(chatTextArea, 0, 15, 2, 1);
//        maingridpane.add(askButton, 1, 16);


        // Add a button to look up sneaker information
//        Button lookupButton = new Button("Lookup Sneaker Info");
//        maingridpane.add(lookupButton, 1, 14);

        // Set the action for the lookup button
//        lookupButton.setOnAction(event -> lookupSneakerInfo());

        Label imageLabel = new Label("Images: ");

//        switch(item.getName()){
//            case "Air Jordan 1 Lows":
//                backgroundImage = new Image(getClass().getResourceAsStream("Client2/src/images/airforces.webp"));
//                break;
//            case "Nike SB Dunk Low":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/dunks.webp"));
//                break;
//            case "Adidas Samba":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/sambas.webp"));
//                break;
//            case "Jordan 1 Retro Low Travis Scott":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/travisscott.webp"));
//                break;
//            case "Nike Air Forces":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/airforces.webp"));
//                break;
//            case "Rick Owens Boots":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/rickowens.webp"));
//                break;
//            case "MSCHF Astro Boy":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/astroboy.jpg"));
//                break;
//            case "Adidas Harden Vol. 7":
//                backgroundImage = new Image(getClass().getResourceAsStream("images/hardens.webp"));
//                break;
//            default:
//                System.out.println("Unknown item");
//                break;
//        }





//        imageViews = new ImageView[3];
//
//        for (int i = 0; i < 3; i++) {
//            imageViews[i] = new ImageView(new Image());  // You might want to set an initial image here
//            imageViews[i].setFitWidth(100);  // Set the width of the images as needed
//            imageViews[i].setFitHeight(100); // Set the height of the images as needed
//        }



        // Add image

//        GridPane lilpane =new GridPane();
//
//        // Create a BackgroundImage with the Image
//        BackgroundImage background = new BackgroundImage(
//               backgroundImage,
//                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
//                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT
//        );
//
//        Background backgroundWithImage = new Background(background);
//
//        lilpane.setBackground(backgroundWithImage);
//
//
//        maingridpane.add(imageLabel, 0, 4, 1, 1);
//        maingridpane.add(lilpane, 0, 5, 4, 4);

//        maingridpane.add(imageViews[0], 1, 4, 1, 1);
//        maingridpane.add(imageViews[1], 2, 4, 1, 1);
//        maingridpane.add(imageViews[2], 3, 4, 1, 1);


        return maingridpane;
    }

    private void showItemBoughtStage() {
        Stage itemBoughtStage = new Stage();
        itemBoughtStage.setTitle("Item Bought: " + item.getName());

        // Load item image
        ImageView itemImageView = new ImageView();
        String itemName = item.getName();

        if ("Air Jordan 1 Lows".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/airjordan.webp")));
        } else if ("Nike SB Dunk Low".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/dunks.webp")));
        } else if ("Adidas Samba".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/sambas.webp")));
        } else if ("Jordan 1 Retro Low Travis Scott".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/travisscott.webp")));
        }
        else if ("Nike Air Forces".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/airforces.webp")));
        }
        else if ("Rick Owens Boots".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/rickowens.webp")));
        }
        else if ("MSCHF Astro Boy".equals(itemName)) {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/astroboy.jpg")));
        }
        else {
            itemImageView = new ImageView(new Image(getClass().getResourceAsStream("Client2/src/images/hardens.webp")));
        }


        itemImageView.setFitWidth(200);
        itemImageView.setFitHeight(200);

        // Create a VBox to hold the image
        VBox vbox = new VBox(itemImageView);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 300);
        itemBoughtStage.setScene(scene);
        itemBoughtStage.show();
    }

//    private void showConfettiAnimation() {
//        // Show confetti animation
//        confetti.setColors(Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE);
//        confetti.setAcceleration(0, 0.05);
//        confetti.setInitialRotation(180);
//        confetti.setInitialVelocity(2);
//        confetti.setEmissionRate(0.5);
//        confetti.setSize(10, 20);
//        confetti.setFadeOut(true);
//
//        Pane root = new Pane(confetti.getCanvas());
//        Scene scene = new Scene(root, 800, 600);
//
//        Stage confettiStage = new Stage();
//        confettiStage.initStyle(StageStyle.UNDECORATED);
//        confettiStage.setScene(scene);
//        confettiStage.show();
//
//        // Play confetti animation for a few seconds
//        PauseTransition delay = new PauseTransition(Duration.seconds(5));
//        delay.setOnFinished(event -> confettiStage.close());
//        delay.play();
//    }


    private void lookupSneakerInfo() {
        String itemName = item.getName();

        try {
            // Construct the URL for the Sneaks-API
            String apiUrl = "https://sneaks-api.herokuapp.com/sneaker/" + itemName;
            URL url = new URL(apiUrl);

            // Open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Get the response
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            String jsonResponseString = readJsonFromReader(reader);

            // Close the connection
            connection.disconnect();

            // Parse JSON manually
            displaySneakerInfo(jsonResponseString);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., connection error)
        }
    }

    private String readJsonFromReader(Reader reader) throws IOException {
        // Read the JSON response from the reader
        StringBuilder content = new StringBuilder();
        int charRead;
        while ((charRead = reader.read()) != -1) {
            content.append((char) charRead);
        }
        return content.toString();
    }

    private void displaySneakerInfo(String jsonResponseString) {
        // Display the information in an Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sneaker Information");
        alert.setHeaderText(null);

        // Customize the content of the alert based on the JSON response
        if (jsonResponseString.contains("error")) {
            int start = jsonResponseString.indexOf("\"error\":\"") + 9;
            int end = jsonResponseString.indexOf("\"", start);
            alert.setContentText("Error: " + jsonResponseString.substring(start, end));
        } else {
            int brandStart = jsonResponseString.indexOf("\"brand\":\"") + 9;
            int brandEnd = jsonResponseString.indexOf("\"", brandStart);
            String brand = jsonResponseString.substring(brandStart, brandEnd);

            int releaseYearStart = jsonResponseString.indexOf("\"releaseYear\":") + 14;
            int releaseYearEnd = jsonResponseString.indexOf(",", releaseYearStart);
            int releaseYear = Integer.parseInt(jsonResponseString.substring(releaseYearStart, releaseYearEnd));

            int retailPriceStart = jsonResponseString.indexOf("\"retailPrice\":") + 13;
            int retailPriceEnd = jsonResponseString.indexOf(",", retailPriceStart);
            double retailPrice = Double.parseDouble(jsonResponseString.substring(retailPriceStart, retailPriceEnd));

            int avgResalePriceStart = jsonResponseString.indexOf("\"averageResalePrice\":") + 22;
            int avgResalePriceEnd = jsonResponseString.indexOf("}", avgResalePriceStart);
            double avgResalePrice = Double.parseDouble(jsonResponseString.substring(avgResalePriceStart, avgResalePriceEnd));

            StringBuilder content = new StringBuilder();
            content.append("Brand: ").append(brand).append("\n");
            content.append("Release Year: ").append(releaseYear).append("\n");
            content.append("Retail Price: $").append(retailPrice).append("\n");
            content.append("Average Resale Price: $").append(avgResalePrice).append("\n");

            alert.setContentText(content.toString());
        }

        // Show the alert
        Optional<ButtonType> result = alert.showAndWait();
    }


    private String chatGPTAPI(String prompt) {
        String apiKey = "YOUR_OPENAI_API_KEY";
        String model = "gpt-3.5-turbo";
        String url = "https://api.openai.com/v1/chat/completions";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // Calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error getting response from ChatGPT.";
        }
    }

    /**
     * Displays the ChatGPT answer in the chatTextArea.
     *
     * @param answer The answer from ChatGPT.
     */
    private void displayChatGPTAnswer(String answer) {
        Platform.runLater(() -> chatTextArea.appendText("\n\nChatGPT: " + answer));
    }

    /**
     * Asks a question to ChatGPT using the OpenAI API.
     */
    private synchronized void askChatGPT() {
        String question = chatTextArea.getText();
        if (!question.isEmpty()) {
            Task<String> task = new Task<String>() {
                @Override
                protected String call() {
                    return chatGPTAPI(question);
                }

                @Override
                protected void succeeded() {
                    displayChatGPTAnswer(getValue());
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        chatTextArea.appendText("\n\nChatGPT: Error getting response.");
                    });
                }
            };

            new Thread(task).start();
        }
    }

    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);

    }
    
    public synchronized void updateItemDisplay() {
        // called when we receive a changed state of items from server

        highestBidderField.setText(item.getHighestBidder());
        if (item.getCurrBidPrice() >= item.getStartBid()) {
            currBidPriceField.setText("$" + priceFormat.format(item.getCurrBidPrice()));
        }
        else {
            currBidPriceField.setText("$N/A");
        }


        double bidVal = item.getCurrBidPrice() + item.getValue() / 10.0;
        if (bidVal >= item.getValue()) {
            bidVal = item.getValue() - 0.01;
        } else if (item.getCurrBidPrice() < item.getStartBid()) {
            bidVal = item.getStartBid();
        }
        userInputBidField.setText("$" + priceFormat.format(bidVal));
    }




}



