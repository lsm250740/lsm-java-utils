package com.lsm.jfiglet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import static com.lsm.jfiglet.FigletFont.convertOneLine;

/**
 * Command-line interface for JFiglet ASCII art generation.
 * Converts text messages into ASCII art using FIGlet fonts.
 */
public class JFiglet {

    /**
     * Main entry point for JFiglet command-line tool.
     * 
     * @param args Command line arguments:
     *             -f FONT_FILE: Specifies font file (file system, classpath, or URL)
     *             -o OUTPUT_FILE: Specifies output file (default: stdout)
     *             MESSAGE: Text to convert to ASCII art (last argument)
     * @throws IOException if font file cannot be read or output file cannot be written
     */
    public static void main(String[] args) throws IOException {
        // Initialize argument parsing variables
        final Iterator<String> arguments = Arrays.asList(args).iterator();
        String font = null;  // Font file path
        String text = null;  // Message to convert
        PrintStream out = System.out;  // Output stream (default: stdout)

        // Parse command line arguments
        while (arguments.hasNext()) {
            final String arg = arguments.next();
            if ("-f".equals(arg)) {
                // Font file option
                font = requireNextArgument(arguments, arg);
            } else if ("-o".equals(arg)) {
                // Output file option
                out = new PrintStream(new FileOutputStream(requireNextArgument(arguments, arg)));
            } else {
                // Assume remaining argument is the message text
                if (!arguments.hasNext()) {
                    text = arg;
                }
                break;
            }
        }
        // Generate and output ASCII art
        if (text == null) {
            // No message provided - show usage
            System.err.println(usage());
        } else if (font == null) {
            // Use default font
            out.println(convertOneLine(text));
        } else {
            // Use specified font
            out.println(convertOneLine(font, text));
        }
        out.close();
    }

    private static String requireNextArgument(Iterator<String> arguments, String sw) {
        if (arguments.hasNext()) {
            return arguments.next();
        }
        throw new IllegalStateException("Argument required after " + sw);
    }

    private static String usage() {
        final StringWriter result = new StringWriter();
        final PrintWriter pw = new PrintWriter(result);
        pw.println("Usage: java -jar jfiglet.jar [-f FLF] [-o OUTFILE] MESSAGE");
        pw.println("Prints MESSAGE to OUTFILE (default stdout) as ASCII art using Figlet font");
        pw.println("Example: java -jar jfiglet.jar -f \"/opt/myfont.flf\" \"Hello World\"");
        pw.println("\n");
        pw.println("Figlet font:");
        pw.println("  -f  FLF is font file location within file system, java classpath or www.");
        pw.println("      When FLF starts with `http://'|`https://' file will be fetched from WWW,");
        pw.println("      if FLF starts with `classpath:' then it will be looked for in JRE classpath,");
        pw.println("      otherwise FLF is path to FLF file in file system.");
        return result.toString();
    }
}
