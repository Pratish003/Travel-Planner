package travelp;

import java.sql.Connection;
import java.sql.PreparedStatement;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import javafx.scene.control.DateCell;
import javafx.geometry.HPos;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class TripTester extends Application {
    static int startCityIndex;
    static int endCityIndex;

    static String[] cities;
    static int[][] connections;

    private ComboBox<String> cityComboBoxStart;
    private ComboBox<String> cityComboBoxEnd;
    private Button planTripButton;
    private Button oneWayTripButton;
    private TextArea resultArea;
    private Button bookNowButton;
    private Button clearButton;
    private double totalFare;
    private double forwardFare;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            loadCitiesFromFile("Resources/cities.txt");
            loadConnectionsFromFile("Resources/connections.txt");

            cityComboBoxStart = new ComboBox<>();
            cityComboBoxEnd = new ComboBox<>();
            oneWayTripButton = new Button("Plan One Way Trip");
            planTripButton = new Button("Plan Round Trip");
            resultArea = new TextArea();
            bookNowButton = new Button("Book Now");
            clearButton = new Button("Clear");

            // Initialize UI components
            initializeUIComponents();

            // Set up event handlers
            setupEventHandlers();

            // Create layout
            VBox layout = createMainLayout();

            Scene scene = new Scene(layout, 480, 460);
            primaryStage.setTitle("Trip Planner");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            showError("Error reading the files: " + e.getMessage());
        }
    }

    private void initializeUIComponents() {
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setFont(Font.font("Consolas", 13));
        resultArea.setStyle("-fx-control-inner-background: #f0f8ff;");
        resultArea.setPrefHeight(270);
        resultArea.setPrefWidth(400);

        cityComboBoxStart.getItems().addAll(cities);
        cityComboBoxEnd.getItems().addAll(cities);

        // Set button styles
        planTripButton.setFont(Font.font("Arial", 14));
        planTripButton.setStyle("-fx-background-color: #4682b4; -fx-text-fill: white; -fx-cursor: hand;");

        oneWayTripButton.setFont(Font.font("Arial", 14));
        oneWayTripButton.setStyle("-fx-background-color: #32a852; -fx-text-fill: white; -fx-cursor: hand;");

        bookNowButton.setFont(Font.font("Arial", 14));
        bookNowButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-cursor: hand;");
        bookNowButton.setDisable(true);

        clearButton.setFont(Font.font("Arial", 14));
        clearButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-cursor: hand;");
    }

    private void setupEventHandlers() {
        oneWayTripButton.setOnAction(e -> planOneWayTrip());
        planTripButton.setOnAction(e -> planTrip());

        // Hover effects for trip buttons
        oneWayTripButton.setOnMouseEntered(e -> {
            oneWayTripButton.setStyle("-fx-background-color: #3ccf77; -fx-text-fill: white;");
            oneWayTripButton.setScaleX(1.1);
            oneWayTripButton.setScaleY(1.1);
        });
        oneWayTripButton.setOnMouseExited(e -> {
            oneWayTripButton.setStyle("-fx-background-color: #32a852; -fx-text-fill: white;");
            oneWayTripButton.setScaleX(1.0);
            oneWayTripButton.setScaleY(1.0);
        });

        planTripButton.setOnMouseEntered(e -> {
            planTripButton.setStyle("-fx-background-color: #5a9bd5; -fx-text-fill: white;");
            planTripButton.setScaleX(1.1);
            planTripButton.setScaleY(1.1);
        });
        planTripButton.setOnMouseExited(e -> {
            planTripButton.setStyle("-fx-background-color: #4682b4; -fx-text-fill: white;");
            planTripButton.setScaleX(1.0);
            planTripButton.setScaleY(1.0);
        });

        cityComboBoxStart.setOnMouseEntered(e -> {
            cityComboBoxStart.setScaleX(1.05);
            cityComboBoxStart.setScaleY(1.05);
        });
        cityComboBoxStart.setOnMouseExited(e -> {
            cityComboBoxStart.setScaleX(1.0);
            cityComboBoxStart.setScaleY(1.0);
        });

        cityComboBoxEnd.setOnMouseEntered(e -> {
            cityComboBoxEnd.setScaleX(1.05);
            cityComboBoxEnd.setScaleY(1.05);
        });
        cityComboBoxEnd.setOnMouseExited(e -> {
            cityComboBoxEnd.setScaleX(1.0);
            cityComboBoxEnd.setScaleY(1.0);
        });

        bookNowButton.setOnMouseEntered(e -> bookNowButton.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white;"));
        bookNowButton.setOnMouseExited(e -> bookNowButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white;"));
        clearButton.setOnMouseEntered(e -> clearButton.setStyle("-fx-background-color: #c9302c; -fx-text-fill: white;"));
        clearButton.setOnMouseExited(e -> clearButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;"));

        bookNowButton.setDisable(false);
        bookNowButton.setOnAction(e -> showBookingForm(totalFare, forwardFare));

        clearButton.setOnAction(e -> clearSelections());
    }

    private VBox createMainLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        Label startLabel = new Label("Select your starting city:");
        Label endLabel = new Label("Select your destination city:");
        startLabel.setFont(Font.font("Arial", 14));
        endLabel.setFont(Font.font("Arial", 14));

        HBox buttonBox = new HBox(10, oneWayTripButton, planTripButton);
        HBox actionButtons = new HBox(10, bookNowButton, clearButton);

        layout.getChildren().addAll(startLabel, cityComboBoxStart, endLabel, cityComboBoxEnd, buttonBox, resultArea, actionButtons);

        return layout;
    }

    private void showBookingForm(double totalRoundTripFare, double estimatedOneWayFare) {
        Stage formStage = new Stage();
        formStage.setTitle("Enter Booking Details");

        // Labels and Inputs
        Label titleLabel = new Label("Trip Booking Form");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();

        Label aadharLabel = new Label("Aadhar No:");
        TextField aadharField = new TextField();

        Label genderLabel = new Label("Gender:");
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");

        Label ageLabel = new Label("Age:");
        ComboBox<Integer> ageBox = new ComboBox<>();
        for (int i = 18; i <= 60; i++) {
            ageBox.getItems().add(i);
        }

        Label dateLabel = new Label("Travel Date:");
        DatePicker travelDatePicker = new DatePicker();
        travelDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // Add Departure and Destination City fields
        Label departureLabel = new Label("Departure City:");
        TextField departureField = new TextField(cities[startCityIndex]);
        departureField.setEditable(false);
        departureField.setStyle("-fx-background-color: #f0f0f0;");

        Label destinationLabel = new Label("Destination City:");
        TextField destinationField = new TextField(cities[endCityIndex]);
        destinationField.setEditable(false);
        destinationField.setStyle("-fx-background-color: #f0f0f0;");

        // Fare fields (read-only)
        Label roundTripFareLabel = new Label("Total Roundtrip Fare (₹):");
        TextField roundTripFareField = new TextField(String.format("%.2f", totalRoundTripFare));
        roundTripFareField.setEditable(false);
        roundTripFareField.setFocusTraversable(false);

        Label oneWayFareLabel = new Label("Estimated One Way Fare (₹):");
        TextField oneWayFareField = new TextField(String.format("%.2f", estimatedOneWayFare));
        oneWayFareField.setEditable(false);
        oneWayFareField.setFocusTraversable(false);

        // QR Code Section
        Label qrLabel = new Label("Scan QR to Pay:");
        ImageView qrImageView = new ImageView();
        try {
            Image qrImage = new Image(new FileInputStream("Resources/image/pay.jpg"));
            qrImageView.setImage(qrImage);
            qrImageView.setFitWidth(150);
            qrImageView.setPreserveRatio(true);
            qrImageView.setSmooth(true);
            qrImageView.setStyle("-fx-border-color: black; -fx-border-width: 1;");
        } catch (FileNotFoundException ex) {
            showAlert(Alert.AlertType.ERROR, "QR code image not found: " + ex.getMessage());
        }

        // Upload Slip
        Label slipLabel = new Label("Upload Payment Slip:");
        Button uploadButton = new Button("Choose File");
        uploadButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-font-weight: bold;");
        uploadButton.setOnMouseEntered(e -> uploadButton.setStyle("-fx-background-color: #3cb371; -fx-text-fill: white;"));
        uploadButton.setOnMouseExited(e -> uploadButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white;"));
        Label fileNameLabel = new Label();
        fileNameLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        fileNameLabel.setTextFill(Color.GRAY);

        FileChooser fileChooser = new FileChooser();
        final File[] selectedFile = new File[1];
        uploadButton.setOnAction(e -> {
            selectedFile[0] = fileChooser.showOpenDialog(formStage);
            if (selectedFile[0] != null) {
                fileNameLabel.setText(selectedFile[0].getName());
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #4169e1; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: #5a87f5; -fx-text-fill: white;"));
        submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-background-color: #4169e1; -fx-text-fill: white;"));

        // Layout
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new Insets(30));
        formLayout.setHgap(15);
        formLayout.setVgap(10);
        formLayout.setStyle("-fx-background-color: #f4faff; -fx-border-color: #dcdcdc; -fx-border-width: 1px;");

        formLayout.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        formLayout.add(nameLabel, 0, 1);
        formLayout.add(nameField, 1, 1);
        formLayout.add(emailLabel, 0, 2);
        formLayout.add(emailField, 1, 2);
        formLayout.add(phoneLabel, 0, 3);
        formLayout.add(phoneField, 1, 3);
        formLayout.add(aadharLabel, 0, 4);
        formLayout.add(aadharField, 1, 4);
        formLayout.add(genderLabel, 0, 5);
        formLayout.add(genderBox, 1, 5);
        formLayout.add(ageLabel, 0, 6);
        formLayout.add(ageBox, 1, 6);
        formLayout.add(dateLabel, 0, 7);
        formLayout.add(travelDatePicker, 1, 7);
        
        // Add city fields
        formLayout.add(departureLabel, 0, 8);
        formLayout.add(departureField, 1, 8);
        formLayout.add(destinationLabel, 0, 9);
        formLayout.add(destinationField, 1, 9);

        // Fare fields
        formLayout.add(roundTripFareLabel, 0, 10);
        formLayout.add(roundTripFareField, 1, 10);
        formLayout.add(oneWayFareLabel, 0, 11);
        formLayout.add(oneWayFareField, 1, 11);

        // QR and payment section
        formLayout.add(qrLabel, 0, 12);
        formLayout.add(qrImageView, 1, 12);
        formLayout.add(slipLabel, 0, 13);
        formLayout.add(uploadButton, 1, 13);
        formLayout.add(fileNameLabel, 1, 14);

        // Submit button
        formLayout.add(submitButton, 1, 14);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setMargin(submitButton, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(formLayout, 500, 800); // Slightly increased height
        formStage.setScene(scene);
        formStage.show();

        // Submit Action
        submitButton.setOnAction(ev -> {
            try {
                // Validate all fields
                if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                    phoneField.getText().isEmpty() || aadharField.getText().isEmpty() ||
                    genderBox.getValue() == null || ageBox.getValue() == null ||
                    travelDatePicker.getValue() == null || fileNameLabel.getText().isEmpty()) {
                    
                    showAlert(Alert.AlertType.ERROR, "Please fill all fields and upload payment slip");
                    return;
                }

                // Collect data
                String fullName = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String aadharNo = aadharField.getText().trim();
                String gender = genderBox.getValue();
                int age = ageBox.getValue();
                LocalDate travelDate = travelDatePicker.getValue();
                String departureCity = departureField.getText(); // assuming you have this field
                String destinationCity = destinationField.getText(); // assuming you have this field
                String slipFileName = fileNameLabel.getText();

                // Insert into MySQL
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    String sql = "INSERT INTO bookings (name, email, phone, aadhar, gender, age, travel_date, " +
                                 "departure_city, destination_city, round_trip_fare, one_way_fare, slip_file_name) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, fullName);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    stmt.setString(4, aadharNo);
                    stmt.setString(5, gender);
                    stmt.setInt(6, age);
                    stmt.setDate(7, java.sql.Date.valueOf(travelDate));
                    stmt.setString(8, departureCity);
                    stmt.setString(9, destinationCity);
                    stmt.setDouble(10, totalRoundTripFare);
                    stmt.setDouble(11, estimatedOneWayFare);
                    stmt.setString(12, slipFileName);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        formStage.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Booking Confirmation");
                        alert.setHeaderText(null);
                        alert.setContentText("🎉 Booking successful!\n\n" +
                            "Name: " + fullName + "\n" +
                            "From: " + departureCity + " to " + destinationCity + "\n" +
                            "Date: " + travelDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                        alert.showAndWait();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to save booking. Please try again.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database connection failed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
            }
        });
    }
    private void clearSelections() {
        cityComboBoxStart.getSelectionModel().clearSelection();
        cityComboBoxEnd.getSelectionModel().clearSelection();
        resultArea.clear();
        resultArea.setStyle("-fx-control-inner-background: #f0f8ff;");
        bookNowButton.setDisable(true);
    }

    void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static void loadCitiesFromFile(String filename) throws IOException {
        List<String> cityList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                cityList.add(line.trim());
            }
        }
        cities = cityList.toArray(new String[0]);
    }

    static void loadConnectionsFromFile(String filename) throws IOException {
        List<int[]> connectionsList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    int from = Integer.parseInt(parts[0].trim());
                    int to = Integer.parseInt(parts[1].trim());
                    int distance = Integer.parseInt(parts[2].trim());
                    connectionsList.add(new int[]{from, to, distance});
                }
            }
        }
        connections = connectionsList.toArray(new int[0][0]);
    }

    private void planTrip() {
        try {
            startCityIndex = cityComboBoxStart.getSelectionModel().getSelectedIndex();
            endCityIndex = cityComboBoxEnd.getSelectionModel().getSelectedIndex();

            if (startCityIndex == -1 || endCityIndex == -1 || startCityIndex == endCityIndex) {
                showError("Please select two different cities.");
                bookNowButton.setDisable(true);
                return;
            }

            RoundTripPlanner roundTripPlanner = new RoundTripPlanner(cities, connections, startCityIndex, endCityIndex);

            // Update instance variables with calculated fares
            this.forwardFare = roundTripPlanner.getForwardTripFare();
            this.totalFare = roundTripPlanner.getTotalRoundTripFare();

            resultArea.clear();
            resultArea.appendText(roundTripPlanner.getRoundTripDetails());
            bookNowButton.setDisable(false);

            // Debug: Print fares to console
            System.out.println("[DEBUG] Forward Fare: " + forwardFare);
            System.out.println("[DEBUG] Total Fare: " + totalFare);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), resultArea);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            verifyRoundTrip(roundTripPlanner);

        } catch (Exception e) {
            showError("Error planning the round trip: " + e.getMessage());
            bookNowButton.setDisable(true);
        }
    }

    private void planOneWayTrip() {
        try {
            startCityIndex = cityComboBoxStart.getSelectionModel().getSelectedIndex();
            endCityIndex = cityComboBoxEnd.getSelectionModel().getSelectedIndex();

            if (startCityIndex == -1 || endCityIndex == -1 || startCityIndex == endCityIndex) {
                showError("Please select two different cities.");
                bookNowButton.setDisable(true);
                return;
            }

            RoundTripPlanner planner = new RoundTripPlanner(cities, connections, startCityIndex, endCityIndex);
            
            // Update instance variables for one-way trip
            this.forwardFare = planner.getForwardTripFare();
            this.totalFare = forwardFare; // For one-way, total = forward fare

            resultArea.clear();
            resultArea.appendText("📍 One Way Trip Details:\n\n");
            resultArea.appendText(planner.getForwardTripDetails());
            resultArea.appendText("\n✅ Roundtrip planning successful!");
            resultArea.appendText("\n\n🛈 Be a little more adventurous! Travel further!");

            bookNowButton.setDisable(false);

            // Debug: Print fares to console
            System.out.println("[DEBUG] One-Way Fare: " + forwardFare);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), resultArea);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (Exception e) {
            showError("Error planning the one way trip: " + e.getMessage());
            bookNowButton.setDisable(true);
        }
    }
    private void verifyRoundTrip(RoundTripPlanner roundTripPlanner) {
        List<String> forwardRouteCopy = new ArrayList<>(roundTripPlanner.getForwardRoute());
        List<String> returnRouteCopy = new ArrayList<>(roundTripPlanner.getReturnRoute());

        forwardRouteCopy.retainAll(roundTripPlanner.getReturnRoute());
        returnRouteCopy.removeAll(forwardRouteCopy);

        resultArea.appendText("\n✅ Roundtrip planning successful!");
        resultArea.appendText("\n\n🛈 Be a little more adventurous! Travel further!");
    }

    private void showError(String message) {
        resultArea.setStyle("-fx-control-inner-background: #ffe6e6;");
        resultArea.setText("⚠️ " + message);
    }
}