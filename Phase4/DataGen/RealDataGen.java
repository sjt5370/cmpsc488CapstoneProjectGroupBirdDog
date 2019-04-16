import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by john f on 4/7/2019.
 */
public class RealDataGen {
    public static void main(String[] args) throws IOException {
        ArrayList<String> adjectives = new ArrayList();
        ArrayList<String> nouns = new ArrayList();
        ArrayList<String> types = new ArrayList();
        ArrayList<String> flavors = new ArrayList();
        ArrayList<String> com = new ArrayList();
        ArrayList<String> sizes = new ArrayList();
        ArrayList<String> firstName = new ArrayList();
        ArrayList<String> lastName = new ArrayList();
        ArrayList<String> cusName = new ArrayList();
        ArrayList<String> street = new ArrayList();
        ArrayList<String> city = new ArrayList();
        ArrayList<String> state = new ArrayList();
        ArrayList<Integer> zip = new ArrayList();

        sizes.add("8.4 oz. cans");
        sizes.add("12 oz. cans");
        sizes.add("16 oz. cans");
        sizes.add("19.2 oz. cans");
        sizes.add("12 oz. bottles");
        sizes.add("22 oz. bottles");
        sizes.add("750 mL bottles");
        sizes.add("64 oz. growlers");
        sizes.add("half barrel kegs");
        sizes.add("quarter barrel kegs");
        sizes.add("sixth barrel kegs");

        File adj = new File(".\\assets\\adjectives.txt");
        File noun = new File(".\\assets\\nouns.txt");
        File type = new File(".\\assets\\types.txt");
        File flav = new File(".\\assets\\flavors.txt");
        File co = new File(".\\assets\\co.txt");
        File fN = new File(".\\assets\\firstName.txt");
        File lN = new File(".\\assets\\lastName.txt");
        File name = new File(".\\assets\\name.txt");
        File address = new File(".\\assets\\address.txt");

        String st;
        String t = "";
        BufferedReader br = new BufferedReader(new FileReader(adj));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            adjectives.add(t);
        }
        br = new BufferedReader(new FileReader(noun));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            nouns.add(t);
        }
        br = new BufferedReader(new FileReader(type));
        while ((st = br.readLine()) != null) {
            st.toLowerCase();
            types.add(st);
        }
        br = new BufferedReader(new FileReader(flav));
        while ((st = br.readLine()) != null) {
            st.toLowerCase();
            flavors.add(st);
        }
        br = new BufferedReader(new FileReader(co));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            com.add(t);
        }
        br = new BufferedReader(new FileReader(fN));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            firstName.add(t);
        }
        br = new BufferedReader(new FileReader(lN));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            lastName.add(t);
        }
        br = new BufferedReader(new FileReader(name));
        while ((st = br.readLine()) != null) {
            t = st.substring(0, 1).toUpperCase() + st.substring(1);
            cusName.add(t);
        }
        br = new BufferedReader(new FileReader(address));
        while ((st = br.readLine()) != null) {
            street.add(st);
            st = br.readLine();
            city.add(st);
            st = br.readLine();
            state.add(st);
            st = br.readLine();
            zip.add(Integer.parseInt(st));
        }
        //
        //delete previous data
        //
        //
        System.out.println("delete from coordinates;");
        System.out.println("delete from pallet;");
        System.out.println("delete from route_info;");
        System.out.println("delete from order_item;");
        System.out.println("delete from order_full;");
        System.out.println("delete from customer_account;");
        System.out.println("delete from employee_account;");
        System.out.println("delete from master_account;");
        System.out.println("delete from inventory;");
        System.out.println("delete from product;");
        //
        //products
        //
        //
        for(int i = 999; i >= 0; i--){
            int a = (int)(Math.random() * (i + 1));
            int n = (int)(Math.random() * (i + 1));
            int typ = (int)(Math.random() * 13);
            int x = (int)(Math.random() * 16);
            int y = (int)(Math.random() * 16);
            int z = (int)(Math.random() * 16);
            int one = (int)(Math.random() * 11);
            int c = (int)(Math.random() * 70);
            String ad = adjectives.remove(a);
            String no = nouns.remove(n);
            String ty = types.get(typ);
            String xx = flavors.get(x);
            String yy = flavors.get(y);
            String zz = flavors.get(z);
            String two = sizes.get(one);
            String company = com.get(c);

            double d = Math.random() * 100;
            DecimalFormat p = new DecimalFormat("###.##");
            float price = Float.valueOf(p.format(d));
            int priority = (int)(Math.random() * 10);
            float volume = (float)(Math.random() * 10000);

            System.out.println("Insert into product values (" + (1000 + i) + ", '" + ad + " " + no + "', 'This is a " + ty +
                ". There are hints of " + xx + ", " + yy + ", and " + zz + " flavors. It is available in " + two + ".', '" +
                company + " Co.', " + price + ", " + priority + ", " + volume + ");");

        }
        //
        //inventory
        //
        //
        for(int i = 0; i <= 999; i++){
            int bulk = (int)(Math.random() * 2001);
            int shelf = (int)(Math.random() * 51);
            int flag = 0;
            if(shelf == 0){
                flag = 1;
            }
            System.out.println("Insert into inventory values (" + (1000 + i) + ", " + bulk + ", " + shelf + ", " + flag + ");");

            int add = (int)(Math.random() * 101);
            if(add < 10){
                i++;
            }
        }
        //
        //masterAccount
        //
        //
        for(int i = 1; i <= 100; i++){
            System.out.println("Insert into master_account values (" + (10000 + i) + ", " + 0 + ", " + "'username" + (10000 + i) + "', " + "'password'" + ");");
            System.out.println("Insert into master_account values (" + (20000 + i) + ", " + 1 + ", " + "'username" + (20000 + i) + "', " + "'password'" + ");");
        }
        //
        //employeeAccount
        //
        //
        for(int i = 100; i > 0; i--){
            int f = (int)(Math.random() * (i));
            int l = (int)(Math.random() * (i));
            int prod = (int)(Math.random() * 101);
            String fName = firstName.remove(f);
            String lName = lastName.remove(l);

            int j = (int)(Math.random() * 101);
            String job = "";
            if(j > 80){
                job = "supervisor";
            }
            else if(j > 40){
                job = "picker";
            }
            else{
                job = "stocker";
            }

            System.out.println("Insert into employee_account values (" + (10000 + i) + ", '" + fName + "', '" + lName + "', '" + job + "', " + prod + ");");
        }
        //
        //customerAccount
        //
        //
        for(int i = 100; i > 0; i--){
            int num = (int)(Math.random() * (i));
            int add = (int)(Math.random() * (i));
            String n = cusName.remove(num);
            String email = n + "@gmail.com";
            String str = street.remove(add);
            String cit = city.remove(add);
            String sta = state.remove(add);
            Integer zi = zip.remove(add);

            System.out.println("Insert into customer_account values (" + (20000 + i) + ", '" + email + "', '" + n + "', '" + str + "', '" + cit + "', '" + sta + "', " + zi + ");");
        }
        //
        //order
        //
        //
        for(int i = 0; i < 50; i++){
            int id = (int)(Math.random() * 100) + 20001;
            int urg = (int)(Math.random() * 10);

            System.out.println("Insert into order_full values (" + (101 + i) + ", " + id + ", 0, " + urg + ", 1);");

            int p = (int)((Math.random() * 50) + 1);
            for(int j = 0; j < p; j++){
                int pid = (int)((Math.random() * 1000) + 1000);
                int quan = (int)(Math.random() * 25 + 1);

                System.out.println("Insert into order_item values (" + (101 + i) + ", " + pid + ", " + quan + ");");
            }
        }
    }
}
