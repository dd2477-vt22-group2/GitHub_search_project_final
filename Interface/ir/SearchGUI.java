/*  
 *   This file is based on the computer assignment for the
 *   Information Retrieval course at KTH. We reused the main 
 *   structure for our project assignment.
 *
 */
package ir;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

/**
 *   A graphical interface to the information retrieval system.
 */
public class SearchGUI extends JFrame {

    /**  The search engine. */
    Engine engine;

    /**  The query posed by the user. */
    private Query query;

    /**  The results of a search query. */
    private ArrayList<PostingsEntry> results = new ArrayList<PostingsEntry>();
    
    /**  The number of results in the database, might not be equal to results.size() */
    private int totalResSize = 0;

    /**  The query type (either intersection, phrase, or ranked). */
    QueryType queryType = QueryType.FIELD_QUERY;

    /**  The ranking type (either tf-idf, pagerank, or combination). */
    RankingType rankingType = RankingType.TF_IDF;

    /**  Max number of results to display. */
    static final int MAX_RESULTS = 10;

    /** Demarkator between file name and file contents in the file contents text area*/
    private static final String MARKER = "----------------------------------------------------";
    
    /**  Show the whole file or from start line. */
    private boolean showAll = false;
    
    private String curr_file_name = null;
    
    private int curr_start_line = -1;


    /*
     *   Common GUI resources
     */
    public JCheckBox[] box = null;
    public JPanel resultWindow = new JPanel();
    private JScrollPane resultPane = new JScrollPane( resultWindow );
    public JCheckBox checkboxClass = new JCheckBox("Search for classes", false);
    public JCheckBox checkboxMethod = new JCheckBox("Search for methods", true);
    public JCheckBox checkboxName = new JCheckBox("Name", false);
    public JCheckBox checkboxSuper = new JCheckBox("Super class", false);
    public JCheckBox checkboxParam = new JCheckBox("Parameter", false);
    public JCheckBox checkboxReturn = new JCheckBox("Return type", false);
    public JCheckBox checkboxComment = new JCheckBox("Comment and body", false);
    public JTextField queryName = new JTextField( "", 30 );
    public JTextField querySuper = new JTextField( "", 30 );
    public JTextField queryParam = new JTextField( "", 30 );
    public JTextField queryReturn = new JTextField( "", 30 );
    public JTextField queryComment = new JTextField( "", 30 );
    public JButton searchButton = new JButton("Search");
    public JTextField queryWindow = new JTextField( "", 45 );
    public JTextArea docTextView = new JTextArea( "", 15, 28 );
    private JScrollPane docViewPane = new JScrollPane( docTextView );
    private Font queryFont = new Font( "Arial", Font.BOLD, 24 );
    private Font resultFont = new Font( "Font.DIALOG", Font.PLAIN, 16 );
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu( "File" );
    JMenu viewMenu = new JMenu( "View" );
    JMenu optionsMenu = new JMenu( "Search options" );
    JMenu rankingMenu = new JMenu( "Ranking score" );
    JMenuItem quitItem = new JMenuItem( "Quit" );
    JRadioButtonMenuItem showfromItem = new JRadioButtonMenuItem( "Show from start line" );
    JRadioButtonMenuItem showallItem = new JRadioButtonMenuItem( "Show whole file" );
    JRadioButtonMenuItem basicItem = new JRadioButtonMenuItem( "Field query" );
    JRadioButtonMenuItem keywordItem = new JRadioButtonMenuItem( "Multi-match query" );
    JRadioButtonMenuItem fuzzyItem = new JRadioButtonMenuItem( "Fuzzy query" );
    JRadioButtonMenuItem boostItem = new JRadioButtonMenuItem( "Boosting query" );
    JRadioButtonMenuItem tfidfItem = new JRadioButtonMenuItem( "TF-IDF" );
    ButtonGroup showoptions = new ButtonGroup();
    ButtonGroup queries = new ButtonGroup();
    ButtonGroup ranking = new ButtonGroup();
	JPanel cards = new JPanel(new CardLayout());
	JLabel boostInfoLabel = new JLabel("Select boosting fields: ", SwingConstants.CENTER);

    /**
     *  Constructor
     */
    public SearchGUI( Engine e ) {
        engine = e;
    }

