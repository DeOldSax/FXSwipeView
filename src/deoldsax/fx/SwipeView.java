package deoldsax.fx;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;


public class SwipeView extends StackPane {

	private ImageView backView; 
	private ImageView frontView; 
	private ImageView dummyView; 
	private List<Image> images; 
	private int currentDisplayedImageIndex; 
	private double frontViewPositionXBeforeSwipe; 
	private double frontViewPositionYBeforeSwipe; 
	double xD; 
	double yD; 
	/**
	 * swipe left: 	--> distance > 0
	 * swipe right: --> distance < 0
	 */
	private double mouseDistance; 
	private double mouseStartPosition; 
	private boolean fade; 
	private boolean backViewIsAlreadySet; 
	private double viewSwitchThreshold; 
	int size = 500; 
	private boolean setControlButtonsVisible; 
	private boolean buttonsAlreadyBuilt; 
	
	
	public SwipeView() {
		super();
		
		backView = new ImageView(); 
		frontView = new ImageView();
		dummyView = new ImageView(); 
		getChildren().add(dummyView); 
		dummyView.setStyle("-fx-border-color: green; -fx-border-width: 5");
		getChildren().add(backView); 
		getChildren().add(frontView); 
		images = new ArrayList<Image>(); 
		currentDisplayedImageIndex = 0; 
		frontView.setFitHeight(size);
		frontView.setFitWidth(size);
		backView.setFitHeight(size);
		backView.setFitWidth(size);
		this.setPadding(new Insets(20, 20, 20, 20));
		
		this.setStyle("-fx-border-color: pink; -fx-border-width: 5"); 
		
		if (setControlButtonsVisible) {
			buildControlButtons(); 
		}
		
		addListener(); 
	}
	
	private void buildControlButtons() {
		buttonsAlreadyBuilt = true;
		Button prevView = new Button("prev"); 
		//TODO: make a FadeTransition for the buttons
		prevView.visibleProperty().bind(this.hoverProperty());
		Button nextView = new Button("next"); 
		nextView.visibleProperty().bind(this.hoverProperty());
		
		// TODO: give the buttons a better position
		nextView.translateXProperty().bind(frontView.fitWidthProperty().divide(2).subtract(nextView.widthProperty()));
		nextView.translateYProperty().bind(frontView.fitHeightProperty().divide(2).subtract(nextView.heightProperty()));
	
		prevView.translateYProperty().bind(frontView.fitWidthProperty().divide(2).subtract(prevView.heightProperty()));
		prevView.translateXProperty().bind(frontView.fitWidthProperty().divide(2).subtract(prevView.widthProperty()).multiply(-1));
		
		getChildren().add(nextView); 
		getChildren().add(prevView); 
		
		nextView.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				//TODO: make Transition
				incrementDisplayedIndex();
				frontView.setImage(images.get(currentDisplayedImageIndex));
			};
		});
		
		prevView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//TODO: make Transition
				decrementDisplayedIndex();
				frontView.setImage(images.get(currentDisplayedImageIndex));
			}
		});
		
	}
	
	private void addListener() {

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				viewSwitchThreshold = frontView.getFitWidth() * (50.0 / 100.0); 
				frontViewPositionXBeforeSwipe = frontView.getLayoutX(); 
				frontViewPositionYBeforeSwipe = frontView.getLayoutY();
			    xD = frontView.getLayoutX() - mouseEvent.getSceneX();
			    mouseStartPosition = mouseEvent.getSceneX(); 
			}
		});
		
		this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				fade = true; 
				fadeImage(event, frontView.getLayoutY()); 
				updateMouseDistance(event); 
				if (mouseDistance > 0 && !backViewIsAlreadySet) {
					backView.setImage(images.get(getNextIndex()));
				} else {
					backView.setImage(images.get(getPrevIndex()));
				}
			}
		});
		
		this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				frontView.setCursor(Cursor.HAND);
				updateMouseDistance(mouseEvent);
				backViewIsAlreadySet = false; 
				if (fade) {
					changeViewIfNeeded();
					fade = false; 
				}
			}
		});
		
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			  @Override public void handle(MouseEvent mouseEvent) {
				  frontView.setCursor(Cursor.HAND);
				  if (setControlButtonsVisible) {
					if (!buttonsAlreadyBuilt) {
						buildControlButtons();
					}
				  }
			  }
		});
		
	}
	
	private void changeViewIfNeeded() {
		if (mouseDistance > viewSwitchThreshold) {
			frontView.setImage(backView.getImage());
			backView.setImage(images.get(getNextIndex()));
			incrementDisplayedIndex(); 
		} else if (mouseDistance < 0 && mouseDistance < -viewSwitchThreshold) {
			frontView.setImage(backView.getImage());
			backView.setImage(images.get(getNextIndex()));
			decrementDisplayedIndex();
		}
		frontView.relocate(frontViewPositionXBeforeSwipe, frontViewPositionYBeforeSwipe);
	}
	
	private void fadeImage(MouseEvent event, double y) {
		frontView.setLayoutX(event.getSceneX() + xD);
		changeOpacity(); 
	}
	
	private void changeOpacity() {
		double percentage = Math.abs(mouseDistance / frontView.getFitWidth()); 
		backView.setOpacity(percentage);
	}
	
	private void updateMouseDistance(MouseEvent event) {
		mouseDistance = mouseStartPosition - event.getSceneX(); 
	}
	
	private void incrementDisplayedIndex() {
		if (currentDisplayedImageIndex == images.size() - 1) {
			currentDisplayedImageIndex = 0; 
		} else {
			currentDisplayedImageIndex++; 
		}
	}
	
	private void decrementDisplayedIndex() {
		if (currentDisplayedImageIndex == 0) {
			currentDisplayedImageIndex = images.size() - 1; 
		} else {
			currentDisplayedImageIndex--; 
		}
	}
	
	private int getNextIndex() {
		if (currentDisplayedImageIndex == images.size() - 1) {
			return 0;  
		} 
		return currentDisplayedImageIndex + 1; 
	}
	
	private int getPrevIndex() {
		if (currentDisplayedImageIndex == 0) {
			return images.size()-1;  
		} 
		return currentDisplayedImageIndex - 1; 
	}
	
	public void addImage(Image image) {
		images.add(image); 
		frontView.setImage(images.get(0));
	}
	
	public void setViewSwitchThreshold(double viewSwitchThreshold) {
		this.viewSwitchThreshold = viewSwitchThreshold;
	}
	
}

