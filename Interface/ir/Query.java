/*  
 *   This file is based on the computer assignment for the
 *   Information Retrieval course at KTH. We reused the main 
 *   structure for our project assignment.
 *
 */ 

package ir;

import java.util.*;
import java.nio.charset.*;
import java.io.*;

public class Query {
	
	// When the name is null or "", we will launch "keyword" search
	// Otherwise, we will search the database according to the name, class, super_calss, etc.
	String name = null;
	
	// 0 for all, 1 for class, 2 for function
	int is_class = 0;
	
	String super_types = null;
	
	String parameters = null;
	
	String return_type = null;
	
	String comments_and_fields = null;
	
    /**
     *  Constructor
     */
    public Query(String name, int is_class, String super_types, String parameters, String return_type, String comments_and_fields) {
		this.name = name;
		this.is_class = is_class;
		this.super_types = super_types;
		this.parameters = parameters;
		this.return_type = return_type;
		this.comments_and_fields = comments_and_fields;
    }
    
    //public Query( String queryString ) {
		//int i = 0;
		//String [] args = queryString.split(" ");
		//String curr_type = "";
        //while ( i < args.length ) {
			//if ( "-n".equals( args[i] )) {
                //i++;
                //curr_type = "-n";
            //} else if ( "-c".equals( args[i] )) {
                //i++;
                //this.is_class = 1;
                //curr_type = "";
            //} else if ( "-f".equals( args[i] )) {
                //i++;
                //this.is_class = 2;
                //curr_type = "";
            //} else if ( "-s".equals( args[i] )) {
                //i++;
                //curr_type = "-s";
			//} else if ( "-p".equals( args[i] )) {
                //i++;
                //curr_type = "-p";
            //} else if ( "-r".equals( args[i] )) {
                //i++;
                //curr_type = "-r";
            //} else if ( "-cf".equals( args[i] )) {
                //i++;
                //curr_type = "-cf";
            //} else if ( "-n".equals( curr_type ) && i < args.length ) {
				//if ( this.name == null ) {
					//this.name = args[i++];
				//} else {
					//this.name = this.name + " " + args[i++];
				//}
            //} else if ( "-s".equals( curr_type ) && i < args.length ) {
				//if ( this.super_types == null ) {
					//this.super_types = args[i++];
				//} else {
					//this.super_types = this.super_types + " " + args[i++];
				//}
            //} else if ( "-p".equals( curr_type ) && i < args.length ) {
				//if ( this.parameters == null ) {
					//this.parameters = args[i++];
				//} else {
					//this.parameters = this.parameters + " " + args[i++];
				//}
            //} else if ( "-r".equals( curr_type ) && i < args.length ) {
				//if ( this.return_type == null ) {
					//this.return_type = args[i++];
				//} else {
					//this.return_type = this.return_type + " " + args[i++];
				//}
            //} else if ( "-cf".equals( curr_type ) && i < args.length ) {
				//if ( this.comments_and_fields == null ) {
					//this.comments_and_fields = args[i++];
				//} else {
					//this.comments_and_fields = this.comments_and_fields + " " + args[i++];
				//}
            //} else {
				//if ( this.comments_and_fields == null ) {
					//this.comments_and_fields = args[i++];
				//} else {
					//this.comments_and_fields = this.comments_and_fields + " " + args[i++];
				//}
			//}
        //}
    //}
    
    public String toStr() {
		String res = "";
		if (this.name == null || "".equals(this.name)) {
			res += "Search type: Keyword\n";
			if (this.is_class == 0) {
				res += "Type: Both\n";
			} else if (this.is_class == 1) {
				res += "Type: Class\n";
			} else {
				res += "Type: Method\n";
			}
			res += "Keywords: " + this.comments_and_fields + "\n";
		} else {
			res += "Search type: Signature\n";
			res += "Name: " + this.name + "\n";
			if (this.is_class == 0) {
				res += "Type: Both\n";
			} else if (this.is_class == 1) {
				res += "Type: Class\n";
			} else {
				res += "Type: Method\n";
			}
			res += "Super class: " + this.super_types + "\n";
			res += "Parameters: " + this.parameters + "\n";
			res += "Return types: " + this.return_type + "\n";
			res += "Comments or body: " + this.comments_and_fields + "\n";
		}
		return res;
    }
}
