package cui;

import com.martiansoftware.jsap.JSAPException;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
class CuiMessageException extends Exception {

    public CuiMessageException(String message) {
        super(message);
    }

    public CuiMessageException(JSAPException ex) {
        super(ex);
    }
}
