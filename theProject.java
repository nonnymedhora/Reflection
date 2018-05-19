//import statements 

import java.lang.*; 
import java.lang.reflect.*; 
import java.awt.*; 
import java.awt.event.*; 

import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.JList;

import java.io.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.Properties;

import java.awt.print.*;

public class theProject extends JFrame {
    
    // io - file related stuff
      private JFileChooser  files;      // File dialog
      private String        filename;    // 
      private JLabel        statusInfo;
      
    // fields
    private static final int WIDTH = 700;
    private static final int HEIGHT = 700;
    
    // force objects
    private ClassLoaderFrame        theLoader;
    private ClassBrowserTestFrame   theBrowser;
    private myPBEEncryptor          theEncryptor;
    private myPBEDecryptor          theDecryptor;
    private TestPrint               tp;
      
    
    // fileds in the object for the UI controls
    private static final JTextField  tf_input = new JTextField();;//1    
    private JPanel      pa_thePanel;
    
    public theProject() {
            addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
              System.exit(0);
            }
         });
         this.setSize(WIDTH, HEIGHT);
         this.setFocusableWindowState(true);
       
    this.initUI();
    }   
    
    // methods
       /**********PRIVATE METHODS ****************/       
        /*   Initialize UI controls   */
       private  void  initUI() {
        
        Container   cp ;//= this.getContentPane();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Bawa's ClassProfiler");
          this.setSize(WIDTH, HEIGHT);      
          this.setResizable(true);    
          cp = this.getContentPane();
          cp.setLayout(new BorderLayout());

         // Setup Menus
        // Create toolbar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar (menuBar);
        
        // Create a menu labeled File, accelerator F
        JMenu file = new JMenu ("File");
        file.setMnemonic (KeyEvent.VK_F);   
        JMenuItem item;
        
        // Create a menu item Open, accelerator O
        // Have it call via the doOpenCommand()
        file.add (item = new JMenuItem ("Open"));
        item.setMnemonic (KeyEvent.VK_O);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doOpenCommand ();
        }
        });
        

        // Create a menu item Load, accelerator L
        // Have it call ClassLoaderTest via the doLoadCommand()
        file.add (item = new JMenuItem ("Load"));
        item.setMnemonic (KeyEvent.VK_L);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doLoadCommand ();
        }
        });
        // Create a menu item Encrypt, accelerator E
        // Have it call DesEncrypter's encrypt(InputStream in, OutputStream out)
        file.add (item = new JMenuItem ("Encrypt"));
        item.setMnemonic (KeyEvent.VK_E);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doEncryptCommand ();
        }
        });
        // Create a menu item Decrypt, accelerator D
        // Have it call DesEncrypter's dncrypt(InputStream in, OutputStream out)
        file.add (item = new JMenuItem ("Decrypt"));
        item.setMnemonic (KeyEvent.VK_D);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doDecryptCommand ();
        }
        });
        
        // Create a menu item Print, accelerator P
        // Have it call doPrintCommand
        file.add (item = new JMenuItem ("Print"));
        item.setMnemonic (KeyEvent.VK_P);
        item.addActionListener(new myPrinterCommand());
        
        // Create a menu item Exit, accelerator x
        // Have it call doCloseCommand when selected
        file.add (item = new JMenuItem ("Exit"));
        item.setMnemonic (KeyEvent.VK_X);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doExitCommand (0);
        }
        });
        
        // Add file menu to menu bar
        menuBar.add (file);
        
        // Create a menu labelled Inspect, accelerator I
        JMenu inspect = new JMenu("Inspect");
        inspect.setMnemonic (KeyEvent.VK_I);
        
        // Create a menu item Reflect, accelerator R
        // Have it call doReflectionCommand when selected
        inspect.add (item = new JMenuItem ("Reflect"));
        item.setMnemonic (KeyEvent.VK_R);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doReflectionCommand ();
        }
        });
        
        // Create a menu item Visualize, accelerator V
        // Have it call doVisualizeCommand when selected
        inspect.add (item = new JMenuItem ("Visualize"));
        item.setMnemonic (KeyEvent.VK_V);
        item.addActionListener (new ActionListener() {
        
        public void actionPerformed (ActionEvent e) {
        doVisualizeCommand ();
        }
        });
        
        // Add inspect menu to menu bar
        menuBar.add (inspect);
        
        
        // Create a menu labeled Help, accelerator H
        JMenu help = new JMenu ("Help");
        help.setMnemonic (KeyEvent.VK_H);
    
        // Create a menu item About, accelerator A
        // Have it call doAboutCommand when selected
        help.add (item = new JMenuItem ("About"));
        item.setMnemonic (KeyEvent.VK_A);
        item.addActionListener (new ActionListener() {
        public void actionPerformed (ActionEvent e) {
        doAboutCommand();
        }
        });
        
        // Add help menu to menu bar
        menuBar.add (help);     

        this.theBrowser = new ClassBrowserTestFrame();
            theBrowser.setVisible(false);        
        
        // user customizable settings //
        /// instantiate the panels individually first//
        
