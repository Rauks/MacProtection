/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import core.MacAlgorithm;
import core.processor.MacProcessor;
import core.processor.MacProcessorException;
import core.tree.Folder;
import core.tree.HashedFile;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import tree.ObservableHashedFile;

/**
 *
 * @author Karl
 */
public class MacProtectionController implements Initializable {
    private DirectoryChooser dirChooser;
    private ReadOnlyBooleanWrapper isProcessing = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyObjectWrapper<TreeItem<Folder>> rootNode = new ReadOnlyObjectWrapper<>();
    private ObservableList<ObservableHashedFile> filesList = FXCollections.observableArrayList();
        
    @FXML
    private void handleQuitAction(ActionEvent event) {
        MacProtectionGui.interruptWorkingThread();
        Platform.exit();
    }
    
    @FXML
    private void handleRootViewFolderSelected(Folder folder){
        this.filesList.clear();
        for(Iterator<HashedFile> it = folder.getFiles().iterator(); it.hasNext();){
            HashedFile file = it.next();
            this.filesList.add(new ObservableHashedFile(file.getName(), file.getHash()));
        }
    }
    
    @FXML
    private void handleLoadRootAction(ActionEvent event) {
        File dirToScan = this.dirChooser.showDialog(((Node)event.getTarget()).getScene().getWindow());
        this.isProcessing.set(true);
        this.processorProgress.progressProperty().unbind();
        this.processorProgress.setProgress(0);
        this.treeProgress.progressProperty().unbind();
        this.treeProgress.setProgress(0);
        if(dirToScan != null){
            try {
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
                            treeProgress.progressProperty().bind(treeBuilder.processProgressProperty());
                            treeBuilder.setOnSucceeded(new EventHandler(){
                                @Override
                                public void handle(Event t) {
                                    MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                                    try {
                                        TreeItem<Folder> root = (TreeItem<Folder>) treeBuilder.get();
                                        rootNode.set(root);
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
            } catch (MacProcessorException ex) {
                this.rootView.setText("");
                this.isProcessing.set(false);
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    @FXML
    private TableView filesTable;
    @FXML
    private TableColumn filesColomn;
    @FXML
    private TableColumn hashsColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<MacAlgorithm> algorithms = FXCollections.observableArrayList();
        algorithms.addAll(MacAlgorithm.getAlgorithms());
        this.choiceAlgorithm.setItems(algorithms);
        this.choiceAlgorithm.setValue(MacAlgorithm.HmacSHA256);
        
        this.dirChooser = new DirectoryChooser();
        this.dirChooser.setTitle("Racine du dossier");
        
        this.choiceAlgorithm.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.choicePassword.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.processorProgress.disableProperty().bind(this.isProcessing.getReadOnlyProperty().not());
        this.treeProgress.disableProperty().bind(this.isProcessing.getReadOnlyProperty().not());
        this.choiceRoot.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.choiceAlgorithm.valueProperty().isNull())
                                            .or(this.choicePassword.textProperty().isEqualTo("")));
        this.rootView.disableProperty().bind(this.treeView.rootProperty().isNull());
        this.filesTable.disableProperty().bind(this.treeView.rootProperty().isNull());
        this.treeView.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        
        this.treeView.rootProperty().bind(this.rootNode);
        this.treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    TreeItem<Folder> selectedItem = (TreeItem<Folder>) newValue;
                    handleRootViewFolderSelected(selectedItem.getValue());
                }
                else{
                    filesList.clear();
                }
            }
        });
        
        this.filesColomn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("name"));
        this.hashsColumn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("hash"));
        this.filesTable.setItems(this.filesList);
        this.filesTable.setPlaceholder(new Label("Aucun fichier dans ce dossier"));
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                choicePassword.requestFocus();
            }
        });
    }    
}
