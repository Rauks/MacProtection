/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.javaws.progress.Progress;
import core.MacAlgorithm;
import core.processor.MacProcessor;
import core.processor.MacProcessorEvent;
import core.processor.MacProcessorException;
import core.processor.MacProcessorListener;
import core.tree.Folder;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author Karl
 */
public class MacProtectionController implements Initializable {
    private DirectoryChooser dirChooser;
    private ReadOnlyDoubleWrapper processProgress = new ReadOnlyDoubleWrapper(0d);;
    
    @FXML
    private void handleQuitAction(ActionEvent event) {
        Platform.exit();
    }
    
    private TreeItem<Folder> buildFolderTreeItem(Folder folder){
        TreeItem<Folder> node = new TreeItem<>();
        node.setValue(folder);
        for(Folder f : folder.getSubFolders()){
            node.getChildren().add(this.buildFolderTreeItem(f));
        }
        return node;
    }
    
    @FXML
    private void handleLoadRootAction(ActionEvent event) {
        File dirToScan = this.dirChooser.showDialog(((Node)event.getTarget()).getScene().getWindow());
        if(dirToScan != null){
            this.rootView.setText(dirToScan.getName());
            processProgress.set(ProgressBar.INDETERMINATE_PROGRESS);
            try {
                MacProcessor p = new MacProcessor(dirToScan, this.choiceAlgorithm.getValue(), this.choicePassword.getText(), MacProcessor.MacOutput.BASE64);
                p.addMacProcessorListener(new MacProcessorListener() {
                    @Override
                    public void macProcessorPerformed(MacProcessorEvent evt) {
                        switch(evt.getState()){
                            case RUNNING:
                                processProgress.set((double)evt.getProcessedFiles() / (double)evt.getTotalFiles());
                                break;
                            case CANCELED:
                                processProgress.set(ProgressBar.INDETERMINATE_PROGRESS);
                                break;
                            case STARTED:
                                processProgress.set(0d);
                        }
                    }
                });
                p.process();
                Folder folder = p.getResult();
                this.treeView.setRoot(this.buildFolderTreeItem(folder));
                
                
            } catch (MacProcessorException ex) {
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            this.rootView.setText("");
        }
    }
    
    @FXML
    private ChoiceBox<MacAlgorithm> choiceAlgorithm;
    @FXML
    private PasswordField choicePassword;
    @FXML
    private Button choiceRoot;
    @FXML
    private TextField rootView;
    @FXML
    private TreeView treeView;
    @FXML
    private ProgressBar processorProgress;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<MacAlgorithm> algorithms = FXCollections.observableArrayList();
        algorithms.addAll(MacAlgorithm.getAlgorithms());
        this.choiceAlgorithm.setItems(algorithms);
        
        this.dirChooser = new DirectoryChooser();
        this.dirChooser.setTitle("Racine du dossier");
        
        this.processorProgress.progressProperty().bind(this.processProgress.getReadOnlyProperty());
    }    
}
