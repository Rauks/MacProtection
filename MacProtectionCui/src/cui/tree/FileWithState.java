package cui.tree;

import core.tree.Folder;
import core.tree.HashedFile;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class FileWithState {

    private Object file;
    private FileState state;

    public FileWithState(Object file, FileState state) {
        this.file = file;
        this.state = state;
    }

    public Object getFile() {
        return file;
    }

    public HashedFile getHashedFile() {
        return (HashedFile) file;
    }

    public Folder getFolder() {
        return (Folder) file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    public boolean isHashedFile() {
        return this.file instanceof HashedFile;
    }
}
