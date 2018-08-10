package de.hama.kalender.kalender.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import de.hama.kalender.kalender.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnKalender;
    private ImageButton btnMountain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnKalender = findViewById(R.id.btnKalender);
        btnKalender.setOnClickListener(this);

        btnMountain = findViewById(R.id.btnMountain);
        btnMountain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==btnKalender) {
            startActivity(new Intent(MainActivity.this, KalenderActivity.class));
        } else if(view==btnMountain) {
            CustomDialog customDialog = new CustomDialog();
            customDialog.show(getSupportFragmentManager(), "MOUNTAIN");
        }
    }

    public static class CustomDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences shared = getContext().getSharedPreferences("MOUNTAIN", 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_mountain, null);

            final EditText txtDate = view.findViewById(R.id.txtDate);
            final EditText txtCount = view.findViewById(R.id.txtCount);
            Button btnPlus = view.findViewById(R.id.btnPlus);
            Button btnMinus = view.findViewById(R.id.btnMinus);

            builder.setView(view)
                    .setTitle("Marienhöhe")
                    .setIcon(R.drawable.berg2)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("DATE", txtDate.getText().toString());
                            editor.putString("COUNT", txtCount.getText().toString());
                            editor.apply();//.commit()?
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CustomDialog.this.getDialog().cancel();
                        }
                    });

            txtDate.setText(shared.getString("DATE", ""));
            txtCount.setText(shared.getString("COUNT", "0"));
            txtCount.setEnabled(false);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int value = Integer.parseInt(txtCount.getText().toString())+1;
                    txtCount.setText(Integer.toString(value));
                }
            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!txtCount.getText().toString().equals("0")) {
                        int value = Integer.parseInt(txtCount.getText().toString())-1;
                        txtCount.setText(Integer.toString(value));
                    }
                }
            });
            return builder.create();
        }
    }
}