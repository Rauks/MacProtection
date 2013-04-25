package cui.tree;

import core.tree.Folder;
import core.tree.HashedFile;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Create a Tree of {@link HashedFile} and {@link Folder}
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class DetailedTree {

    private final Folder physicalRoot;

    /**
     * Construct {@link DetailedTree} with a {@link Folder}
     *
     * @param physicalRoot Physical root Folder
     */
    public DetailedTree(Folder physicalRoot) {
        this.physicalRoot = physicalRoot;
    }

    /**
     * Create a {@link TreeMap} with differences between {@code physicalRoot}
     * and {@code validationRoot}
     *
     * @param validationFolder folder to compare
     * @return TreeMap<String, FileWithState>
     */
    public TreeMap<String, FileWithState> create(Folder validationFolder) {
        TreeMap<String, FileWithState> returnTree = new TreeMap<>();

        for (Iterator<Folder> it = this.physicalRoot.getAllSubFolders().iterator(); it.hasNext();) {
            Folder file = it.next();
            FileWithState hashedFileWithFlag = new FileWithState(file, this.hasFile(validationFolder, file));
            returnTree.put(this.printFileRoot(file.getParent()) + file.getName(), hashedFileWithFlag);
        }

        for (Iterator<Folder> it = validationFolder.getAllSubFolders().iterator(); it.hasNext();) {
            Folder file = it.next();
            if (this.hasFile(this.physicalRoot, file) == FileState.ADDED) {
                FileWithState hashedFileWithFlag = new FileWithState(file, FileState.DELETED);
                returnTree.put(this.printFileRoot(file.getParent()) + file.getName(), hashedFileWithFlag);
            }
        }

        for (Iterator<HashedFile> it = this.physicalRoot.getAllFiles().iterator(); it.hasNext();) {
            HashedFile file = it.next();
            FileWithState hashedFileWithFlag = new FileWithState(file, this.hasFile(validationFolder, file));
            returnTree.put(this.printFileRoot(file.getParent()) + file.getName(), hashedFileWithFlag);
        }

        for (Iterator<HashedFile> it = validationFolder.getAllFiles().iterator(); it.hasNext();) {
            HashedFile file = it.next();
            if (this.hasFile(this.physicalRoot, file) == FileState.ADDED) {
                FileWithState hashedFileWithFlag = new FileWithState(file, FileState.DELETED);
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

    private FileState hasFile(Folder validationFolder, Folder file) {
        for (Iterator<Folder> it = physicalRoot.getAllSubFolders().iterator(); it.hasNext();) {
            Folder folder = it.next();

            if (folder.getName().equals(file.getName())) {
                if (folder.equals(file)) {
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
