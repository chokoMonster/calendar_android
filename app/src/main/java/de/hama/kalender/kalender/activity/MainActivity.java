package de.hama.kalender.kalender.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.hama.kalender.kalender.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnKalender;
    private ImageButton btnMountain;
    private Button btnMountainStatistics;
    private ImageButton btnSnooze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnKalender = findViewById(R.id.btnKalender);
        btnKalender.setOnClickListener(this);

        btnMountain = findViewById(R.id.btnMountain);
        btnMountain.setOnClickListener(this);

        btnMountainStatistics = findViewById(R.id.btnMountainStatistics);
        btnMountainStatistics.setOnClickListener(this);

        btnSnooze = findViewById(R.id.btnSnooze);
        btnSnooze.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPlayer:
                PlayerDialog customDialog = new PlayerDialog();
                customDialog.show(getSupportFragmentManager(), "PLAYER");
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view==btnKalender) {
            startActivity(new Intent(MainActivity.this, KalenderActivity.class));
        } else if(view==btnMountain) {
            MountainDialog customDialog = new MountainDialog();
            customDialog.show(getSupportFragmentManager(), "MOUNTAIN");
        } else if(view==btnMountainStatistics) {
            startActivity(new Intent(MainActivity.this, MountainStatisticsActivity.class));
        } else if(view==btnSnooze) {
            SnoozeDialog customDialog = new SnoozeDialog();
            customDialog.show(getSupportFragmentManager(), "SNOOZE");
        }
    }

    public static class MountainDialog extends DialogFragment {
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
                            MountainDialog.this.getDialog().cancel();
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


    public static class SnoozeDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences shared_snooze = getContext().getSharedPreferences("SNOOZE", 0);
            final SharedPreferences shared_players = getContext().getSharedPreferences("PLAYER", 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_snooze, null);

            TextView lblS1 = view.findViewById(R.id.lblS1);
            TextView lblS2 = view.findViewById(R.id.lblS2);
            final EditText txtS1 = view.findViewById(R.id.txtS1);
            final EditText txtS2 = view.findViewById(R.id.txtS2);
            Button btnPlusS1 = view.findViewById(R.id.btnPlusS1);
            Button btnMinusS1 = view.findViewById(R.id.btnMinusS1);
            Button btnPlusS2 = view.findViewById(R.id.btnPlusS2);
            Button btnMinusS2 = view.findViewById(R.id.btnMinusS2);

            builder.setView(view)
                    .setTitle("Snooze")
                    .setIcon(R.drawable.clock1)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = shared_snooze.edit();
                            editor.putString("PLAYER1", txtS1.getText().toString());
                            editor.putString("PLAYER2", txtS2.getText().toString());
                            editor.apply();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SnoozeDialog.this.getDialog().cancel();
                        }
                    });

            lblS1.setText(shared_players.getString("PLAYER1", "0"));
            lblS2.setText(shared_players.getString("PLAYER2", "0"));
            txtS1.setText(shared_snooze.getString("PLAYER1", "0"));
            txtS2.setText(shared_snooze.getString("PLAYER2", "0"));
            txtS1.setEnabled(false);
            txtS2.setEnabled(false);

            btnPlusS1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int value = Integer.parseInt(txtS1.getText().toString())+1;
                    txtS1.setText(Integer.toString(value));
                }
            });
            btnMinusS1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!txtS1.getText().toString().equals("0")) {
                        int value = Integer.parseInt(txtS1.getText().toString())-1;
                        txtS1.setText(Integer.toString(value));
                    }
                }
            });
            btnPlusS2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int value = Integer.parseInt(txtS2.getText().toString())+1;
                    txtS2.setText(Integer.toString(value));
                }
            });
            btnMinusS2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!txtS2.getText().toString().equals("0")) {
                        int value = Integer.parseInt(txtS2.getText().toString())-1;
                        txtS2.setText(Integer.toString(value));
                    }
                }
            });
            return builder.create();
        }
    }


    public static class PlayerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final SharedPreferences shared = getContext().getSharedPreferences("PLAYER", 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_player, null);

            final EditText txtPlayer1 = view.findViewById(R.id.txtPlayer1);
            final EditText txtPlayer2 = view.findViewById(R.id.txtPlayer2);

            builder.setView(view)
                    .setTitle("Players")
                    .setIcon(R.drawable.players1)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("PLAYER1", txtPlayer1.getText().toString());
                            editor.putString("PLAYER2", txtPlayer2.getText().toString());
                            editor.apply();//.commit()?
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PlayerDialog.this.getDialog().cancel();
                        }
                    });
            txtPlayer1.setText(shared.getString("PLAYER1", ""));
            txtPlayer2.setText(shared.getString("PLAYER2", ""));
            return builder.create();
        }
    }
}