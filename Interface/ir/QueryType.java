/*  
 *   This file is based on the computer assignment for the
 *   Information Retrieval course at KTH. We reused the main 
 *   structure for our project assignment.
 *
 */

package ir;

public enum QueryType {
    FIELD_QUERY, // The basic intersetion search based on different fields
    KEYWORD_QUERY,
    FUZZY_QUERY,
    BOOST_QUERY
}
