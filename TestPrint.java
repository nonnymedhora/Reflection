import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class TestPrint extends Frame {
  TextArea textArea;
  Label statusInfo;
  Button loadButton, printButton, closeButton;
  Properties p = new Properties();
    
  public TestPrint() {
    super ("File Opener");
    add (statusInfo = new Label(), "North");
    Panel p = new Panel ();
    p.add (loadButton = new Button ("Open"));
        loadButton.addActionListener( new LoadFileCommand() );
    p.add (printButton = new Button ("Print"));
        printButton.addActionListener( new PrintCommand() );
    p.add (closeButton = new Button ("Close"));
        closeButton.addActionListener( new CloseCommand() );        
    add (p, "South");
    add (textArea = new TextArea (10, 40), "Center");
    pack();
  }
  
  public static void main (String args[]) {
    TestPrint f = new TestPrint();
    f.show();
  }

  // Bail Out
  class CloseCommand implements ActionListener {
    public void actionPerformed (ActionEvent e) {
        dispose();
    }
  }
    
  // Load a file into the text area.
  class LoadFileCommand implements ActionListener {
    public void actionPerformed (ActionEvent e) {
      int state;
      String msg;
      FileDialog file = new FileDialog (TestPrint.this, "Load // Open File", FileDialog.LOAD);
      file.setFile ("*.java");  // Set initial filename filter
      file.show(); // Blocks
      String curFile;
      if ((curFile = file.getFile()) != null) {
        String filename = file.getDirectory() + curFile;
        char[] data;
        setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        File f = new File (filename);
        try {
          FileReader fin = new FileReader (f);
          int filesize = (int)f.length();
          data = new char[filesize];
          fin.read (data, 0, filesize);
        } catch (FileNotFoundException exc) {
          String errorString = "File Not Found: " + filename;
          data = errorString.toCharArray ();
        } catch (IOException exc) {
          String errorString = "IOException: " + filename;
          data = errorString.toCharArray ();
        }
        statusInfo.setText ("Load / Open: " + filename);
        textArea.setText (new String (data));
        setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    }
  }

  // Print a file into the text area.
  class PrintCommand implements ActionListener {
    public void actionPerformed (ActionEvent e) {
      PrintJob pjob = getToolkit().getPrintJob(TestPrint.this, "Cool Stuff", p);
      if (pjob != null) {
        Graphics pg = pjob.getGraphics();
        if (pg != null) {
          String s = textArea.getText();
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