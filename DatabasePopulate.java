
package tester;
import java.util.Scanner;
/**
 *
 * @author Nicholas
 */
public class Tester {

    public static void main(String[] args) {
        
        int n = 0;
        int zero = 0;
        int orderNum = 0;
        for(int i = 0; i < 1000; i++)
        {
            n = n + 1;
            System.out.print("Insert into product values (" + n + ", 'product" + n + 
                    "', 'this is product1', 'manufacture" + n + "');");
            System.out.println("");
            
            System.out.print("Insert into inventory values (" + n + ", " + n + ", " +
                    n + ", " + zero + ");");                    
            System.out.println("");
            
            zero = (zero + n) % 2;
            System.out.print("Insert into master_account values (" + n + ", " + zero + 
                    ", 'User" + n + "', 'password" + n + "');");                    
            System.out.println("");
            
            if(zero == 0){
                System.out.print("Insert into employee_account values (" + n + 
                        ", 'Tester" + n + "', 'employee', 'temp', " + n + ");");                    
                System.out.println("");
            }
            
            if(zero == 1){
                System.out.print("Insert into customer_account values (" + n + 
                        ", '" + n + " tester ave', 'abc1234atpsu');");                    
                System.out.println("");
                
                for(int j = 0; j < 3; j++){
                    orderNum = orderNum + 1;
                    System.out.print("Insert into order_info values (" + orderNum + 
                        ", '" + n + ", '" + n + (j%2) + ");");                    
                    System.out.println("");
                    
                    System.out.print("Insert into order_info values (" + n + ", '" +
                            orderNum + ", '" + n + ", '" + n + (j%2) + ");");                    
                    System.out.println("");
                }
            }
        }
    }
    
}
