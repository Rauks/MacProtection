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

    /**
     * Create an {@link Object} with a {@code state}
     * <p/>
     * This {@code file} is a {@link HashedFile} or {@link Folder}
     *
     * @param file Object
     * @param state State
     */
    public FileWithState(Object file, FileState state) {
        this.file = file;
        this.state = state;
    }

    /**
     * Return Object file
     *
     * @return Object file
     */
    public Object getFile() {
        return file;
    }

    /**
     * Return HashedFile file
     *
     * @return HashedFile file
     */
    public HashedFile getHashedFile() {
        return (HashedFile) file;
    }

    /**
     * Return Folder file
     *
     * @return Folder file
     */
    public Folder getFolder() {
        return (Folder) file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    /**
     * Return State of the file
     *
     * @return state
     */
    public FileState getState() {
        return state;
    }

    public void setState(FileState state) {
        this.state = state;
    }

    /**
     * Return if file is a {@link HashedFile}
     *
     * @return boolean
     */
    public boolean isHashedFile() {
        return this.file instanceof HashedFile;
    }
}
