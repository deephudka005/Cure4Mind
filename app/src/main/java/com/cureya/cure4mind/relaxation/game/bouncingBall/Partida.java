package com.cureya.cure4mind.relaxation.game.bouncingBall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.cureya.cure4mind.R;

public class Partida extends androidx.appcompat.widget.AppCompatImageView {

    private int acel;
    private Bitmap pelota, fondo;
    private int tam_pantX, tam_pantY, posX, posY, velX, velY;
    private int tamPelota;
    boolean pelota_sube;
    private TextView textView;
    private int toques;
    private String toquesText;
    private Paint pincel;

    public Partida(Context contexto, int nivel_dificultad){

        super(contexto);

        WindowManager manejador_ventana=(WindowManager) contexto.getSystemService(Context.WINDOW_SERVICE);

        Display pantalla=manejador_ventana.getDefaultDisplay();

        Point maneja_coord=new Point(); //Integra dos coordenadas

        pantalla.getSize(maneja_coord); //Determina el tamaño de la pantalla

        tam_pantX=maneja_coord.x;

        tam_pantY=maneja_coord.y;

        // Construir un layout programatico con el fondo
        BitmapDrawable dibujo_fondo=(BitmapDrawable) ContextCompat.getDrawable(contexto, R.drawable.paisaje_1);

        fondo=dibujo_fondo.getBitmap();// mirar en api getBitmap en clase BitmapDrawable. esto nos lleva a la siguiente instr.

        fondo=Bitmap.createScaledBitmap(fondo, tam_pantX, tam_pantY, false);//mirar en clase Bitmap


        BitmapDrawable objetoPelota=(BitmapDrawable)ContextCompat.getDrawable(contexto, R.drawable.pelota_1); //dibujo ahora la pelota del mismo modo que el fondo

        pelota=objetoPelota.getBitmap();

        tamPelota=tam_pantY/3; //tamaño de la pelota

        pelota=Bitmap.createScaledBitmap(pelota, tamPelota, tamPelota, false);

        posX=tam_pantX/2-tamPelota/2;  //posicion de partir de la pelota

        posY=0-tamPelota;

        acel=nivel_dificultad*(maneja_coord.y/400);  //velocidad de la pelota


        //PRUEBA TEXTO
        toques = 0;
        toquesText = "0";
        pincel = new Paint();
        pincel.setTextSize(100);
        pincel.setColor(Color.WHITE);
        pincel.setStrokeWidth(600);
    }

    public void sumaToque(int dificultad) {
        toques+=dificultad;
        toquesText = ""+toques;
    }

    public boolean toque(int x, int y){

        if(y<tam_pantY/3) return false; //Prohibimos que pueda tocar la pelota muy arriba primer tercio de la pantalla

        if(velY<=0) return false;  //Si la pelota esta parada pues no detecto pulsacion

        if(x<posX || x> posX+tamPelota) return false;  //si pica fuera de la pelota no hace nada

        if(y<posY || y>posY+tamPelota) return false;

        velY=-velY; //Aplicamos velocidad en sentido opuesto

        double desplX=x-(posX+tamPelota/2);  //Aplicamos velocidad de desplazamiento en funcion de si toca en el centro o que

        desplX=desplX/(tamPelota/2)*velY/2;

        velX+=(int)desplX;

        return true;
    }

    //matematicas del movimiento de la pelota. Si devuelve true es que la partida se termina porque sa salio o algo
    public boolean movimientoBola(){

        if(posX<0-tamPelota){

            posY=0-tamPelota;

            velY=acel;
        }

        posX+=velX;

        posY+=velY;

        if(posY>=tam_pantY) return true; //Pelota se sale por arriba de la pantalla

        if(posX+tamPelota<0 || posX>tam_pantX) return true; //pelota se sale por los lados

        if(velY<0) pelota_sube=true;

        if(velY>0 && pelota_sube){

            pelota_sube=false;

        }

        velY+=acel;

        return false;
    }

    //Dibuja en la pantalla lo creado anteriormente
    protected void onDraw(Canvas lienzo){

        lienzo.drawBitmap(fondo, 0,0, null);

        lienzo.drawBitmap(pelota, posX, posY, null);

        //lienzo.drawBitmap(textView.getDrawingCache(), 50, 200, null);
        lienzo.drawText(toquesText, 50,200, pincel);
    }
}
