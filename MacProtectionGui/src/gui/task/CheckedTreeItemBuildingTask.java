/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.task;

import core.tree.Folder;
import core.tree.HashedFile;
import gui.tree.ObservableFolder;
import gui.tree.ObservableHashedFile;
import java.util.HashSet;
import java.util.Iterator;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Karl
 */
public class CheckedTreeItemBuildingTask extends Task{
    Image nodeImage;
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0d);
    private int totalNodes;
    private int processedNodes;
    
    private Folder rootFolder;
    private Folder checkFolder;
    
    /**
     * Return the ProgressProperty of this Task.
     * 
     * @return The ProgressProperty of this Task.
     */
    public ReadOnlyDoubleProperty processProgressProperty(){
        return this.progress.getReadOnlyProperty();
    }

    /**
     * Build a {@link TreeItem} tree from a {@link Folder} tree.
     * 
     * @param rootFolder The local folder.
     * @param rootFolder The checking folder used to validate the local folder.
     */
    public CheckedTreeItemBuildingTask(Folder rootFolder, Folder checkFolder) {
        this.rootFolder = rootFolder;
        this.checkFolder = checkFolder;
        this.totalNodes = rootFolder.getAllSubFolders().size();
        this.processedNodes = 0;
        this.nodeImage = new Image(TreeItemBuildingTask.class.getResourceAsStream("/gui/res/folder.png"));
    }

    /**
     * Build a {@link TreeItem} node from a {@link Folder}.
     * 
     * @param folder The folder used to build the node.
     * @return The result node.
     */
    private TreeItem<ObservableFolder> buildFolderTreeItem(Folder folder, Folder check){
        TreeItem<ObservableFolder> node = new TreeItem<>();
        ObservableFolder oFolder = new ObservableFolder(folder);
        if(check == null){ 
            //Folder node dont exists in check tree
            oFolder.setInvalide();
            //Files added in local
            for(Iterator<HashedFile> it = folder.getFiles().iterator(); it.hasNext();){
                HashedFile file = it.next();
                ObservableHashedFile oFile;
                oFile = new ObservableHashedFile(new HashedFile(file.getName(), "Ajouté", file.getSize()));
                oFile.setInvalid();
                oFolder.addObservableHashedFile(oFile);
            }
            //Folders added in locel
            for(Folder f : folder.getSubFolders()){
                TreeItem<ObservableFolder> buildedNode = this.buildFolderTreeItem(f, null);
                buildedNode.getValue().setInvalide();
                buildedNode.getValue().setAdded();
                node.getChildren().add(buildedNode);
            }
        }
        else{ 
            //Folder node exists in check tree
            if(!folder.isConformTo(check)){
                oFolder.setInvalide();
            }
            //Files & files added in local
            for(Iterator<HashedFile> it = folder.getFiles().iterator(); it.hasNext();){
                HashedFile file = it.next();
                ObservableHashedFile oFile;
                HashedFile cFile = check.getFile(file.getName());
                if(cFile == null){ 
                    //File added
                    oFile = new ObservableHashedFile(new HashedFile(file.getName(), "Ajouté", file.getSize()));
                    oFile.setInvalid();
                }
                else{ 
                    //File exists
                    oFile = new ObservableHashedFile(file);
                    oFile.setCheckHash(cFile.getHash());
                }
                oFolder.addObservableHashedFile(oFile);
            }
            //Files deleted
            for(Iterator<HashedFile> it = check.getFiles().iterator(); it.hasNext();){
                HashedFile cFile = it.next();
                if(folder.getFile(cFile.getName()) == null){
                    ObservableHashedFile delFile = new ObservableHashedFile(new HashedFile(cFile.getName(), "Supprimé", cFile.getSize()));
                    delFile.setCheckHash(cFile.getHash());
                    delFile.setInvalid();
                    oFolder.addObservableHashedFile(delFile);
                }
            }
            //Folders & folders added
            HashSet<ObservableFolder> findedFolders = new HashSet<>();
            for(Folder f : folder.getSubFolders()){
                Folder c = check.getSubFolder(f.getName());
                if(c == null){
                    //Folder added locally
                    TreeItem<ObservableFolder> buildedNode = this.buildFolderTreeItem(f, null);
                    buildedNode.getValue().setInvalide();
                    buildedNode.getValue().setAdded();
                    node.getChildren().add(buildedNode);
                }
                else{
                    //Folder exists
                    TreeItem<ObservableFolder> buildedNode = this.buildFolderTreeItem(f, c);
                    findedFolders.add(buildedNode.getValue());
                    node.getChildren().add(buildedNode);
                }
            }
            //Deleted folders
            for(Iterator<Folder> it = check.getSubFolders().iterator(); it.hasNext();){
                Folder c = it.next();
                if(!findedFolders.contains(new ObservableFolder(c))){
                    TreeItem<ObservableFolder> buildedNode = this.buildFolderTreeItem(new Folder(c.getName()), c);
                    buildedNode.getValue().setInvalide();
                    buildedNode.getValue().setDeleted();
                    node.getChildren().add(buildedNode);
                }
            }
        }
        
        node.setValue(oFolder);
        node.setGraphic(new ImageView(this.nodeImage));
        this.progress.set((double) this.processedNodes++ / (double) this.totalNodes);
        return node;
    }
    
    /**
     * Invoked when the execution is requested by a {@link Thread}. A {@link TreeItem} tree is builded from a {@link Folder} tree.
     * 
     * @return The builded {@link TreeItem} tree.
     * @throws Exception An unhandled exception which occurred during the background operation.
     */
    @Override
    protected TreeItem<ObservableFolder> call() throws Exception {
        return buildFolderTreeItem(this.rootFolder, this.checkFolder);
    }
}
