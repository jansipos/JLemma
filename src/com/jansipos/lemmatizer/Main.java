package com.jansipos.lemmatizer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class Main extends Application {

	private static String initialDir = "res/text/test";
	private static Button button;
	private static File chosenFolder;

	Lemmatizer l = new Lemmatizer();

	private static final int WIDTH = 500;
	private static final int HEIGHT = 100;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {

		button = new Button("Browse");
		button.setOnAction(e -> {

			FileChooser fc = new FileChooser();
			fc.setTitle("Choose files");
			fc.setInitialDirectory(new File(initialDir));

			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt", "*.txt");
			fc.getExtensionFilters().add(extFilter);

			List<File> chosenFiles = fc.showOpenMultipleDialog(primaryStage);

			if (chosenFiles != null && !chosenFiles.isEmpty()) {

				chosenFolder = chosenFiles.get(0).getParentFile();

				chosenFiles.forEach(f -> l.lemmatize(f));

				displayDialogAndFiles();
			}
		});

		StackPane layout = new StackPane();
		layout.getChildren().add(button);

		Scene scene = new Scene(layout, WIDTH, HEIGHT);

		primaryStage.setTitle("Lemmatizer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void displayDialogAndFiles() {

		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle("Result");
		alert.setHeaderText("Lemmatization complete");
		alert.setContentText("Done!");

		alert.showAndWait();

		// otvara fajlove s rezultatima u sistemskom file browseru
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(new File(chosenFolder.toString() + "/out"));
			}
		} catch (IOException e) {
			System.out.println("Unable to open file browser: " + e.getMessage());
		}
	}
}

