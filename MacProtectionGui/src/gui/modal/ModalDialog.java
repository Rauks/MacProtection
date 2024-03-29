/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.modal;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Used to build a JavaFX Modal Stage with messages and buttons. Each button action can be handeled.
 * Use {@link Stage#showAndWait} to have an Alert behavior.
 * 
 * @author Karl
 */
public class ModalDialog extends Stage{
    private final static double MARGIN = 15;
    private final static int BUTTON_PREF_WIDTH = 100;
    private final static int BUTTON_SPACING = 20;
    private final static int MESSAGE_MIN_WIDTH = 200;
    private final static int MESSAGE_MAX_WIDTH = 800;
    
    /**
     * Decribe the Modal stage types (change the icon displayed).
     */
    public enum ModalType{
        ERROR("ModalError.png"),
        INFO("ModalInfo.png"),
        QUERY("ModalQuery.png"),
        VALID("ModalValid.png"),
        WARNING("ModalWarning.png");
        
        private Image img;
        
        private ModalType(String img){
            this.img = new Image(ModalDialog.class.getResourceAsStream("res/" + img));
        }
        
        private Image getImage(){
            return this.img;
        }
    }
    
    /**
     * Decribe the button models addable to this modal stage.
     */
    public enum ModalButton{
        OK("Ok"),
        CANCEL("Annuler"),
        YES("Oui"),
        NO("Non");
        
        private String buttonText;
        
        private ModalButton(String buttonText){
            this.buttonText = buttonText;
        }
        
        private Button getButton(){
            Button b = new Button(this.buttonText);
            b.setPrefWidth(BUTTON_PREF_WIDTH);
            return b;
        }
    }
    
    private Scene scene;
    private BorderPane mainPane;
    private VBox messagesBox;
    private HBox buttonsBox;
    private ImageView icon;
    
    /**
     * Create a new empty Modal stage.
     * 
     * @see ModalType
     * 
     * @param type The Modal stage type.
     */
    public ModalDialog(ModalType type){
        this(type, null);
    }
    
    /**
     * Create a new empty Modal stage. The {@code closeHandler} is called when there is an external request to close this Window.
     * 
     * @param type The Modal stage type.
     * @param closeHandler Called when there is an external request to close this Window.
     */
    public ModalDialog(ModalType type, final EventHandler<WindowEvent> closeHandler){
        this.mainPane = new BorderPane();
        
        this.messagesBox = new VBox();
        this.messagesBox.setAlignment(Pos.TOP_LEFT);
        this.mainPane.setCenter(this.messagesBox);
        BorderPane.setMargin(this.messagesBox, new Insets(MARGIN, MARGIN, MARGIN, 2*MARGIN));
        
        this.buttonsBox = new HBox();
        this.buttonsBox.setAlignment(Pos.CENTER);
        this.buttonsBox.setSpacing(BUTTON_SPACING);
        this.mainPane.setBottom(this.buttonsBox);
        BorderPane.setMargin(this.buttonsBox, new Insets(0, 0, MARGIN, 0));
        
        this.icon = new ImageView();
        this.icon.setImage(type.getImage());
        this.mainPane.setLeft(this.icon);
        BorderPane.setMargin(this.icon, new Insets(MARGIN));
        
        this.setResizable(false);
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setIconified(false);
        this.centerOnScreen();
        
        this.scene = new Scene(this.mainPane);
        this.setScene(this.scene);
        
        this.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent t) {
                close();
                if(closeHandler != null){
                    closeHandler.handle(t);
                }
            }
        });
    }
    
    /**
     * Add a button to this Modal stage.
     * 
     * @param button The button to add.
     */
    public void addButton(ModalButton button){
        this.addButton(button, null);
    }
    
    /**
     * Add a button to this Modal stage. The {@code handler} is invoked whenever the button is fired.
     * 
     * @param button The button to add.
     * @param handler Invoked whenever the button is fired.
     */
    public void addButton(ModalButton button, final EventHandler<ActionEvent> handler){
        Button b = button.getButton();
        b.setPrefWidth(BUTTON_PREF_WIDTH);
        b.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                close();
                if(handler != null){
                    handler.handle(t);
                }
            }
        });
        this.buttonsBox.getChildren().add(b);
    }
    
    /**
     * Add a message to the Modal stage messages.
     * 
     * @param text The message text.
     */
    public void addMessage(String text){
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMinWidth(MESSAGE_MIN_WIDTH);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        this.messagesBox.getChildren().add(message);
    }
}
