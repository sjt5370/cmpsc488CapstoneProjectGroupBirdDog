package tester;
import java.util.Scanner;
/**
 *
 * @author Nicholas
 */
public class Tester {

    public static void main(String[] args) {
        
        int n = 10000;
        int zero = 0;
        int orderNum = 10000;
        for(int i = 0; i < 1000; i++)
        {
            n = n + 1;
            System.out.print("Insert into product values (" + n + ", 'product" + n + 
                    "', 'this is product1', 'manufacture" + n + "', " + n + ", " + 1 + ");");
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
                        ", 'abc1234atpsu', 'customer account " + n + "', '" + n +  " tester ave', 'Tester Town', 'TS', 12345);");                    
                System.out.println("");
                System.out.println("Insert into coordinates values (" + n + ", " + (n % 90) + ", " + (n % 180) + ");");
                System.out.println("");
                
                for(int j = 0; j < 3; j++){
                    orderNum = orderNum + 1;
                    System.out.print("Insert into order_info values (" + orderNum + 
                        ", " + n + ", " + n + ", " + (j%2) + ");");                    
                    System.out.println("");
                    
                 /*   System.out.print("Insert into order_history values (" + n + ", " +
                            orderNum + ", " + n + ");");                    
                    System.out.println("");*/
                }
            }
        }
    }
    
}
