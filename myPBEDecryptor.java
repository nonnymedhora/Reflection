
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class myPBEDecryptor extends Frame {
    private int WIDTH = 400;
    private int HEIGHT = 300;
    private int BUFFER_SIZE = 8;
    private TextField inputTF;
    
    public static void main(String[] args) {
        new myPBEDecryptor();
    }
 
    public myPBEDecryptor() {
        super("Bawa's File Decryptor");
        setup();
        pack();
        setSize(WIDTH,HEIGHT);
        addWindowListener(new WindowHandler());
        setVisible(true);
    }

 void setup() {
      // Create panel for password input
      Panel inputPanel = new Panel();
      inputPanel.setLayout(new GridLayout(8,3));
      Label decryptLabel_1 = new Label("DECRYPTION...."); 
        decryptLabel_1.setFont(new Font("Monospaced",Font.BOLD,18));
      Label decryptLabel_2 = new Label("1 Enter pass phrase (Optional)");
      Label decryptLabel_3 = new Label("2 Press \"Decrypt\" OR File >> Decrypt"); 
      Label decryptLabel_4 = new Label("3 Select File to Decrypt");
      Label decryptLabel_5 = new Label("4 Choose File to Save Decrypted Output");
      Label thePhrase      = new Label(" Enter pass phrase:");
      inputTF = new TextField(70);
        inputTF.setFont(new Font("Monospaced",Font.PLAIN,14));
        inputTF.setEchoChar('*');
      Button decrypt = new Button("Decrypt");
        decrypt.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                decryptFile();
            }
        });
        
      inputPanel.add(decryptLabel_1);
      inputPanel.add(decryptLabel_2);
      inputPanel.add(decryptLabel_3);
      inputPanel.add(decryptLabel_4);
      inputPanel.add(decryptLabel_5);
      inputPanel.add(thePhrase);
      inputPanel.add(inputTF);
      inputPanel.add(decrypt);
      
      add("North",inputPanel);
       // Setup menu bar
      MenuBar menuBar = new MenuBar();
      Menu fileMenu = new Menu("File");
      MenuItem fileDecrypt = new MenuItem("Decrypt");       
      MenuItem fileExit = new MenuItem("Exit");
      fileMenu.add(fileDecrypt);
      
      fileMenu.add(fileExit);
      fileDecrypt.addActionListener(new MenuItemHandler());
      fileExit.addActionListener(new MenuItemHandler());
      menuBar.add(fileMenu);
      setMenuBar(menuBar);
     }

     Cipher createCipher(int mode) throws Exception {
      // Get the pass phrase out of the text field
      char[] charArray = inputTF.getText().toCharArray();
      // Create a PBEKeySpec from the pass phrase
      PBEKeySpec keySpec = new PBEKeySpec(charArray);
      // Create a SecretKeyFactory
      SecretKeyFactory keyFactory = 
       SecretKeyFactory.getInstance("PBEWithMD5AndDES");
      // Generate a secret key using the keyspec
      SecretKey key = keyFactory.generateSecret(keySpec);
      // Create the salt using the MD5 digest of the pass phrase
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(inputTF.getText().getBytes());
      byte[] digest = md.digest();
      byte[] salt = new byte[8];
      for(int i=0;i<8;++i) salt[i] = digest[i];
      // Create a PBEParameterSpec using the salt
      PBEParameterSpec paramSpec = new PBEParameterSpec(salt,20);
      // Create an instance of the cipher
      Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
      // Initialize it for encryption/decryption with the key and salt
      cipher.init(mode,key,paramSpec); 
      return cipher;
     }
     
      void decryptFile() {
          try {
           // Create and initialize a cipher
           Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
           // Select input file
           String inPathString = 
            getPath("Select file to decrypt",FileDialog.LOAD);
           if(inPathString == null) return;
           // Select output file
           String outPathString = 
            getPath("Save decrypted file as",FileDialog.SAVE);
           if(outPathString == null) return;
           // Open files and encrypt/decrypt
           applyCipher(inPathString,outPathString,cipher,"Decrypting ...");
          }catch(Exception ex) {
           ex.printStackTrace();
          } 
      }

     
     void applyCipher(String inFile,String outFile,Cipher cipher,
   String message) throws Exception {
  // Open input file using a CiperInputStream
  CipherInputStream in = new CipherInputStream(
   new BufferedInputStream(new FileInputStream(inFile)),cipher);
  // Open output file
  BufferedOutputStream out = new BufferedOutputStream(
   new FileOutputStream(outFile));
  // Notify user that encryption/decryption is in progress
  WaitDialog dialog = new WaitDialog(message);
  dialog.pack();
  dialog.setBounds(100,100,150,100);
  dialog.show();
  // Encrypt/decrypt file
  byte[] buffer = new byte[BUFFER_SIZE];
  int numRead = 0;
  do {
   numRead = in.read(buffer);
   if(numRead > 0) out.write(buffer,0,numRead);
  } while(numRead == 8);
  in.close();
  out.close();
  // Remove dialog box after a second
  Thread.sleep(1000);
  dialog.hide();
  dialog.dispose(); 
 }
 
 String getPath(String title,int mode) {
  FileDialog fd = new FileDialog(this,title,mode);
  fd.show();
  // If file a file has not been selected then return null
  String inDirectoryString = fd.getDirectory();
  String inFileString = fd.getFile();
  if(inDirectoryString == null || inFileString == null) 
   return null;
  return inDirectoryString + inFileString; 
 }
  class MenuItemHandler implements ActionListener {
  public void actionPerformed(ActionEvent e) {
   String s = e.getActionCommand();
   if(s.equals("Decrypt")) decryptFile();
   else{
    setVisible(false);
    dispose();
   }
  }
 }
 class WindowHandler extends WindowAdapter {
  public void windowClosing(WindowEvent e) {
   setVisible(false);
   dispose();
  }
 }
 class WaitDialog extends Dialog {
  public WaitDialog(String s) {
   super(myPBEDecryptor.this,"Please wait");
   setLayout(new BorderLayout());
   add("Center",new Label(s,Label.CENTER));
  }
 }
}

 
    
