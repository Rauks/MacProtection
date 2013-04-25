package cui.tree;

import core.tree.Folder;
import core.tree.HashedFile;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class DetailedTree {

    private final Folder physicalRoot;

    public DetailedTree(Folder physicalRoot) {
        this.physicalRoot = physicalRoot;
    }

    public TreeMap<String, HashedFileWithState> create(Folder validationFolder) {
        TreeMap<String, HashedFileWithState> returnTree = new TreeMap<>();

        for (Iterator<HashedFile> it = this.physicalRoot.getAllFiles().iterator(); it.hasNext();) {
            HashedFile file = it.next();
            HashedFileWithState hashedFileWithFlag = new HashedFileWithState(file);
            hashedFileWithFlag.setState(this.hasFile(validationFolder, file));
            returnTree.put(this.printFileRoot(file.getParent()) + file.getName(), hashedFileWithFlag);
        }

        for (Iterator<HashedFile> it = validationFolder.getAllFiles().iterator(); it.hasNext();) {
            HashedFile file = it.next();
            if (this.hasFile(this.physicalRoot, file) == FileState.ADDED) {
                HashedFileWithState hashedFileWithFlag = new HashedFileWithState(file);
                hashedFileWithFlag.setState(FileState.DELETED);
                returnTree.put(this.printFileRoot(file.getParent()) + file.getName(), hashedFileWithFlag);
            }
        }

        return returnTree;
    }

    private FileState hasFile(Folder physicalRoot, HashedFile file) {
        for (Iterator<HashedFile> it = physicalRoot.getAllFiles().iterator(); it.hasNext();) {
            HashedFile hashedFile = it.next();

            if (hashedFile.getName().equals(file.getName())) {
                if (hashedFile.equals(file)) {
                    return FileState.EQUAL;
                } else {
                    return FileState.MODIFIED;
                }
            }
        }

        return FileState.ADDED;
    }

    private String printFileRoot(Folder folder) {
        if (folder.getParent() != null) {
            return this.printFileRoot(folder.getParent()) + folder.getName() + "/";
        }
        return folder.getName() + "/";
    }
}
