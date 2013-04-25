package cui.tree;

/**
 * Enum to describe state of file
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public enum FileState {

    EQUAL("E"), DELETED("D"), MODIFIED("M"), ADDED("A"), RENAMED("R");
    private String name;

    FileState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