//            tf_input   = new JTextField();
            
            tf_input.setText("Type the class name to browse, ex.java.awt.Button");
                tf_input.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                    // add the class whose name is in the text field
                        try {
                        String text = tf_input.getText();
                        theBrowser.setVisible(true);
                        theBrowser.addClass(Class.forName(text));
                    // clear text field to indicate success
                        tf_input.setText("");
                     }
                        catch (ClassNotFoundException e) {
                            Toolkit.getDefaultToolkit().beep(); // make some noise
                    }
        }
        });
        
           cp.add(tf_input, BorderLayout.NORTH);
           cp.add(this.theBrowser, BorderLayout.CENTER);
           cp.setSize(this.getToolkit().getScreenSize());   //(700, 700);   
           
            this.pack();
            this.setVisible(true);    
    }
    
    public void doOpenCommand () {
        tp = new TestPrint();
        tp.show();
    }
   
   
    public void doAboutCommand() {
        String      aboutTheProject = "\tBaWa's Class Profiler\n\n\n  ";
            aboutTheProject = aboutTheProject.concat( "FILE\n\n    Open --- Open a java file \n\n    Load --- Load a class file (with 'main method') \n\n" + 
                                                  "    Encrypt --- Encrypt any file ('key' is Optional) \n\n" +
                                                  "    Decrypt --- Decrypt any file ('key' is Optional) \n\n" +
                                                  "    Print --- Print Class Structure information contents \n\n" +
                                                  "  INSPECT\n\n    Reflect --- Run class from tree (MUST have 'main') \n\n" +
                                                  "    Visualize ---- See the class diagram (coming soon)\n\n" +
                                                  "\u00a9  Navroz Medhora\nGolden Gate University\nAugust 2002" );
        JTextArea   aboutTextArea = new JTextArea();
            aboutTextArea.setEditable(false);
            aboutTextArea.setSize(500, 500);    
            aboutTextArea.setText(aboutTheProject);
            aboutTextArea.setVisible(true);
            
        JPanel      aboutPanel = new JPanel();
            aboutPanel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createTitledBorder("BaWa's Class Profiler"),
              BorderFactory.createEmptyBorder(0, 5, 5, 5)
    ));
        aboutPanel.add(aboutTextArea);
    
        JFrame      aboutFrame = new JFrame();
            aboutFrame.setSize(600,600);
            aboutFrame.setVisible(true);
            aboutFrame.setResizable(false); 
            aboutFrame.getContentPane().add(aboutPanel);
            
            aboutFrame.setSize(600,600);
            aboutFrame.setVisible(true);
            aboutFrame.setResizable(false);     
    }
  
   
    public void doLoadCommand() {
        this.theLoader = new ClassLoaderFrame();
        theLoader.setVisible(true);
    }
  
   
    public void doEncryptCommand() {
        theEncryptor = new myPBEEncryptor ();
        
    }
  
   
    public void doDecryptCommand() {
        theDecryptor = new myPBEDecryptor ();
    }
  
  
    public void doPrintCommand() {
//        pc = new PrintCommand();
        
    }
  
  
    public void doExitCommand (int status) {
        System.exit (status);
    }
  
   
    public void doReflectionCommand() {
        try {        
            // the user selected a different node--update description
      TreePath path = theBrowser.getTree().getSelectionPath();
      if (path == null) { return ;}
      DefaultMutableTreeNode selectedNode
         = (DefaultMutableTreeNode)path.getLastPathComponent();
      Class c = (Class)selectedNode.getUserObject();
      String[] args = new String[] {};

      Method m = c.getMethod("main", new Class[] { args.getClass() });
         m.invoke(null, new Object[] { args });
    }
    catch (NoSuchMethodException nsme) {
        JOptionPane.showMessageDialog(this, "WRONG", "No main method", JOptionPane.ERROR_MESSAGE);
        }
    catch (IllegalAccessException iae) { 
        JOptionPane.showMessageDialog( this, "ACCESS ERROR", "ILLEGAL", JOptionPane.ERROR_MESSAGE);
    }
    catch (InvocationTargetException e ) {
        JOptionPane.showMessageDialog( this, "TARGET ERROR", "CANNOT BE INVOKED", JOptionPane.ERROR_MESSAGE);};
    }

   
    public void doVisualizeCommand() {
   
    }

        
    
    public static void main(String args[]) throws Exception {
      theProject tPt   =   new theProject();
      tPt.initUI();
    }
    
    // Print a file into the text area.
  class myPrinterCommand implements ActionListener {
      Properties p = new Properties();
     
    public void actionPerformed (ActionEvent e) {
      PrintJob pjob = Toolkit.getDefaultToolkit().getPrintJob(theProject.this, "Cool Stuff", p);
      if (pjob != null) {
        Graphics pg = pjob.getGraphics();
        if (pg != null) {
          String s = theBrowser.textArea.getText();
          printLongString (pjob, pg, s);
          pg.dispose();
        }
        pjob.end();
      }
    }
}
    
    // Print string to graphics via printjob
  // Does not deal with word wrap or tabs
  void printLongString (PrintJob pjob, Graphics pg, String s) {
    int pageNum = 1;
    int linesForThisPage = 0;
    int linesForThisJob = 0;
    // Note: String is immutable so won't change while printing.
    if (!(pg instanceof PrintGraphics)) {
      throw new IllegalArgumentException ("Graphics context not PrintGraphics");
    }
    StringReader sr = new StringReader (s);
    LineNumberReader lnr = new LineNumberReader (sr);
    String nextLine;
    int pageHeight = pjob.getPageDimension().height;
    Font helv = new Font("Helvetica", Font.PLAIN, 12);
    //have to set the font to get any output
    pg.setFont (helv);
    FontMetrics fm = pg.getFontMetrics(helv);
    int fontHeight = fm.getHeight();
    int fontDescent = fm.getDescent();
    int curHeight = 0;
    try {
      do {
        nextLine = lnr.readLine();
        if (nextLine != null) {         
          if ((curHeight + fontHeight) > pageHeight) {
            // New Page
            System.out.println ("" + linesForThisPage + " lines printed for page " + pageNum);
            pageNum++;
            linesForThisPage = 0;
            pg.dispose();
            pg = pjob.getGraphics();
            if (pg != null) {
              pg.setFont (helv);
            }
            curHeight = 0;
          }
          curHeight += fontHeight;
          if (pg != null) {
            pg.drawString (nextLine, 0, curHeight - fontDescent);
            linesForThisPage++;
            linesForThisJob++;
          } else {
            System.out.println ("pg null");
          }
        }
      } while (nextLine != null);
    } catch (EOFException eof) {
      // Fine, ignore
    } catch (Throwable t) { // Anything else
      t.printStackTrace();
    }
    System.out.println ("" + linesForThisPage + " lines printed for page " + pageNum);
    System.out.println ("pages printed: " + pageNum);
    System.out.println ("total lines printed: " + linesForThisJob);
  }

  

}    
    