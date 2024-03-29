package gui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import gui.task.TreeItemBuildingTask;
import gui.task.MacProcessorTask;
import core.MacAlgorithm;
import core.MacAlgorithmException;
import core.check.CheckMacException;
import core.check.CheckReader;
import core.check.CheckReaderReadingException;
import core.check.CheckWriter;
import core.check.CheckWriterWritingException;
import core.processor.MacProcessor;
import core.processor.MacProcessorException;
import core.tree.Folder;
import gui.modal.ModalDialog;
import gui.task.CheckedTreeItemBuildingTask;
import gui.tree.ObservableFolder;
import gui.tree.ObservableHashedFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

/**
 *
 * @author Karl
 */
public class MacProtectionController implements Initializable {
    private DirectoryChooser dirChooser;
    private FileChooser fileChooser;
    private ReadOnlyBooleanWrapper isProcessing = new ReadOnlyBooleanWrapper(false);
    private ReadOnlyObjectWrapper<TreeItem<ObservableFolder>> rootNode = new ReadOnlyObjectWrapper<>();
    private ObservableList<ObservableHashedFile> filesList = FXCollections.observableArrayList();
    
    /**
     * Return the scene of this
     * 
     * @return 
     */
    private Scene getScene(){
        return this.mainPane.getScene();
    }
    
    /**
     * Handle a quit action. Will exit the application and all the registered working thread will be interrupted.
     * 
     * @param event The {@link ActionEvent} associated. 
     */
    @FXML
    private void handleQuitAction(ActionEvent event) {
        MacProtectionGui.interruptWorkingThread();
        Platform.exit();
    }
    
    /**
     * Show the about window.
     * 
     * @param event The {@link ActionEvent} associated. 
     */
    @FXML
    public void handleAbout(ActionEvent event){
        ModalDialog modal = new ModalDialog(ModalDialog.ModalType.INFO);
        modal.addButton(ModalDialog.ModalButton.OK);
        modal.addMessage("Utilitaire de Protection d’un Système de Fichier par hash Mac.");
        modal.addMessage("Projet de Sécurité & Cryptographie, ENSISA 2A IR.");
        modal.addMessage("Réalisé par Karl Woditsch & Georges Olivarès.");
        modal.showAndWait();
    }
    
