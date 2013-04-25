package cui.tree;

import core.tree.HashedFile;
import java.io.File;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class HashedFileWithState extends HashedFile {

    private FileState state;

    /**
     * Create a {@code HashedFileWithState} from a {@code file} and his
     * {@code hash}.
     *
     * @param name The name of the file.
     * @param hash The hash of the file.
     */
    public HashedFileWithState(File file, String hash) {
        super(file, hash);
    }

    /**
     * Create a {@code HashedFileWithState} from a {@code file}, the hash will be
     * {@code null}.
     *
     * @param name The name of the file.
     */
    public HashedFileWithState(File file) {
        super(file);
    }

    /**
     * Create a {@code HashedFileWithState} from another {@code hashedFile}. Can
     * be used to make a copy of a {@code HashedFileWithState}.
     *
     * @param name The name of the file.
     */
    public HashedFileWithState(HashedFile hashedFile) {
        super(hashedFile);
    }

    public void setState(FileState state) {
        this.state = state;
    }

    public FileState getState() {
        return this.state;
    }
}
