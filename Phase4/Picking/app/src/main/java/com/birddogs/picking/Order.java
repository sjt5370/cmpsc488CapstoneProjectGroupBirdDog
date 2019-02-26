package com.birddogs.picking;

import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Order extends AppCompatActivity {
    static ArrayList<String> palette;
    static ArrayList<String> pickedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        final LinearLayout lm = (LinearLayout) findViewById(R.id.layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        //get palette
        palette = DatabaseInterface.getNewPalette();

        for(int i = 0; i < palette.size(); i++){
            CheckBox c = new CheckBox(this);
            c.setText(palette.get(i));
            c.setId(i);
            c.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    clicked(v, palette);
                }
            });
            //c.setLayoutParams(params);
            lm.addView(c);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //checks that all products are scanned and enables/disables finish/hold buttons
    public void clicked(View view, ArrayList products){
        //get buttons
        Button finish = (Button) findViewById(R.id.finishPalette);
        Button hold = (Button) findViewById(R.id.holdButton);
        Button scan = (Button) findViewById(R.id.scanButton);
        boolean fin = true;

        for(int i = 0; i < products.size(); i++){
            CheckBox c = findViewById(i);
            if(!c.isChecked()){
                fin = false;
            }
        }
        //check for checked and update buttons
        if(fin){
            finish.setEnabled(true);
            hold.setEnabled(false);
            scan.setEnabled(false);
        }
        else{
            finish.setEnabled(false);
            hold.setEnabled(true);
            scan.setEnabled(true);
        }
    }

    public void openScan(View view){
        Intent intent = new Intent(this, Scan.class);
        startActivity(intent);
    }

    //shows confirmation dialog for Place on Hold and Finish Order button
    public void alert(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if(view.getId() == R.id.finishPalette){
            alert.setTitle("Finish Palette and Exit?");
            alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Order.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        else if(view.getId() == R.id.holdButton) {
            alert.setTitle("Place Palette on Hold and Exit?");
            alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseInterface.cancelPalette(pickedProducts);
                    Order.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        AlertDialog alertD = alert.create();
        alertD.show();
    }


}
