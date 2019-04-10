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

        File adj = new File("C:\\Users\\jokin\\Desktop\\adjectives.txt");
        File noun = new File("C:\\Users\\jokin\\Desktop\\nouns.txt");
        File type = new File("C:\\Users\\jokin\\Desktop\\types.txt");
        File flav = new File("C:\\Users\\jokin\\Desktop\\flavors.txt");
        File co = new File("C:\\Users\\jokin\\Desktop\\co.txt");

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

        for(int i = 999; i > 0; i--){
            int a = (int)(Math.random() * (i + 1));
            int n = (int)(Math.random() * (i + 1));
            int typ = (int)(Math.random() * 13);
            int x = (int)(Math.random() * 16);
            int y = (int)(Math.random() * 16);
            int z = (int)(Math.random() * 16);
            int one = (int)(Math.random() * 11);
            int c = (int)(Math.random() * 70);
            String ad = adjectives.get(a);
            String no = nouns.get(n);
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
            float volume = (float)(Math.random() * 100);

            System.out.println("Insert into product values (" + (1000 + i) + ", '" + ad + " " + no + "', 'This is a " + ty +
                ". There are hints of " + xx + ", " + yy + ", and " + zz + " flavors. It is available in " + two + ".', '" +
                company + " Co.', " + price + ", " + priority + ", " + volume + ");");

        }
    }
}
