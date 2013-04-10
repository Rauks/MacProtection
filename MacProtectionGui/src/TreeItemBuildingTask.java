
import core.tree.Folder;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Karl
 */
public class TreeItemBuildingTask extends Task{
    private Folder rootFolder;

    public TreeItemBuildingTask(Folder rootFolder) {
        this.rootFolder = rootFolder;
    }

    private TreeItem<Folder> buildFolderTreeItem(Folder folder){
        TreeItem<Folder> node = new TreeItem<>();
        node.setValue(folder);
        for(Folder f : folder.getSubFolders()){
            node.getChildren().add(this.buildFolderTreeItem(f));
        }
        return node;
    }
    
    @Override
    protected TreeItem<Folder> call() throws Exception {
        return buildFolderTreeItem(this.rootFolder);
    }
}
