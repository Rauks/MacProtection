package cui;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;

/**
 *
 * @author Georges OLIVARES <dev@olivares-georges.fr>
 */
public class MacProtectionOptionsFactory {

    public static void password(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("password")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('p')
                .setLongFlag("password");
        opt.setHelp("Password used to crypt datas");
        jsap.registerParameter(opt);
    }

    public static void algorithm(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("algo")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('a')
                .setLongFlag("algo");
        opt.setHelp("Algoritm");
        jsap.registerParameter(opt);
    }

    public static void source(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("source")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(false)
                .setShortFlag('s')
                .setDefault(".")
                .setLongFlag("source");
        opt.setHelp("Source file");
        jsap.registerParameter(opt);
    }

    public static void checkFile(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("check_file")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('c')
                .setLongFlag("check");
        opt.setHelp("Checksum file to compare (generated with 'export' command)");
        jsap.registerParameter(opt);
    }

    public static void saveFile(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("file_save")
                .setStringParser(JSAP.STRING_PARSER)
                .setRequired(true)
                .setShortFlag('f')
                .setLongFlag("file");
        opt.setHelp("File to save");
        jsap.registerParameter(opt);
    }

    public static void tree(JSAP jsap) throws JSAPException {
        FlaggedOption opt = new FlaggedOption("tree")
                .setStringParser(JSAP.BOOLEAN_PARSER)
                .setRequired(false)
                .setDefault("true")
                .setShortFlag('t')
                .setLongFlag(JSAP.NO_LONGFLAG);
        opt.setHelp("List recursively all files and folders");
        jsap.registerParameter(opt);
    }
}