    /**
     *  Sets up the GUI and initializes
     */
    void init() {
        // Create the GUI
        setSize( 600, 800 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        resultWindow.setLayout(new BoxLayout(resultWindow, BoxLayout.Y_AXIS));
        resultPane.setLayout(new ScrollPaneLayout());
        resultPane.setBorder( new EmptyBorder(10,10,10,0) );
        resultPane.setPreferredSize( new Dimension(400, 450 ));
        getContentPane().add(p, BorderLayout.CENTER);
        // Top menus
        menuBar.add( fileMenu );
        menuBar.add( viewMenu );
        menuBar.add( optionsMenu );
        menuBar.add( rankingMenu );
        fileMenu.add( quitItem );
        viewMenu.add( showfromItem );
        viewMenu.add( showallItem );
        optionsMenu.add( basicItem );
        optionsMenu.add( fuzzyItem );
        optionsMenu.add( keywordItem );
        optionsMenu.add( boostItem );
        rankingMenu.add( tfidfItem );
        showoptions.add( showfromItem );
        showoptions.add( showallItem );
        queries.add( basicItem );
        queries.add( keywordItem );
        queries.add( fuzzyItem );
        queries.add( boostItem );
        ranking.add( tfidfItem );
        showfromItem.setSelected( true );
        basicItem.setSelected( true );
        tfidfItem.setSelected( true );
        p.add( menuBar );
        // Logo
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
        p1.add( new JLabel( new ImageIcon( engine.pic_file )));
        p.add( p1 );
        // Search box
        JPanel queryTypeBlock = new JPanel();
        queryTypeBlock.setLayout(new BoxLayout(queryTypeBlock, BoxLayout.X_AXIS));
        queryTypeBlock.add( checkboxMethod );
        queryTypeBlock.add( checkboxClass );
        p.add( queryTypeBlock );
        // p2 for keyword search
        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		JLabel guideLabel = new JLabel("User Guide", SwingConstants.CENTER);
        queryWindow.setFont( queryFont );
        queryWindow.setMaximumSize( queryWindow.getPreferredSize() );
        
        JPanel boostPanel = new JPanel();
        boostPanel.setLayout(new BoxLayout(boostPanel, BoxLayout.X_AXIS));
        boostPanel.add( boostInfoLabel );
        boostPanel.add( checkboxName );
        boostPanel.add( checkboxSuper );
        boostPanel.add( checkboxParam );
        boostPanel.add( checkboxReturn );
        boostPanel.add( checkboxComment );
        
        p2.add( queryWindow );
        p2.add(Box.createRigidArea(new Dimension(0,4)));
        p2.add( boostPanel );
        // p3 for field/fuzzy search
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
        
        JPanel queryNameBlock = new JPanel();
        queryNameBlock.setLayout(new BoxLayout(queryNameBlock, BoxLayout.X_AXIS));
        JLabel queryNameLabel = new JLabel("Name ", SwingConstants.CENTER);
        queryName.setMaximumSize( queryName.getPreferredSize() );
        queryNameBlock.add( queryNameLabel );
        queryNameBlock.add( queryName );
        
        JPanel querySuperBlock = new JPanel();
        querySuperBlock.setLayout(new BoxLayout(querySuperBlock, BoxLayout.X_AXIS));
        JLabel querySuperLabel = new JLabel("Super classes ", SwingConstants.CENTER);
        querySuper.setMaximumSize( querySuper.getPreferredSize() );
        querySuperBlock.add( querySuperLabel );
        querySuperBlock.add( querySuper );
        
        JPanel queryParamBlock = new JPanel();
        queryParamBlock.setLayout(new BoxLayout(queryParamBlock, BoxLayout.X_AXIS));
        JLabel queryParamLabel = new JLabel("Parameters ", SwingConstants.CENTER);
        queryParam.setMaximumSize( queryParam.getPreferredSize() );
        queryParamBlock.add( queryParamLabel );
        queryParamBlock.add( queryParam );
        
        JPanel queryReturnBlock = new JPanel();
        queryReturnBlock.setLayout(new BoxLayout(queryReturnBlock, BoxLayout.X_AXIS));
        JLabel queryReturnLabel = new JLabel("Return type ", SwingConstants.CENTER);
        queryReturn.setMaximumSize( queryReturn.getPreferredSize() );
        queryReturnBlock.add( queryReturnLabel );
        queryReturnBlock.add( queryReturn );
        
        JPanel queryCommentBlock = new JPanel();
        queryCommentBlock.setLayout(new BoxLayout(queryCommentBlock, BoxLayout.X_AXIS));
        JLabel queryCommentLabel = new JLabel("Comments or body ", SwingConstants.CENTER);
        queryComment.setMaximumSize( queryComment.getPreferredSize() );
        queryCommentBlock.add( queryCommentLabel );
        queryCommentBlock.add( queryComment );
        
        p3.add( queryNameBlock );
        p3.add(Box.createRigidArea(new Dimension(0,4)));
        p3.add( querySuperBlock );
        p3.add(Box.createRigidArea(new Dimension(0,4)));
        p3.add( queryParamBlock );
        p3.add(Box.createRigidArea(new Dimension(0,4)));
        p3.add( queryReturnBlock );
        p3.add(Box.createRigidArea(new Dimension(0,4)));
        p3.add( queryCommentBlock );
        p3.add(Box.createRigidArea(new Dimension(0,4)));
        p3.add( searchButton );
        
        cards.add(p3, "Field mode");
        cards.add(p2, "Keyword mode");
        p.add( cards );
        p.add( resultPane );

        docTextView.setFont(resultFont);
        docTextView.setText("\n  The contents of the document will appear here.");
        docTextView.setLineWrap(true);
        docTextView.setWrapStyleWord(true);
        p.add(docViewPane);
        setVisible( true );
        hideBoostCheckboxes();

        /*
         *  Searches for documents matching the string in the search box, and displays
         *  the first few results.
         */
        Action search_keyword = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                // Empty the results window
                displayInfoText( " " );
                // Turn the search string into a Query
                String queryString = queryWindow.getText().toLowerCase().trim();
                if (checkboxClass.isSelected() && !checkboxMethod.isSelected()) {
					query = new Query(null, 1, null, null, null, queryString);
				} else if (!checkboxClass.isSelected() && checkboxMethod.isSelected()) {
					query = new Query(null, 2, null, null, null, queryString);
				} else {
					query = new Query(null, 0, null, null, null, queryString);
				}
                long startTime = System.currentTimeMillis();
                search();
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Display the first few results + a button to see all results.
                //
                // We don't want to show all results directly since the displaying itself
                // might take a long time, if there are many results.
                if ( results.size() != 0 ) {
                    displayResults( MAX_RESULTS, elapsedTime/1000.0 );
                } else {
                    displayInfoText( "Found 0 matching document" );
                }
            }
		};
        // A search is carried out when the user presses "return" in the search box.
        queryWindow.registerKeyboardAction( search_keyword, "", KeyStroke.getKeyStroke( "ENTER" ), JComponent.WHEN_FOCUSED );

