package cui;

import com.martiansoftware.jsap.JSAPException;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
class CuiMessageException extends Exception {

    CuiMessageException(String message) {
        super(message);
    }

    CuiMessageException(JSAPException ex) {
        super(ex);
    }
    
}
