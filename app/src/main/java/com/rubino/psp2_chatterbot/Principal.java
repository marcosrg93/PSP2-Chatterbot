package com.rubino.psp2_chatterbot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rubino.psp2_chatterbot.chatterbot.ChatterBot;
import com.rubino.psp2_chatterbot.chatterbot.ChatterBotFactory;
import com.rubino.psp2_chatterbot.chatterbot.ChatterBotSession;
import com.rubino.psp2_chatterbot.chatterbot.ChatterBotType;

import java.util.ArrayList;
import java.util.Locale;


public class Principal extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private FloatingActionButton fab;
    private boolean hablando = false;
    private static final int CTE = 1, CTE2 = 2;
    private TextToSpeech tts;
    String frase = "";
    ChatterBotFactory factory;
    ChatterBot bot1;
    ChatterBotSession bot1session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hablar(view);
            }
        });

        Intent i = new Intent();
        i.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(i, CTE);

        factory = new ChatterBotFactory();
        try {
            bot1 = factory.create(ChatterBotType.CLEVERBOT);

        } catch (Exception e) {
            e.printStackTrace();
            Log.v("LOGINERROR", e.toString() + "");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CTE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent intent = new Intent();
                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(intent);
            }
        } else if (requestCode == CTE2) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textos = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                frase = textos.get(0);
                fab.setClickable(false);
                addView2(getCurrentFocus(), frase);
                BotSpeak s = new BotSpeak(frase);
                s.execute();
            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            hablando = true;
            tts.setLanguage(new Locale("es", "ES"));
        } else {
            Log.v("TTSERROR: ", "No se puede reproducir");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }


    public void Hablar(View v) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hablar ahora");
        i.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        startActivityForResult(i, CTE2);
    }


    class BotSpeak extends AsyncTask<Object, Integer, String> {


        BotSpeak(String... p) {
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object[] params) {
            String respuesta = "";
            try {
                bot1session = bot1.createSession();
                respuesta = bot1session.think((frase));

            } catch (Exception ex) {

            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (hablando) {
                tts.speak(s, TextToSpeech.QUEUE_ADD, null);
                addView(getCurrentFocus(), s);
                fab.setClickable(true);
            } else {
            }
            frase = "";
        }
    }


    public void addView(View v, String mensaje) {
        LinearLayout svl;
        svl = (LinearLayout) findViewById(R.id.addll);
        TextView view2 = new TextView(this);
        view2.setText(mensaje);
        view2.setBackgroundResource(R.drawable.bg_msg_from);
        view2.setPadding(10, 20, 10, 20);
        view2.setGravity(Gravity.END);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        svl.addView(view2, params);
    }

    public void addView2(View v, String mensaje) {
        LinearLayout svl;
        svl = (LinearLayout) findViewById(R.id.addll);
        TextView view = new TextView(this);
        view.setText(mensaje);
        view.setPadding(5, 20, 5, 20);
        view.setBackgroundResource(R.drawable.bg_msg_from2);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        svl.addView(view, params);
    }
}
