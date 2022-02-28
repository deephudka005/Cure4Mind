package com.cureya.cure4mind.relaxation.game.bouncingBall;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.cureya.cure4mind.R;

public class GestionActivity extends Activity {

    private Partida partida;
    private int dificultad;
    private int FPS = 30;
    private Handler temporizador; //Manejador del hilo de la animacion (Cada cuanto pinto un nuevo frame)
    private int toques;
    private MediaPlayer boteBalon;
    private MediaPlayer finalJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toques = 0;
        Bundle extras = getIntent().getExtras();
        dificultad = extras.getInt("DIFICULTAD");
        partida = new Partida(getApplicationContext(), dificultad);
        setContentView(partida);
        temporizador = new Handler();
        temporizador.postDelayed(elhilo,2000);
        boteBalon = MediaPlayer.create(this, R.raw.golpeobalon);
        finalJuego = MediaPlayer.create(this, R.raw.finaljuego);
    }

    // Hilo que llama al metodo que se encarga del movimiento de la bola
    private Runnable elhilo = new Runnable() {
        @Override
        public void run() {
            if (partida.movimientoBola()) {
                fin();
            } else {
                partida.invalidate(); //Elimina el contenido de ImageView y llama de nuevo on Draw (borra el frame que estas viendo y te crea el otro)
                temporizador.postDelayed(elhilo,1000/FPS); //mete un delay entre una ejecucion del hilo y otra creo
            }
        }
    };

    private void fin() {
        temporizador.removeCallbacks(elhilo); //mata el hilo
        finalJuego.start();
        Intent i = new Intent();
        i.putExtra("PUNTUACION", toques);
        setResult(RESULT_OK,i);
        finish(); //destruye la actividad actual
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (partida.toque(x, y)) {
            boteBalon.start();
            toques+=dificultad;
            partida.sumaToque(dificultad);
        }
        return false;
    }
}
