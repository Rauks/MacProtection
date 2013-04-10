/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

/**
 *
 * @author Karl
 */
public class MacProtectionController implements Initializable {
    
    @FXML
    private void handleQuitAction(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private ChoiceBox choiceAlgorithm;
    @FXML
    private PasswordField choicePassword;
    @FXML
    private Button choiceRoot;
    @FXML
    private TextField rootView;
    @FXML
    private TreeView treeView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
