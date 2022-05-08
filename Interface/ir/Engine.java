/*  
 *   This file is based on the computer assignment for the
 *   Information Retrieval course at KTH. We reused the main 
 *   structure for our project assignment.
 *
 */

package ir;

import java.io.File;

/**
 *  This is the main class for the search engine.
 */
public class Engine {
	
	/** The local folder that stores all java files. */
	String dir_Name = "";

    /** The Elastic Search client */
    EsUtil es;

    /** The engine GUI. */
    SearchGUI gui;

    /** The file containing the logo. */
    String pic_file = "";

    public Engine( String[] args ) {
        decodeArgs( args );
        es = new EsUtil();
        gui = new SearchGUI( this );
        gui.init();
    }

    /**
     *   Decodes the command line arguments.
     */
    private void decodeArgs( String[] args ) {
        int i = 0;
        while ( i < args.length ) {
            if ( "-d".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
					dir_Name = args[i++];
                }
            } else if ( "-l".equals( args[i] )) {
                i++;
                if ( i < args.length ) {
                    pic_file = args[i++];
                }
            } else {
                System.err.println( "Unknown option: " + args[i] );
                break;
            }
        }
    }

    public static void main( String[] args ) {
        Engine e = new Engine( args );
    }
}
