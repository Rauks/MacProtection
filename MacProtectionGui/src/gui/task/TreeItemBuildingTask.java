package gui.task;


import core.processor.MacProcessor;
import core.tree.Folder;
import core.tree.HashedFile;
import gui.tree.ObservableFolder;
import gui.tree.ObservableHashedFile;
import java.util.Iterator;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Used to build a {@link TreeItem} tree from a {@link Folder} tree.
 * 
 * @author Karl
 */
public class TreeItemBuildingTask extends Task{
    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0d);
    private int totalNodes;
    private int processedNodes;
    
    private Folder rootFolder;
    
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
     * @param rootFolder 
     */
    public TreeItemBuildingTask(Folder rootFolder) {
        this.rootFolder = rootFolder;
        this.totalNodes = rootFolder.getAllSubFolders().size();
        this.processedNodes = 0;
    }

    /**
     * Build a {@link TreeItem} node from a {@link Folder}.
     * 
     * @param folder The folder used to build the node.
     * @return The result node.
     */
    private TreeItem<ObservableFolder> buildFolderTreeItem(Folder folder){
        TreeItem<ObservableFolder> node = new TreeItem<>();
        ObservableFolder oFolder = new ObservableFolder(folder);
        for(Iterator<HashedFile> it = folder.getFiles().iterator(); it.hasNext();){
            oFolder.addObservableHashedFile(new ObservableHashedFile(it.next()));
        }
        node.setValue(oFolder);
        for(Folder f : folder.getSubFolders()){
            node.getChildren().add(this.buildFolderTreeItem(f));
        }
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
        return buildFolderTreeItem(this.rootFolder);
    }
}