		Action search_field = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                displayInfoText( " " );
                // Turn the search string into a Query
                String Name = queryName.getText().toLowerCase().trim();
                String Super = querySuper.getText().toLowerCase().trim();
                String Param = queryParam.getText().toLowerCase().trim();
                String Return = queryReturn.getText().toLowerCase().trim();
                String Comment = queryComment.getText().toLowerCase().trim();
                if (checkboxClass.isSelected() && !checkboxMethod.isSelected()) {
					query = new Query(Name, 1, Super, Param, Return, Comment);
				} else if (!checkboxClass.isSelected() && checkboxMethod.isSelected()) {
					query = new Query(Name, 2, Super, Param, Return, Comment);
				} else {
					query = new Query(Name, 0, Super, Param, Return, Comment);
				}
                long startTime = System.currentTimeMillis();
                search();
                long elapsedTime = System.currentTimeMillis() - startTime;
                if ( results.size() != 0 ) {
                    displayResults( MAX_RESULTS, elapsedTime/1000.0 );
                } else {
                    displayInfoText( "Found 0 matching document" );
                }
            }
		};
		searchButton.addActionListener( search_field );

		Action switchShowAll = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				showAll = true;
				if (curr_file_name != null && curr_start_line != -1) {
					displayFile( curr_file_name, curr_start_line );
				}
            }
        };
        showallItem.addActionListener( switchShowAll );
        
        Action switchShowFrom = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				showAll = false;
				if (curr_file_name != null && curr_start_line != -1) {
					displayFile( curr_file_name, curr_start_line );
				}
            }
        };
        showfromItem.addActionListener( switchShowFrom );

		Action switchFieldQuery = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				curr_file_name = null;
				curr_start_line = -1;
				displayInfoText( " " );
				docTextView.setText("\n  The contents of the document will appear here.");
				hideBoostCheckboxes();
				queryType = QueryType.FIELD_QUERY;
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Field mode");
            }
        };
        basicItem.addActionListener( switchFieldQuery );
        
        Action switchFuzzyQuery = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				curr_file_name = null;
				curr_start_line = -1;
				displayInfoText( " " );
				docTextView.setText("\n  The contents of the document will appear here.");
				hideBoostCheckboxes();
				queryType = QueryType.FUZZY_QUERY;
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Field mode");
            }
        };
        fuzzyItem.addActionListener( switchFuzzyQuery );

		Action switchKeywordQuery = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				curr_file_name = null;
				curr_start_line = -1;
				displayInfoText( " " );
				docTextView.setText("\n  The contents of the document will appear here.");
				hideBoostCheckboxes();
				queryType = QueryType.KEYWORD_QUERY;
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Keyword mode");
            }
        };
        keywordItem.addActionListener( switchKeywordQuery );
        
        Action switchBoostQuery = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
				curr_file_name = null;
				curr_start_line = -1;
				displayInfoText( " " );
				docTextView.setText("\n  The contents of the document will appear here.");
				showBoostCheckboxes();
				queryType = QueryType.BOOST_QUERY;
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Keyword mode");
            }
        };
        boostItem.addActionListener( switchBoostQuery );

        Action quit = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        };
        quitItem.addActionListener( quit );

        Action setTfidfRanking = new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                rankingType = RankingType.TF_IDF;
            }
        };
        tfidfItem.addActionListener( setTfidfRanking );

    }
    
    void showBoostCheckboxes() {
		boostInfoLabel.setVisible( true );
		checkboxClass.setVisible( false );
		checkboxMethod.setVisible( false );
		checkboxName.setVisible( true );
		checkboxSuper.setVisible( true );
		checkboxParam.setVisible( true );
		checkboxReturn.setVisible( true );
		checkboxComment.setVisible( true );
	}
	
	void hideBoostCheckboxes() {
		boostInfoLabel.setVisible( false );
		checkboxClass.setVisible( true );
		checkboxMethod.setVisible( true );
		checkboxName.setVisible( false );
		checkboxSuper.setVisible( false );
		checkboxParam.setVisible( false );
		checkboxReturn.setVisible( false );
		checkboxComment.setVisible( false );
	}

	void search() {
		curr_file_name = null;
		curr_start_line = -1;
        try {
            results.clear();
            SearchResponse<DocEntry> res = null;
            if (queryType == QueryType.FIELD_QUERY) {
                res = engine.es.fieldSearch(query.name, query.is_class, query.super_types, query.parameters, query.return_type, query.comments_and_fields);
            } else if (queryType == QueryType.KEYWORD_QUERY) {
				if (query.is_class == 0) {
					res = engine.es.keywordSearch(query.comments_and_fields);
				} else {
					res = engine.es.keywordSearchWithIsClass(query.comments_and_fields, query.is_class);
				}
            } else if (queryType == QueryType.FUZZY_QUERY) {
				res = engine.es.fuzzySearch(query.name, query.is_class, query.super_types, query.parameters, query.return_type, query.comments_and_fields);
            } else if (queryType == QueryType.BOOST_QUERY) {
				int coeffName = 1;
				int coeffSuper = 1;
				int coeffParam = 1;
				int coeffReturn = 1;
				int coeffComment = 1;
				if (checkboxName.isSelected()) {
					coeffName = 2;
				}
				if (checkboxSuper.isSelected()) {
					coeffSuper = 2;
				}
				if (checkboxParam.isSelected()) {
					coeffParam = 2;
				}
				if (checkboxReturn.isSelected()) {
					coeffReturn = 2;
				}
				if (checkboxComment.isSelected()) {
					coeffComment = 2;
				}
                res = engine.es.keywordSearchWithFieldBoost(query.comments_and_fields, coeffName, coeffSuper, coeffParam, coeffReturn, coeffComment);
            } else {
				System.err.println( "Invalid query type!" );
			}
            if (res != null) {
                totalResSize = (int) res.hits().total().value();
                for (Hit<DocEntry> hit : res.hits().hits()) {
                    if (hit.source() != null) {
                        results.add(new PostingsEntry(hit.source().file_name.split("\\\\java_files\\\\")[1], hit.source().file_start_line, hit.source().file_end_line, hit.score()));
                    }
                }
            } else {
                totalResSize = 0;
            }
        } catch (IOException e) {
            results.clear();
            totalResSize = 0;
        }
	}

	// null method, for test only
	//void search() {
		//System.out.println( query.toStr() );
		//curr_file_name = null;
		//curr_start_line = -1;
		//results.clear();
		//results.add(new PostingsEntry("AffordanceModel.java", "449", "40", 20.0));
		//totalResSize = 0;
	//}

    void displayInfoText( String info ) {
        resultWindow.removeAll();
        JLabel label = new JLabel( info );
        label.setFont( resultFont );
        resultWindow.add( label );
        revalidate();
        repaint();
    }
    
    void displayFile(String fileName, int startLine) {
		String contents = "";
        String line;
        int cnt_line = 0;
		if (showAll) {
			contents = "Displaying contents of " + fileName + "\n" + MARKER + "\n";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(engine.dir_Name + "\\/" + fileName), "UTF-8"))) {
				while ((line = br.readLine()) != null) {
					cnt_line++;
					contents += cnt_line + " " + line.trim() + "\n";
                }
            } catch (FileNotFoundException exc) {
            } catch (IOException exc) {
            } catch (NullPointerException exc) {
            }                                   
            docTextView.setText(contents);
            SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					docViewPane.getVerticalScrollBar().setValue(startLine * 23 + 46);
				}
			});
		} else {
			contents = "Displaying contents of " + fileName + " from line " + startLine + "\n" + MARKER + "\n";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(engine.dir_Name + "\\/" + fileName), "UTF-8"))) {
				while ((line = br.readLine()) != null) {
					cnt_line++;
					if (cnt_line >= startLine) {
						contents += cnt_line + " " + line.trim() + "\n";
					}
                }
            } catch (FileNotFoundException exc) {
            } catch (IOException exc) {
            } catch (NullPointerException exc) {
            }                                   
            docTextView.setText(contents);
            docTextView.setCaretPosition( 0 );
		}
	}
    
    void displayResults( int maxResultsToDisplay, double elapsedTime ) {
        displayInfoText( String.format( "Found %d matching document(s) in %.3f seconds", totalResSize, elapsedTime ));
        docTextView.setText("\n  The contents of the document will appear here.");
        box = new JCheckBox[maxResultsToDisplay];
        int i;
        for ( i=0; i<results.size() && i<maxResultsToDisplay; i++ ) {
            String description = i + ". " +  results.get(i).toStr();
            box[i] = new JCheckBox();
            box[i].setSelected( false );

            JPanel result = new JPanel();
            result.setAlignmentX(Component.LEFT_ALIGNMENT);
            result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));

            JLabel label = new JLabel(description);
            label.setFont( resultFont );

            MouseAdapter showDocument = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
					String [] fileInfo = ((JLabel)e.getSource()).getText().split(" ");
                    String fileName = fileInfo[1];
                    int startLine = Integer.parseInt(fileInfo[4]);
                    curr_file_name = fileName;
                    curr_start_line = startLine;
                    displayFile( fileName, startLine );
                }
            };
            label.addMouseListener(showDocument);
            result.add(box[i]);
            result.add(label);

            resultWindow.add( result );
        }
        // If there were many results, give the user an option to see all of them.
        if ( i<results.size() ) {
            JPanel actionButtons = new JPanel();
            actionButtons.setLayout(new BoxLayout(actionButtons, BoxLayout.X_AXIS));
            actionButtons.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton display10MoreBut = new JButton( "Display 10 more results" );
            display10MoreBut.setFont( resultFont );
            actionButtons.add( display10MoreBut );
            Action display10More = new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    displayResults( (int)this.getValue("resCurSize") + 10, elapsedTime );
                }
            };
            display10More.putValue("resCurSize", i);
            display10MoreBut.addActionListener( display10More );

            actionButtons.add(Box.createRigidArea(new Dimension(5,0)));

            JButton displayAllBut = new JButton( "Display all " + results.size() + " results" );
            displayAllBut.setFont( resultFont );
            actionButtons.add( displayAllBut );
            Action displayAll = new AbstractAction() {
                public void actionPerformed( ActionEvent e ) {
                    displayResults( results.size(), elapsedTime );
                }
            };
            displayAllBut.addActionListener( displayAll );

            resultWindow.add(actionButtons);
        }
        revalidate();
        repaint();
    }
}
