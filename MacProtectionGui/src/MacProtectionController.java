/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import core.MacAlgorithm;
import core.processor.MacProcessor;
import core.tree.Folder;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
    private ReadOnlyBooleanWrapper isProcessing = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyObjectWrapper<TreeItem<Folder>> rootNode = new ReadOnlyObjectWrapper<>(new TreeItem<Folder>());
        
    @FXML
    private void handleQuitAction(ActionEvent event) {
        MacProtectionGui.interruptWorkingThread();
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
        this.isProcessing.set(true);
        if(dirToScan != null){
            this.rootView.setText(dirToScan.getName());
            final MacProcessorTask processor = new MacProcessorTask(dirToScan, this.choiceAlgorithm.getValue(), this.choicePassword.getText(), MacProcessor.MacOutput.HEXADECIMAL);
            final Thread processorThread = new Thread(processor);
            this.processorProgress.progressProperty().bind(processor.processProgressProperty());
            processor.setOnSucceeded(new EventHandler(){
                @Override
                public void handle(Event t) {
                    MacProtectionGui.WORKING_THREADS.remove(processorThread);
                    try {
                        Folder result = (Folder) processor.get();
                        final TreeItemBuildingTask treeBuilder = new TreeItemBuildingTask(result);
                        final Thread treeBuilderThread = new Thread(treeBuilder);
                        treeBuilder.setOnSucceeded(new EventHandler(){
                            @Override
                            public void handle(Event t) {
                                MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                                try {
                                    TreeItem<Folder> root = (TreeItem<Folder>) treeBuilder.get();
                                    System.out.println(root);
                                    rootNode.set(root);
                                    Logger.getLogger(MacProtectionController.class.getName()).log(Level.INFO, "Done.");
                                } catch (InterruptedException | ExecutionException ex) {
                                    Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
                                } finally{
                                    isProcessing.set(false);
                                }
                            }
                        });
                        treeBuilder.setOnFailed(new EventHandler(){
                            @Override
                            public void handle(Event t) {
                                MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                                isProcessing.set(false);
                                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, "Tree building failed.");
                            }
                        });
                        MacProtectionGui.WORKING_THREADS.add(treeBuilderThread);
                        treeBuilderThread.start();
                    } catch (InterruptedException | ExecutionException ex) {
                        Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
                        isProcessing.set(false);
                    }
                }
            });
            processor.setOnFailed(new EventHandler(){
                @Override
                public void handle(Event t) {
                     MacProtectionGui.WORKING_THREADS.remove(processorThread);
                     isProcessing.set(false);
                     Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, "Processor failed.");
                }
            });
            MacProtectionGui.WORKING_THREADS.add(processorThread);
            processorThread.start();
        }
        else{
            this.rootView.setText("");
            this.isProcessing.set(false);
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
    @FXML
    private ProgressBar treeProgress;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<MacAlgorithm> algorithms = FXCollections.observableArrayList();
        algorithms.addAll(MacAlgorithm.getAlgorithms());
        this.choiceAlgorithm.setItems(algorithms);
        this.choiceAlgorithm.setValue(MacAlgorithm.HmacSHA256);
        this.choiceAlgorithm.setMaxWidth(Double.MAX_VALUE);
        
        this.dirChooser = new DirectoryChooser();
        this.dirChooser.setTitle("Racine du dossier");
        
        this.choiceAlgorithm.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.choicePassword.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.choiceRoot.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.rootView.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.treeView.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        
        this.treeView.rootProperty().bind(this.rootNode);
    }    
}
