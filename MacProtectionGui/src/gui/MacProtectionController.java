package gui;

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
import gui.tree.ObservableHashedFile;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Comparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;

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
    
    private void handleRootViewFolderSelected(Folder folder){
        this.filesList.clear();
        for(Iterator<HashedFile> it = folder.getFiles().iterator(); it.hasNext();){
            HashedFile file = it.next();
            this.filesList.add(new ObservableHashedFile(file));
        }
    }
    
    @FXML
    private void handleLoadRootAction(ActionEvent event) {
        File dirToScan = this.dirChooser.showDialog(((Node)event.getTarget()).getScene().getWindow());
        this.isProcessing.set(true);
        this.rootNode.set(null);
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
    private TableColumn filesColumn;
    @FXML
    private TableColumn hashsColumn;
    @FXML
    private TableColumn sizesColumn;
    
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
        
        this.filesColumn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("name"));
        this.hashsColumn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("hash"));
        this.sizesColumn.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<ObservableHashedFile, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableHashedFile, String> p) {
                    long size = p.getValue().getSize();
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(0); 
                    if (size < 1024) {
                        return new SimpleStringProperty(size + " o");
                    } 
                    else if (size >= 1024 && size < 1048576){
                        return new SimpleStringProperty(df.format((double)size / 1024d) + " Ko");
                    }
                    else if (size >= 1048576 && size < 1073741824){
                        return new SimpleStringProperty(df.format((double)size / 1048576d) + " Mo");
                    }
                    else{
                        return new SimpleStringProperty(df.format((double)size / 1073741824d) + " Go");
                    }
                }
            });
        this.sizesColumn.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String o1u = o1.substring(o1.length() - 2, o1.length());
                String o2u = o2.substring(o2.length() - 2, o2.length());
                if(o1u.equals(o2u)){
                    return Collator.getInstance().compare(o1, o2);
                }
                else{
                    switch(o1u){
                        case " o":
                            return -1;
                        case "Ko":
                            if(o2u.equals(" o")){
                                return 1;
                            }
                            else{
                                return -1;
                            }
                        case "Mo":
                            if(o2u.equals(" o") || o2u.equals("Ko")){
                                return 1;
                            }
                            else{
                                return -1;
                            }
                        case "Go":
                            return 1;
                        default:
                            return 0;
                    }
                }
            }
        });
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
