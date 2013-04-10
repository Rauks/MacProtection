
import core.processor.MacProcessor;
import core.tree.Folder;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

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
    private TreeItem<Folder> buildFolderTreeItem(Folder folder){
        TreeItem<Folder> node = new TreeItem<>();
        node.setValue(folder);
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
    protected TreeItem<Folder> call() throws Exception {
        return buildFolderTreeItem(this.rootFolder);
    }
}
