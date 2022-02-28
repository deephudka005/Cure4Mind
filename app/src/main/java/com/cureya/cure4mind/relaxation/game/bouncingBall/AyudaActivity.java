package com.cureya.cure4mind.relaxation.game.bouncingBall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cureya.cure4mind.R;

public class AyudaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouncing_ball_ayuda);
    }

    public void volverJuego(View view) {
        onBackPressed();
    }
}
