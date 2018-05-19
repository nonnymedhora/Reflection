public class CircleTest {
    
      public static void main(String[] args) {
      
        // Variables:
        double r1 = 1, r2 = 1/(2*Math.PI);  // radii
        double circumference, area, radius;
        
        // Two circles:
       Circle circle1, circle2;
   
      // Test 1:
       circle1 = new Circle(r1);
       circumference = circle1.circumference();
       area = circle1.area();
       radius = circle1.getRadius();
       System.out.print("Circle 1:  radius = " + radius + "\n");
       System.out.print("Circumference = " + circumference + "\n");
       System.out.println("Area = " + area + "\n" + "\n");
       
       // Test 2:
       circle2 = new Circle(); circle2.setRadius(r2);
       circumference = circle2.circumference();
       area = circle2.area();
       radius = circle2.getRadius();
       System.out.print("Circle 2:  radius = " + radius + "\n");
       System.out.print("Circumference = " + circumference + "\n");
       System.out.println("Area = " + area + "\n" + "\n");
       
     }
    
}
