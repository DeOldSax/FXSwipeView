package deoldsax.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
	
	public static void main(String[] args) {
		launch(args); 
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		SwipeView view = new SwipeView(); 
		BorderPane pane = new BorderPane(); 
		
		Image one = new Image(App.class.getResourceAsStream("1.jpg")); 
		Image two = new Image(App.class.getResourceAsStream("2.jpg")); 
		Image three = new Image(App.class.getResourceAsStream("3.jpg")); 
		Image four = new Image(App.class.getResourceAsStream("4.jpg")); 
		view.addImage(one);
		view.addImage(two);
		view.addImage(three);
		view.addImage(four);
		
		pane.setCenter(view);
		Scene scene = new Scene(pane); 
		stage.setScene(scene);
		stage.show();
	}
}
