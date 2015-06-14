package com.jansipos.lemmatizer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class Main extends Application {

	private static String initialDir = "res/text/test";

	private static Stage window;
	private static VBox layout;
	private static Button button;
	
	private static ProgressBar progressBar; 
	
	private static File chosenFolder;
	private static List<File> chosenFiles;

	Lemmatizer l = new Lemmatizer();

	private static final int WIDTH = 500;
	private static final int HEIGHT = 100;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {

		window = primaryStage;
		
		LemmaService service = new LemmaService();
		service.setOnSucceeded(e -> {
			displayDialogAndFiles();
			service.reset();
			progressBar.setVisible(false);
		});
		
		button = new Button("Browse");
		button.setOnAction(e -> {

			FileChooser fc = new FileChooser();
			fc.setTitle("Choose files");
			fc.setInitialDirectory(new File(initialDir));

			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt", "*.txt");
			fc.getExtensionFilters().add(extFilter);

			chosenFiles = fc.showOpenMultipleDialog(window);
			
			service.start();
		});

		layout = new VBox();
		layout.setSpacing(10);
		layout.setAlignment(Pos.CENTER);
		
		progressBar = new ProgressBar(0);
		progressBar.setMinWidth(WIDTH - 100);
		progressBar.setVisible(false);
		progressBar.progressProperty().bind(service.progressProperty());
		
		layout.getChildren().addAll(button, progressBar);

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
	
	private class LemmaService extends Service<Void> {

		@Override
		protected Task<Void> createTask() {
			
			return new Task<Void>() {
				
				protected Void call() throws Exception {
					
					if (chosenFiles != null && !chosenFiles.isEmpty()) {
						
						chosenFolder = chosenFiles.get(0).getParentFile();
						progressBar.setVisible(true);

						for (int i = 0, len = chosenFiles.size(); i < len; i++) {
							l.lemmatize(chosenFiles.get(i));
							updateProgress(i, len);
						}

					}
					return null;
				}
			};
		}
	}
}

