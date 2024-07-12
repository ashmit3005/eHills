
/*
 * EE422C Final Project submission by
 * <Ashmit Bhatnagar>
 * <ab77538>
 * <17610>
 * Fall 2023
 */

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GUI extends Application {

    // Client instance to manage communication with the server
    Client client = null;

    // Input/Output streams for communication with the server
    ObjectOutputStream writer = null;
    ObjectInputStream reader = null;
    Socket socket = null;
    static String IP = "localhost";

    // JavaFX scene for displaying items
    Scene scene;
    ArrayList<ItemPage> itemPages = new ArrayList<>();

    // Decimal formatter for currency display
    DecimalFormat currencyFormat = new DecimalFormat("0.00");

    private static final int TEXT_UPDATE_DELAY = 50; // milliseconds


    @Override
    public void start(Stage primaryStage) {

        // Establish networking connection with the server
        int port = 5000;
        try {
            // Connect to the server via Socket
            socket = new Socket(IP, port);

            // Initialize Object Input/Output streams for communication
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());

            // Testing connection establishment
            System.out.println("Networking established");

            // Create a new client and pass necessary information to it
            client = new Client("", socket, writer, reader, this);

        } catch (IOException e) {
            // Handle networking failure
            System.out.println("Networking failed. Server may be down. Please restart");
            System.exit(0);
        }

        // Create a ProgressBar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        // Create a Label for displaying loading messages
        Label loadingLabel = new Label("Loading...");

        // Create a StackPane to center the components
        StackPane root = new StackPane();
        root.getChildren().addAll(progressBar, loadingLabel);

        // Create a Scene
        Scene scene = new Scene(root, 400, 200);

        // Set the Scene to the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Loading Screen");

        // Show the Stage
        primaryStage.show();

        // Simulate a task that takes time to complete (e.g., loading resources)
        simulateLoadingTask(progressBar, loadingLabel, primaryStage);


    }


    // Switch the scene to display items after successful login
    private void startClient(Stage primaryStage) {
        primaryStage.setScene(scene);
        primaryStage.setTitle("eHILLS: " + client.getName());
    }

    // Update the display of items
    public void updateItems() {
        for (ItemPage ip : itemPages) {
            ip.updateItemDisplay();
        }
    }

    private void simulateLoadingTask(ProgressBar progressBar, Label loadingLabel, Stage primaryStage) {
        // Simulate a task that takes time to complete
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    // Update the progress and loading message
                    double progress = progressBar.getProgress() + 0.2;
                    progressBar.setProgress(progress);
                    loadingLabel.setText("Loading... " + (int) (progress * 100) + "%");
                }),
                new KeyFrame(Duration.seconds(1))
        );

        // Set the cycle count to a specific number (e.g., 5)
        int cycleCount = 5;
        timeline.setCycleCount(cycleCount);

        // In a real application, you would perform your loading task here

        // Simulate a task completion after the animation completes
        timeline.setOnFinished(event -> {
            // Launch the main application GUI after loading is complete
            launchMainGUI(primaryStage);
        });

        // Start the timeline
        timeline.play();
    }

    private void launchMainGUI(Stage primaryStage) {

       // primaryStage.close();
        String musicFile = "Client2/src/netflix.mp3"; // Replace with your music file path
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);

        // Play the audio once on startup
        mediaPlayer.play();

        // ITEMS VIEW: Load up items GUIs
        TabPane itemsPane = new TabPane();
        scene = new Scene(itemsPane, 700, 1020);
        scene.getStylesheets().add("stylesheet/GUIStyles.css");
        itemTabsInit(itemsPane, primaryStage);

        // LOGIN VIEW: Create GUI specifically for logging in with username/password or as a guest

        GridPane loginPane = new GridPane();
        loginPane.setHgap(20);
        loginPane.setVgap(20);


        // Create a scene and place it in the stage
        Scene loginScene = new Scene(loginPane, 340, 200);
        loginScene.getStylesheets().add("stylesheet/GUIStyles.css");
        primaryStage.setTitle("Login to eHills"); // Set the stage title
        primaryStage.setScene(loginScene); // Place the scene in the stage
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/landscape-green-hill-circle-logo-symbol-icon-graphic-design-illustration-vector.jpg")));
        primaryStage.show(); // Display the stage

        // Username elements
        Label usernameLabel = new Label("Username: ");
        usernameLabel.setFont(new Font("Cambria", 20));
        TextField usernameTextField = new TextField();
        usernameTextField.setEditable(true);
        usernameTextField.setPrefWidth(150);

        // Password elements
        Label passwordLabel = new Label("Password: ");
        passwordLabel.setFont(new Font("Cambria", 20));
        PasswordField passwordTextField = new PasswordField();
        passwordTextField.setPrefWidth(150);

        // Button to log in
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(120);

        // Button to log in as a guest
        Button guestButton = new Button("Enter as Guest");
        loginButton.setMaxWidth(300);

        // Button to quit
        Button loginExitButton = new Button("Exit");
        loginButton.setMaxWidth(120);

        // Button to clear text in username/password fields
        Button loginClearButton = new Button("Clear");
        loginButton.setMaxWidth(120);

        // TextArea to display login status or messages
        TextArea loginTextArea = new TextArea("Welcome to eHills!");
        loginTextArea.setEditable(false);
        loginTextArea.setPrefWidth(280);
        loginTextArea.setPrefHeight(30);

        // Add all elements to the login GridPane
        loginPane.add(usernameLabel, 1, 1);
        loginPane.add(usernameTextField, 2, 1);
        loginPane.add(passwordLabel, 1, 2);
        loginPane.add(passwordTextField, 2, 2);
        loginPane.add(loginButton, 1, 3);
        loginPane.add(guestButton, 2, 3);
        loginPane.add(loginExitButton, 1, 4);
        loginPane.add(loginClearButton, 2, 4);
        loginPane.add(loginTextArea, 1, 5, 2, 1);



        // LOGIN BUTTON ACTION
        loginButton.setOnAction(event -> {
            // Send login attempt to the server
            client.flushLoginAttempt(new SecureObject(usernameTextField.getText(), passwordTextField.getText()).encode());
            loginButton.setDisable(true);
            loginClearButton.setDisable(true);
            loginExitButton.setDisable(true);
            guestButton.setDisable(true);

            // Check if login is successful
            if (checkLogin()) {
                loginTextArea.setText("Login Successful!");
                client.setName(usernameTextField.getText());
                startClient(primaryStage);
                itemTabsInit(itemsPane, primaryStage);
            } else {
                loginTextArea.setText("Incorrect username or password: Please try again");
                String musicFile2 = "Client2/src/trash.mp3"; // Replace with your music file path
                Media sound2 = new Media(new File(musicFile2).toURI().toString());
                MediaPlayer mediaPlayer2 = new MediaPlayer(sound2);

                // Play the audio once on startup
                mediaPlayer2.play();
            }

            // Enable buttons after login attempt is processed
            loginButton.setDisable(false);
            loginClearButton.setDisable(false);
            loginExitButton.setDisable(false);
            guestButton.setDisable(false);
        });

        // CONTINUE AS GUEST BUTTON ACTION
        guestButton.setOnAction(event -> {
            // Send guest login attempt to the server
            client.flushLoginAttempt(new SecureObject("Guest", ""));

            // Check if guest login is successful
            if (checkLogin()) {
                loginTextArea.setText("Login Successful!");
                if (usernameTextField.getText().equals("")) {
                    client.setName("Guest");
                } else {
                    client.setName(usernameTextField.getText());
                }
                startClient(primaryStage);
                itemTabsInit(itemsPane, primaryStage);
            } else {
                loginTextArea.setText("Incorrect username/password: Please try again");
            }
        });

        // CLEAR BUTTON ACTION
        loginClearButton.setOnAction(event -> {
            // Clear username and password fields
            usernameTextField.clear();
            passwordTextField.clear();
            loginTextArea.setText("Welcome to eHills!");
        });

        // EXIT BUTTON ACTION
        loginExitButton.setOnAction(event -> System.exit(0));
    }


    // Check the status of the login attempt
    private boolean checkLogin() {
        Boolean loginStatus;
        try {
            // Sleep for a short duration to allow the server to process the login attempt
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Retrieve and reset the login status from the client
        loginStatus = client.getLoginStatus();
        if (loginStatus == null) {
            System.out.println("Login not received from server");
            return false;
        } else {
            client.resetLogin();
            System.out.println("Sending login status: " + loginStatus);
            return loginStatus;
        }
    }


    // Initialize the tabs for displaying items
    private void itemTabsInit(TabPane tabPane, Stage primaryStage) {
        tabPane.getTabs().clear();

        // Initialize a welcome page for the user
        Tab landing = new Tab("Welcome!");
        BorderPane landingPane = new BorderPane();
        TextArea landingText = new TextArea();
        landingText.setEditable(false);
        landingText.setVisible(true);
        landingText.setPrefWidth(200);
        landingPane.setTop(landingText);
        landing.setContent(landingPane);
        tabPane.getTabs().add(landing);

        // Updated text message
        String welcomeMessage = "Hi! Welcome to eHILLS!\n" +
                "Now that you're logged in, you can bid on items until you cop 'em. \n" +
                "Each item can be bagged instantly, if you buy now. \n" +
                "A buy now price has been marked to indicate the price you can stop bidding. \n" +
                "Don't be spooked by the timer! When the timer hits 0, the item is goneski!! \n" +
                "A bid in the last 15 seconds will reset the timer back to 15 seconds. \n" +
                "Happy bidding and good luck! ;)";

        // Create a timeline for text animation
        Timeline timeline = new Timeline();

        // Add keyframes to update text gradually
        for (int i = 0; i <= welcomeMessage.length(); i++) {
            int finalI = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * TEXT_UPDATE_DELAY), event -> {
                String partialMessage = welcomeMessage.substring(0, finalI);
                landingText.setText(partialMessage);
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        // Show the updated text when the animation is finished
        KeyFrame finalKeyFrame = new KeyFrame(Duration.millis(welcomeMessage.length() * TEXT_UPDATE_DELAY), event -> {
            landingText.setText(welcomeMessage);
        });
        timeline.getKeyFrames().add(finalKeyFrame);

//        timeline.setOnFinished(event -> {
//            // Launch the main application GUI after loading is complete
//            launchMainGUI2(primaryStage);
//        });

        // Play the animation
        timeline.play();


        // loop through ArrayList of items to initialize a tab for each
        for (Item i : client.getItems()) {
            System.out.println(i);
            i.startBidTimer();
            ItemPage iPage = new ItemPage(i, this.client);
            GridPane iPane = iPage.initDisplay();
            Tab currentTab = new Tab(i.getName());
            currentTab.setContent(iPane);
            currentTab.setClosable(false);
            itemPages.add(iPage);
            tabPane.getTabs().add(currentTab);
        }

    }

    // Main method to launch the application
    public static void main(String[] args) {
        // Set the server IP address if provided as a command line argument
        if (args.length != 0) {
            GUI.IP = args[0];
        }
        launch(args);
    }
}
