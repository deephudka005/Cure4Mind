package com.cureya.cure4mind.relaxation.game.bouncingBall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cureya.cure4mind.R;

public class MainScreenActivity extends AppCompatActivity {

    int record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouncing_ball);
    }

    @Override
    protected void onResume() {
        super.onResume();
        leeRecord();
    }

    private void guardaRecord() {
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mieditor = datos.edit();
        mieditor.putInt("RECORD", record);
        mieditor.apply();
    }

    private void leeRecord() {
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        record = datos.getInt("RECORD", 0);
        TextView recordText = (TextView) findViewById(R.id.textRecord);
        recordText.setText(getString(R.string.highest) + record);
    }

    public void abrirAyuda(View view) {
        Intent i = new Intent(this, AyudaActivity.class);
        startActivity(i);
    }

    public void dificultad(View view) {
        String dific = (String) ((Button) view).getText();
        int dificultad = 1;
        if (dific.equals(getString(R.string.medium))) dificultad = 2;
        if (dific.equals(getString(R.string.difficult))) dificultad = 3;
        Intent i = new Intent(this,GestionActivity.class);
        i.putExtra("DIFICULTAD", dificultad);
        startActivityForResult(i, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent puntuacion) {
        super.onActivityResult(requestCode, resultCode, puntuacion);
        if(requestCode != 1 || resultCode != RESULT_OK) return;
        int resultado = puntuacion.getIntExtra("PUNTUACION", 0);
        String mensajePostPartida="";
        if (resultado > record) {
            record = resultado;
            TextView recordText = (TextView)findViewById(R.id.textRecord);
            recordText.setText(getString(R.string.highest) + record);
            guardaRecord();
            mensajePostPartida += getString(R.string.newRecord) + " ";
        }

        mensajePostPartida+= getString(R.string.endMessage) +"\n"+ getString(R.string.youscored) +" " + resultado + " " + getString(R.string.point);
        Toast mitoast = Toast.makeText(this, mensajePostPartida, Toast.LENGTH_LONG);
        mitoast.setGravity(Gravity.CENTER, 0, 0);
        mitoast.show();

    }
}