    /**
     * Create a check file.
     * 
     * @param event The {@link ActionEvent} associated. 
     */
    @FXML
    private void handleCheckFileCreation(ActionEvent event){
        File fileToSave = this.fileChooser.showSaveDialog(this.getScene().getWindow());
        if(fileToSave != null){
            try {
                CheckWriter cw = new CheckWriter(new FileOutputStream(fileToSave), this.rootNode.get().getValue().getFolder(), new MacAlgorithm(this.choiceAlgorithm.getValue()), this.choicePassword.getText());
                cw.write();
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Fichier de validation créé.");
                modal.showAndWait();
            } catch (CheckMacException ex) {
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Erreur de creation du hash de signature Mac.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Password malformé.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MacAlgorithmException | NoSuchAlgorithmException ex) {
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Algorithme de hash Mac invalide.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CheckWriterWritingException | FileNotFoundException ex) {
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Erreur d'écriture du fichier de validation.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Load a check file and validate the current root tree with the checking tree of the check file.
     * 
     * @param event The {@link ActionEvent} associated. 
     */
    @FXML 
    private void handleCheckFileLoading(ActionEvent event){
        File checkFile = this.fileChooser.showOpenDialog(this.getScene().getWindow());
        if(checkFile != null){
            try {
                this.isProcessing.set(true);
                CheckReader cr = new CheckReader(new FileInputStream(checkFile), new MacAlgorithm(this.choiceAlgorithm.getValue()), this.choicePassword.getText());
                cr.read();
                Folder checkFolder = cr.getRootFolder();
                final CheckedTreeItemBuildingTask treeBuilder = new CheckedTreeItemBuildingTask(this.rootNode.get().getValue().getFolder(), checkFolder);
                final Thread treeBuilderThread = new Thread(treeBuilder);
                treeProgress.progressProperty().bind(treeBuilder.processProgressProperty());
                treeBuilder.setOnSucceeded(new EventHandler(){
                    @Override
                    public void handle(Event t) {
                        MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                        try {
                            TreeItem<ObservableFolder> root = (TreeItem<ObservableFolder>) treeBuilder.get();
                            rootNode.set(root);
                            isProcessing.set(false);
                            ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                            modal.addButton(ModalDialog.ModalButton.OK);
                            modal.addMessage("Validation terminée.");
                            modal.showAndWait();
                        } catch (InterruptedException | ExecutionException ex) {
                            isProcessing.set(false);
                            Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                EventHandler errorHandler = new EventHandler(){
                    @Override
                    public void handle(Event t) {
                        MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                        isProcessing.set(false);
                        ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                        modal.addButton(ModalDialog.ModalButton.OK);
                        modal.addMessage("Erreur de construction de l'arborescence des dossiers.");
                        modal.showAndWait();
                        Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, "Folders building failed.");
                    }
                };
                treeBuilder.setOnCancelled(errorHandler);
                treeBuilder.setOnFailed(errorHandler);
                MacProtectionGui.WORKING_THREADS.add(treeBuilderThread);
                treeBuilderThread.start();
            } catch (InvalidKeyException ex) {
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Password malformé.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException | CheckReaderReadingException ex) {
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Erreur de lecture du fichier de validation.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MacAlgorithmException | NoSuchAlgorithmException ex) {
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Algorithme de hash Mac invalide.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CheckMacException ex) {
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Fichier de validation corrumpu.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Change the files view to show the files of {@code folder}. 
     * 
     * @param folder The folder selected.
     */
    private void handleRootViewFolderSelected(ObservableFolder folder){
        this.filesList.clear();
        for(Iterator<ObservableHashedFile> it = folder.getObservableFiles().iterator(); it.hasNext();){
            ObservableHashedFile file = it.next();
            this.filesList.add(file);
        }
    }
    
    /**
     * Handle a root loading action. A new root folder tree will be builded.
     * 
     * @param event The {@link ActionEvent} associated. 
     */
    @FXML
    private void handleLoadRootAction(ActionEvent event) {
        this.dirChooser.setTitle("Racine du dossier");
        File dirToScan = this.dirChooser.showDialog(this.getScene().getWindow());
        if(dirToScan != null){
            try {
                this.isProcessing.set(true);
                this.rootNode.set(null);
                this.processorProgress.progressProperty().unbind();
                this.processorProgress.setProgress(0);
                this.treeProgress.progressProperty().unbind();
                this.treeProgress.setProgress(0);
                this.rootView.setText(dirToScan.getName());
                final MacProcessorTask processor = new MacProcessorTask(dirToScan, new MacAlgorithm(this.choiceAlgorithm.getValue()), this.choicePassword.getText(), MacProcessor.MacOutput.HEXADECIMAL);
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
                                        TreeItem<ObservableFolder> root = (TreeItem<ObservableFolder>) treeBuilder.get();
                                        rootNode.set(root);
                                        isProcessing.set(false);
                                        ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                                        modal.addButton(ModalDialog.ModalButton.OK);
                                        modal.addMessage("Calculs terminés.");
                                        modal.showAndWait();
                                    } catch (InterruptedException | ExecutionException ex) {
                                        rootView.setText("");
                                        isProcessing.set(false);
                                        Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                            EventHandler errorHandler = new EventHandler(){
                                @Override
                                public void handle(Event t) {
                                    MacProtectionGui.WORKING_THREADS.remove(treeBuilderThread);
                                    rootView.setText("");
                                    isProcessing.set(false);
                                    ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                                    modal.addButton(ModalDialog.ModalButton.OK);
                                    modal.addMessage("Erreur de construction de l'arborescence des dossiers.");
                                    modal.showAndWait();
                                    Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, "Folders tree building failed.");
                                }
                            };
                            treeBuilder.setOnCancelled(errorHandler);
                            treeBuilder.setOnFailed(errorHandler);
                            MacProtectionGui.WORKING_THREADS.add(treeBuilderThread);
                            treeBuilderThread.start();
                        } catch (InterruptedException | ExecutionException ex) {
                            rootView.setText("");
                            isProcessing.set(false);
                            Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                EventHandler errorHandler = new EventHandler(){
                    @Override
                    public void handle(Event t) {
                        MacProtectionGui.WORKING_THREADS.remove(processorThread);
                        rootView.setText("");
                        isProcessing.set(false);
                        ModalDialog modal = new ModalDialog(ModalDialog.ModalType.ERROR);
                        modal.addButton(ModalDialog.ModalButton.OK);
                        modal.addMessage("Erreur de calcul des hashs Mac.");
                        modal.addMessage("L'algorithme utilisé requiert un format de password invalide ou est incorrectement implémenté.");
                        modal.showAndWait();
                        Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, "Mac Processor failed.");
                    }
                };
                processor.setOnCancelled(errorHandler);
                processor.setOnFailed(errorHandler);
                MacProtectionGui.WORKING_THREADS.add(processorThread);
                processorThread.start();
            } catch (MacAlgorithmException ex) {
                this.rootView.setText("");
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Algorithme de hash Mac invalide.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MacProcessorException ex) {
                this.rootView.setText("");
                this.isProcessing.set(false);
                ModalDialog modal = new ModalDialog(ModalDialog.ModalType.VALID);
                modal.addButton(ModalDialog.ModalButton.OK);
                modal.addMessage("Dossier à scanner invalide.");
                modal.showAndWait();
                Logger.getLogger(MacProtectionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private AnchorPane mainPane;
    @FXML
    private MenuItem menuCheckCreation;
    @FXML
    private MenuItem menuCheckRead;
    @FXML
    private MenuItem menuQuit;
    @FXML
    private ChoiceBox<String> choiceAlgorithm;
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
    private ProgressIndicator workingIndicator;
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
        //User choices
        ObservableList<String> algorithms = FXCollections.observableArrayList();
        algorithms.addAll(MacAlgorithm.AVAILABLE_ALGORITHMS);
        this.choiceAlgorithm.setItems(algorithms);
        
        this.dirChooser = new DirectoryChooser();
        this.fileChooser = new FileChooser();
        this.fileChooser.getExtensionFilters().add(new ExtensionFilter("Fichier de vérification Mac", "*.checkmac"));
        this.fileChooser.getExtensionFilters().add(new ExtensionFilter("Autres fichiers", "*"));
        
        //Menu accelerators
        this.menuCheckCreation.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        this.menuCheckRead.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        this.menuQuit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        
        //Gui disable bindings
        this.choiceAlgorithm.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.choicePassword.disableProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.processorProgress.disableProperty().bind(this.isProcessing.getReadOnlyProperty().not());
        this.treeProgress.disableProperty().bind(this.isProcessing.getReadOnlyProperty().not());
        this.workingIndicator.visibleProperty().bind(this.isProcessing.getReadOnlyProperty());
        this.choiceRoot.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.choiceAlgorithm.valueProperty().isNull())
                                            .or(this.choicePassword.textProperty().isEqualTo("")));
        this.rootView.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        this.filesTable.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        this.treeView.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        this.menuCheckCreation.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        this.menuCheckRead.disableProperty().bind(this.isProcessing.getReadOnlyProperty()
                                            .or(this.treeView.rootProperty().isNull()));
        
        //Folders tree bindings
        this.treeView.rootProperty().bind(this.rootNode);
        this.treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    TreeItem<ObservableFolder> selectedItem = (TreeItem<ObservableFolder>) newValue;
                    handleRootViewFolderSelected(selectedItem.getValue());
                }
                else{
                    filesList.clear();
                }
            }
        });
        this.treeView.setCellFactory(new Callback<TreeView<ObservableFolder>, TreeCell<ObservableFolder>>() {
            private Image nodeImage = new Image(MacProtectionController.class.getResourceAsStream("res/folder.png"));
            
            @Override
            public TreeCell<ObservableFolder> call(TreeView<ObservableFolder> param) {
                return new TreeCell<ObservableFolder>() {
                    @Override
                    public void updateItem(ObservableFolder item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty){
                        this.setGraphic(new ImageView(nodeImage));
                            if (!isEmpty()) {
                                switch(item.getFlag()){
                                    case VALID:
                                        this.setTextFill(Color.GREEN);
                                        this.setText(item.toString());
                                        break;
                                    case INVALID:
                                        this.setTextFill(Color.RED);
                                        setText(item.toString());
                                        break;
                                    case DELETED:
                                        this.setTextFill(Color.ORANGE);
                                        setText(item.toString() + " (Supprimé)");
                                        break;
                                    case ADDED:
                                        this.setTextFill(Color.ORANGE);
                                        setText(item.toString() + " (Ajouté)");
                                        break;
                                }
                            }
                        }
                    }
                };
            }
        });
        
        //Files table bindings
        this.filesColumn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("name"));
        this.hashsColumn.setCellValueFactory(new PropertyValueFactory<ObservableHashedFile, String>("hash"));
        this.hashsColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<ObservableHashedFile, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            ObservableHashedFile file = this.getTableView().getItems().get(this.getTableRow().getIndex());
                            switch(file.getFlag()){
                                case VALID:
                                    this.setTextFill(Color.GREEN);
                                    this.setText(item);
                                    break;
                                case INVALID:
                                    this.setTextFill(Color.RED);
                                    setText(item);
                                    break;
                                case DELETED:
                                    this.setTextFill(Color.ORANGE);
                                    setText("Supprimé");
                                    break;
                                case ADDED:
                                    this.setTextFill(Color.ORANGE);
                                    setText("Ajouté");
                                    break;
                            }
                        }
                    }
                };
            }
        });
        this.sizesColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableHashedFile, String>, ObservableValue<String>>() {
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
        this.sizesColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell<ObservableHashedFile, String> cell = new TableCell<ObservableHashedFile, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            setText(item);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER_RIGHT);
                return cell;
            }
        });
        this.sizesColumn.setComparator(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String o1u = o1.substring(o1.length() - 2);
                String o2u = o2.substring(o2.length() - 2);
                if(o1u.equals(o2u)){
                    String o1c;
                    String o2c;
                    if(o1u.equals(" o")){
                        o1c = o1.substring(0, o1.length() - 2);
                        o2c = o2.substring(0, o2.length() - 2);
                    }
                    else{ //Ko Mo Go
                        o1c = o1.substring(0, o1.length() - 3);
                        o2c = o2.substring(0, o2.length() - 3);
                    }
                    o1c = o1c.replace(',', '.');
                    o2c = o2c.replace(',', '.');
                    return Double.compare(Double.parseDouble(o1c), Double.parseDouble(o2c));
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
        
        //Password autofocus
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                choicePassword.requestFocus();
            }
        });
    }    
}
