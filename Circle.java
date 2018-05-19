import java.io.*;
    
    public class Circle implements Serializable  {
    
      // Class variable:
      protected final static double PI = 3.14159265358979323846;
      
      // Instance variable:
      protected double r;  // the radius of the circle
      
      // Constructors:
         public Circle(double r) {
       this.setRadius(r);
     }
 
     public Circle() { }
     
     // Encapsulate the radius:
     public void setRadius(double r) {
       this.r = r;
     }
 
     public double getRadius() {
         return this.r;
     }
     
    // Instance methods:
     public double circumference() {
       return 2 * PI * this.r;
     }
 
     public double area() {
       return PI * this.r * this.r;
     }
   }

