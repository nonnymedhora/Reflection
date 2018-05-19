/**
 * ClassBrowserTest
 * @author NMedhora
 */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.lang.ClassLoader;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class ClassBrowserTest {  
    public static void main(String[] args) {
     
     JInternalFrame frame = new ClassBrowserTestFrame();
     frame.show();
   }
}

class ClassBrowserTestFrame extends JInternalFrame
   implements ActionListener, TreeSelectionListener {
   private DefaultMutableTreeNode   root;
   private DefaultTreeModel         model;
   private JTree                    tree;
   public JTextArea                textArea;
   
   JScrollPane              treePane;
   JScrollPane              areaPane;
   
   public ClassBrowserTestFrame() {  
        setTitle("ClassBrowserTest");
        setSize(300, 200);
        setVisible(true);
        setResizable(true);

      // the root of the class tree is Object
      root = new DefaultMutableTreeNode(java.lang.Object.class);
      model = new DefaultTreeModel(root);
      tree = new JTree(model);

        
      // add this class to populate the tree with some data
      addClass(java.lang.Object.class);//getClass()

      // set up selection mode
      tree.addTreeSelectionListener(this);
      int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
      tree.getSelectionModel().setSelectionMode(mode);

      // this text area holds the class description
      textArea = new JTextArea();
        textArea.setEditable( false );
      
      // add tree and text area to the content pane
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(1, 2));
      
      treePane = new JScrollPane(tree);
      areaPane = new JScrollPane(textArea);
       
        panel.add(treePane);
        panel.add(areaPane);



      Container contentPane = getContentPane();
      contentPane.add(panel, "Center");
   }

   public void actionPerformed(ActionEvent event) {
   }

   public void valueChanged(TreeSelectionEvent event) {
    // the user selected a different node--update description
      TreePath path = tree.getSelectionPath();
      if (path == null) { return ;}
      DefaultMutableTreeNode selectedNode
         = (DefaultMutableTreeNode)path.getLastPathComponent();
      Class c = (Class)selectedNode.getUserObject();
      
	  String packageDescription		= getPackageDescription(c);
	  String superDescription 		= getSuperDescription(c);
      String ctorDescription 		= getConstructorDescription(c);
      String innerClassDescription 	= getInnerClassDescription(c);
      String interfaceDescription 	= getInterfaceDescription(c);
      String fieldDescription 		= getFieldDescription(c);
      String methodDescription 		= getMethodDescription(c);
//      String subClassDescription 	= getSubClassDescription(c);
     
      textArea.setText( "\t\t============================\n" + 
                        "\t\tCLASS ==>  " + c.getName() + "\n" +
                        "\t\t============================\n"
                      );
      textArea.append(packageDescription);
      textArea.append(superDescription);      
      textArea.append(ctorDescription);
      textArea.append(interfaceDescription);
      textArea.append(fieldDescription);
      textArea.append(methodDescription);
      textArea.append(innerClassDescription);
//      textArea.append(subClassDescription);
   }

   public DefaultMutableTreeNode findUserObject(Object obj) {
    // find the node containing a user object
      Enumeration e = root.breadthFirstEnumeration();
      while (e.hasMoreElements()) {
        DefaultMutableTreeNode node  = (DefaultMutableTreeNode)e.nextElement();
         if (node.getUserObject().equals(obj)) {
            return node;
        }
      }
      return null;
   }

   public DefaultMutableTreeNode addClass(Class c) {
    // add a new class to the tree

      // skip non-class types
      if (c.isInterface() || c.isPrimitive()) { 
        return null;  
      }

      // if the class is already in the tree, return its node
      DefaultMutableTreeNode node = findUserObject(c);
      if (node != null) {
        return node;
      }

      // class isn't present--first add class parent recursively
      Constructor[] cn = c.getDeclaredConstructors();
      Class s = c.getSuperclass();
      Class e[] = c.getInterfaces();
      Class n[] = c.getClasses();

      DefaultMutableTreeNode parent;
      if (s == null) {
         parent = root;
      }
      else {
         parent = addClass(s);
      }

      parent = addClass(s);

      // add the class as a child to the parent
      DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(c);
      model.insertNodeInto(newNode, parent, parent.getChildCount());

      // make node visible
      TreePath path = new TreePath(model.getPathToRoot(newNode));
      tree.makeVisible(path);

      return newNode;
   }
   
   public static String getPackageDescription(Class c) {
    	String r = "PACKAGE SPECIFICATION\n===============\n";
    	Package p = c.getPackage();
    	if ( p != null) {
	    	Package [] pq;
			r += "Package Name => \t" + p.getName() + "\n";  
			r += "Implementation => \t"  + p.getImplementationTitle() + "\n";
			r += "Implentation Vendor => \t" + p.getImplementationVendor() + "\n";
			r += "Implentation Version => \t" + p.getImplementationVersion() + "\n";
			r += "Specification => \t" + p.getSpecificationTitle() + "\n";
			r += "Specification Vendor => \t" + p.getSpecificationVendor() + "\n";
			r += "Specification Version => \t" + p.getSpecificationVersion() + "\n";
	    
	    } else {
	    	r += "NONE\n";
	    }
    	return r += "\n";
    }


   
   
        /*
        * Each class has an array of constructors
        * Each constructor has one return type - void and is public
        * Each ctor can have an array of parameters
        */
    
    public static String getConstructorDescription(Class c) {
        // use reflection to identify the ctors
        String r = "CONSTRUCTORS\n============\n";
        Constructor[] cn = c.getDeclaredConstructors();
        for(int i = 0; i < cn.length; i++) {
            Constructor ctor = cn[i];
            Class paramCt[] = ctor.getParameterTypes();
            r += "public  void  " +  ctor.getName() + "  " ;
            
            if (paramCt.length == 0) { r += "(  ) \n";}
            else {
                r += "( ";
            
               for (int j = 0; j < paramCt.length; j++) {
                   r += paramCt[j] + "  ";
               }
               r+= " )\n";        
        }
            }
            r += "\n";  
            return r;
        }
        
                /*
        * Each class has an array of constructors
        * Each constructor has one return type - void and is public
        * Each ctor can have an array of parameters
        */
    
    public static String getSuperDescription(Class c) {
        // use reflection to identify the ctors
        String r = "Super Class Hierarchy\n============\n";
        if ( c.getName() != "java.lang.Object") {
	        Class theSuper = c.getSuperclass();
	        Vector supers = new Vector(10,2);
			supers.addElement(theSuper.getName());
	        
	        try {
	        	while ( theSuper != Class.forName("java.lang.Object")) { 
	        	theSuper = theSuper.getSuperclass();  
	        	supers.addElement(theSuper.getName());     	
	        	} 
	        	supers.trimToSize();
	        	
	        	for (int i = supers.size()-1; i >= 0; i--) {
	        		r += String.valueOf(supers.elementAt(i)) + "\n";	
	        	} 
	        
	            r += "\n";  
	          
	        }
	        catch (ClassNotFoundException cnfe) {
	        	cnfe.printStackTrace();
	        }
	     } else {
	     	r += "NONE\n\n";
	     }
	        return r;     	
    }
        
    
        
        
        
        
   
   /*
   *    Each class may have an array of Interfaces
   *    Get the full class path for same
   */
   
   public static String getInterfaceDescription(Class c) {
        // use reflection to identify interfaces implemented by the class c
        String r = "INTERFACES IMPLEMENTED\n======================\n";
        Class[] interfaces = c.getInterfaces();
        if (interfaces.length == 0) {
            r += "NONE\n";
        }
        else {
            for(int i = 0; i < interfaces.length; i++) {
                Class ie = interfaces[i];
                r += ie.getName() + "\n";
            }
        }
        r += "\n";
        return r;
   }
   
   /*
   * Each class has an array of class fields
   * Fields may be 
   */

   public static String getFieldDescription (Class c) {
        
    // use reflection to find types and names of fields
      String r = "CLASS FIELDS\n============\n";
      Field[] fields = c.getDeclaredFields();
      if (fields.length == 0) {
      	r += "NONE\n";
      } else {      	
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
         
            if ((f.getModifiers() & Modifier.STATIC) != 0) {
                r += "static  ";
            }
         
            if ((f.getModifiers() & Modifier.PUBLIC) != 0) {
                r += "public  ";
            } else if ((f.getModifiers() & Modifier.PRIVATE) != 0) {
                r += "private  ";
            }
            else {
                r += "protected  ";
            }
            
            r += f.getType().getName() + "  "  + f.getName() + "\n";//+ f.get(c)         
        }

    }
    r += "\n"; 
    return r;      
   }
   
   public static String getMethodDescription (Class c) {
    // use reflection to determine the method signatures
    String r = "METHODS\n=======\n";
    Method [] methods = c.getDeclaredMethods();
    if (methods.length == 0) {
        r += "NONE\n";
    }
    else {
         
     for (int i = 0; i < methods.length; i++) {
         Method m = methods[i];
         Class[]  mParam = m.getParameterTypes();
         Class[]  mExec  = m.getExceptionTypes();
          if ((m.getModifiers() & Modifier.STATIC) != 0) {
             r += "static  ";
         }
          if ((m.getModifiers() & Modifier.PUBLIC) != 0) {
             r += "public  ";
         } else if ((m.getModifiers() & Modifier.PRIVATE) != 0) {
             r += "private  ";
         }
         else {
            r += "protected  ";
        }
          r += m.getReturnType().getName().toString() + "  " + 
//                m.getParameterTypes().toString() + " " + 
                m.getName().toString() ;
                if (mParam.length == 0) {
                    r += " (  ) ";
                }
                else {
                    r += "  (  ";
                for (int j = 0; j < mParam.length; j++) {
                    r += mParam[j] + "  ";
                }
                r += " ) ";
            }
       
            if (mExec.length == 0 ) {
                r += "\n";
            }
            else {
            
                r += " throws  ";
                for (int j = 0; j < mExec.length; j++) {
                    r += "  " + mExec[j] + "   ";
                }
                r += "\n";
            }
         }
        }
    
      r += "\n";
      return r;
    }
    
    public static String getInnerClassDescription(Class c) {
        // use Reflection to locate inner classes
        String r = "Inner Classes\n=============\n";
        Class [] n = c.getDeclaredClasses();
        if (n.length  == 0) {
            r += "NONE\n";
        }
        else {
            for (int i = 0; i < n.length; i++) {
                Class nn = n[i];
                r += nn.getName() + "\n";
            }
        }
        r += "\n";
        return r;
    }
    
    
//    public static String getSubClassDescription(Class c) {
//    	String r = "Sub Classes\n===============\n";
//    	Package p = c.getPackage();
//    	Package [] pq;
//		r += p.getName();    		
//    		
//    	return r;
//    }

    public JTree getTree () {
        return this.tree;
    }
}