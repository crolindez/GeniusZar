package es.carlosrolindez.geniuszar;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private TextToSpeech t1;

    private Button playButton;
    private Button OperationButton;
    private SeekBar difficultyBar;
    private ToggleButton numberButton[];
    private final int NUM_NUMBER_BUTTONS = 9;

    private Random r;


    private static final int SUMA = 0;
    private static final int RESTA = 1;
    private static final int MULTIPLICACION = 2;

    private static final String sumaText = " mas ";
    private static final String restaText = " menos ";
    private static final String multipText = " por ";

    private int difficulty;

    private int operation;



    @Override
    protected void onResume() {
        super.onResume();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    Locale loc = new Locale("es", "ES");
                    t1.setLanguage(loc);
                }
            }
        });
    }

    public void onPause(){
        super.onPause();
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.playButton);
        b.setText("start");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        operation = SUMA;
        difficulty = 0;

        r = new Random();



        playButton =(Button)findViewById(R.id.playButton);
        playButton.setText("start");
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");
                } else {
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("stop");
                }

            }

        });

        OperationButton =(Button)findViewById(R.id.operationButton);
        OperationButton.setText("Suma");
        OperationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                switch (operation) {
                    case SUMA:
                        operation = RESTA;
                        b.setText("Resta");
                        break;
                    case RESTA:
                        operation = MULTIPLICACION;
                        b.setText("Multip.");
                        break;
                    case MULTIPLICACION:
                        operation = SUMA;
                        b.setText("Suma");
                    default:

                }

            }

        });

        difficultyBar = (SeekBar)findViewById(R.id.seekBar);
        difficultyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                difficulty = progress;
            }
        });

        numberButton = new ToggleButton[9];
        numberButton[0] = (ToggleButton)findViewById(R.id.toggleButton1);
        numberButton[1] = (ToggleButton)findViewById(R.id.toggleButton2);
        numberButton[2] = (ToggleButton)findViewById(R.id.toggleButton3);
        numberButton[3] = (ToggleButton)findViewById(R.id.toggleButton4);
        numberButton[4] = (ToggleButton)findViewById(R.id.toggleButton5);
        numberButton[5] = (ToggleButton)findViewById(R.id.toggleButton6);
        numberButton[6] = (ToggleButton)findViewById(R.id.toggleButton7);
        numberButton[7] = (ToggleButton)findViewById(R.id.toggleButton8);
        numberButton[8] = (ToggleButton)findViewById(R.id.toggleButton9);

    }



    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int primerNumero = numberButtonsSelected();
            if (primerNumero==0) {
                timerHandler.removeCallbacks(timerRunnable);
                playButton.setText("start");
                return;
            }
            primerNumero =  r.nextInt(primerNumero) + 1;
            primerNumero = pickButtonSelected(primerNumero);
            int segundoNumero = r.nextInt(9) + 1;
            String opString;
            switch (operation) {
                case SUMA:
                    opString = sumaText;
                    break;
                case RESTA:
                    opString = restaText;
                    if (segundoNumero>primerNumero) {
                        int aux = segundoNumero;
                        segundoNumero = primerNumero;
                        primerNumero = aux;
                    }
                    break;
                case MULTIPLICACION:
                default:
                    opString = multipText;
                    break;
            }
            String toSpeak = primerNumero + opString + segundoNumero;
            Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
            String utteranceId=this.hashCode() + "";
            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            int time;
            switch (difficulty) {
                case 0:
                    time = 15000;
                    break;
                case 1:
                default:
                    time = 10000;
                    break;
                case 2:
                    time = 5000;
                    break;
                case 3:
                    time = 2500;
                    break;
            }

            timerHandler.postDelayed(this, time);
        }
    };


    private int numberButtonsSelected() {
        int cont = 0;
        for (int i=0; i<NUM_NUMBER_BUTTONS; ++i) {
            if (numberButton[i].isChecked()) {
                ++cont;
            }
        }
        return cont;
    }

    private int pickButtonSelected(int pointed) {
        int cont = 0;
        for (int i=0; i<NUM_NUMBER_BUTTONS; ++i) {
            if (numberButton[i].isChecked()) ++cont;
            if (pointed==cont) {
                return i+1;
            }
        }
        return 0;
    }

}
