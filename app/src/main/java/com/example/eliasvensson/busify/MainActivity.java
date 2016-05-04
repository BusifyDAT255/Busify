package com.example.eliasvensson.busify;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fromDateButton = (Button) findViewById(R.id.from_date_button);
        Button toDateButton = (Button) findViewById(R.id.to_date_button);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (v == findViewById(R.id.from_date_button)){
                    DateDialog dialog = new DateDialog(findViewById(R.id.txt_from_date));
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }

                if (v == findViewById(R.id.to_date_button)){
                    DateDialog dialog = new DateDialog(findViewById(R.id.txt_to_date));
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        };

        fromDateButton.setOnClickListener(listener);
        toDateButton.setOnClickListener(listener);
    }
}