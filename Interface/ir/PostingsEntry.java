/*  
 *   This file is based on the computer assignment for the
 *   Information Retrieval course at KTH. We reused the main 
 *   structure for our project assignment.
 *
 */

package ir;

import java.util.*;

public class PostingsEntry {

    public String FileName;
    public String StartLine;
    public String EndLine;
    public double Score;
    
    public PostingsEntry(String FileName, String StartLine, String EndLine, double Score) {
		this.FileName = FileName;
		this.StartLine = StartLine;
		this.EndLine = EndLine;
		this.Score = Score;
    }

	// Do not change this function
	// If necessary, remember to change "MouseAdapter showDocument" function in the GUI
	public String toStr() {
		return this.FileName + " from line " + this.StartLine + " score: " + this.Score;
    }
}
