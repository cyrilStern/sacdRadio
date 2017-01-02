package com.example.root.myapplication;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText urlrestapi;
    static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_OUT_STEREO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final String ip = "31.12.64.211";
    static final int port = 8000;
    private MediaPlayer mediaPlayer;
    private Thread th;
    private JSONArray getRadio;
    private LinearLayout rel;
    private JSONObject row;
    private String url2;
    boolean isPlaying;
    int playBufSize;
    protected Socket socket;
    protected AudioTrack audioTrack;
    protected Handler handler;
    private Handler handler2;
    private ProgressBar pb;
    private TextView tv;
    private  boolean isNotFinish;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(100);
        handler2 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                float compteur = msg.getData().getFloat("compteur");
                pb.setProgress(((int) compteur));
            }
        };
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String mesgget = (String) msg.getData().get("collection");
                try {
                    getRadio = new JSONArray(mesgget);// sb.toString());

                    for (int i = 0; i < getRadio.length(); i++) {

                        row = getRadio.getJSONObject(i);

                        Button button  = new Button(getApplicationContext());
                        button.setId(10220 + i);

                        button.setText(row.getString("radioStationName"));


                        final Uri url2 = Uri.parse(row.getString("radioUrl"));

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if((th !=null) || mediaPlayer.isPlaying()){
                                    mediaPlayer.stop();
                                    mediaPlayer.reset();
                                    tv.setText("");
                                    th.interrupt();
                                    th = null;

                                }else {

                                    final float i;
                                    i =0;
                                    isNotFinish = true;


                                        new Thread(new Runnable() {
                                            float j = i;

                                            @Override
                                            public void run() {
                                                while (isNotFinish) {
                                                    try {
                                                        Thread.sleep(50);
                                                        Bundle bd = new Bundle();
                                                        Message msPB;
                                                        bd.putFloat("compteur", j);
                                                        msPB = handler2.obtainMessage();
                                                        msPB.setData(bd);
                                                        handler2.sendMessage(msPB);
                                                        j = j+0.2f;
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                                Bundle bd = new Bundle();
                                                Message msPB;
                                                bd.putFloat("compteur", 100);
                                                msPB = handler2.obtainMessage();
                                                msPB.setData(bd);
                                                handler2.sendMessage(msPB);
                                            }
                                        }).start();

                                    tv.setText(url2.toString());
                                    th = new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            try {
                                                mediaPlayer.setDataSource(getApplicationContext(),url2);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            //mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("http://uk5.internet-radio.com:8278/live"));
                                            try {

                                                mediaPlayer.prepare();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            mediaPlayer.start();
                                            while(!mediaPlayer.isPlaying()) isNotFinish = true;
                                            isNotFinish =false;


                                        }
                                    });
                                    th.start();
                                }
                            }
                        });
                        rel.addView(button);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
        init(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    private void init(final Context context){


        rel = (LinearLayout) findViewById(R.id.alignbuuton);
        playBufSize=AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
        mediaPlayer = new MediaPlayer();
//        Button button1 = new Button(this);
//        rel.addView(button1);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(th !=null){
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                    th.interrupt();
//                    th = null;
//                }else {
//                    th = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String url = "http://uk5.internet-radio.com:8278/live";
//                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            try {
//                                mediaPlayer.setDataSource(url);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            //mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("http://uk5.internet-radio.com:8278/live"));
//                            try {
//                                mediaPlayer.prepare();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            mediaPlayer.start();
//
//                        }
//                    });
//                    th.start();
//                }

//                    socket = new Socket("uk5.internet-radio.com/live",8278);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                audioTrack.play();
//
//                isPlaying = true;
//                while (isPlaying) {
//                    int readSize = 0;
//                    try { readSize = socket.getInputStream().read(buffer); }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    short[] sbuffer = new short[1024];
//                    for(int i = 0; i < buffer.length; i++)
//                    {
//
//                        int asInt = 0;
//                        asInt = ((buffer[i] & 0xFF) << 0)
//                                | ((buffer[i+1] & 0xFF) << 8)
//                                | ((buffer[i+2] & 0xFF) << 16)
//                                | ((buffer[i+3] & 0xFF) << 24);
//                        float asFloat = 0;
//                        asFloat = Float.intBitsToFloat(asInt);
//                        int k=0;
//                        try{k = i/4;}catch(Exception e){}
//                        sbuffer[k] = (short)(asFloat * Short.MAX_VALUE);
//                        //Log.i("buffer: ", (String.valueOf(sbuffer[k])));
//                        i=i+3;
//                    }
//                    audioTrack.write(sbuffer, 0, sbuffer.length);
//                }
//                //audioTrack.stop();
//               // try { socket.close(); }
//                //catch (Exception e) { e.printStackTrace(); }
//               }else {
//                   audioTrack.stop();
//                   try {//                       socket.close();
//                   } catch (IOException e) {
//                       e.printStackTrace();
//                   }




        new Thread(new Runnable() {
            public void run() {
                URL url;
                Message myMessage;
                myMessage = handler.obtainMessage();
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("http://89.92.177.105:3000/radio");

                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader isw = new InputStreamReader(in);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) // Read line by line
                        sb.append(line + "\n");
                    Bundle b = new Bundle();
                    b.putString("collection", sb.toString());
                    // envoyer le message au Hanlder
                    myMessage.setData(b);
                    handler.sendMessage(myMessage);
                    //Object obj = parser.parse(;





                } catch (Exception e) {
                    Log.e("merde",e.toString());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();


    }

}
