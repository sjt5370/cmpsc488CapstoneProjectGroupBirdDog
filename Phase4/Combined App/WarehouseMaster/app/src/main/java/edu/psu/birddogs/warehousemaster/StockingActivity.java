package edu.psu.birddogs.warehousemaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StockingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocking);
        this.setTitle(getResources().getString(R.string.stocking_bar));
        ((Button) findViewById(R.id.restockingButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockingActivity.this, RestockingActivity.class);
                startActivity(intent);
            }
        });
        ((ImageView) findViewById(R.id.restockingImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockingActivity.this, RestockingActivity.class);
                startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.receivingButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockingActivity.this, ReceivingActivity.class);
                startActivity(intent);
            }
        });
        ((ImageView) findViewById(R.id.receivingImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StockingActivity.this, ReceivingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutButton) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.logout_button));
            alert.setMessage(getResources().getString(R.string.logout_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(StockingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertD = alert.create();
            alertD.show();
            return true;
        } else return super.onOptionsItemSelected(item);
    }
}
