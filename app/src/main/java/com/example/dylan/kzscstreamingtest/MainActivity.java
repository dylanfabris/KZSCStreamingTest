package com.example.dylan.kzscstreamingtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private Button btn;
    private TextView donateBtn;
    private TextView sendFeedback;
    private Button resetBtn;
    private Button rfaBtn;
    private Button textBtn;
    private Button callBtn;
    private Button scheduleBtn;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
    private String high = "http://188.165.192.5:8242/kzschigh?type=.mp3";
    private String low = "http://188.165.192.5:8242/kzsclow?type=.mp3";
    private String donateLink = "https://www.kzsc.org/product/donate-to-kzsc-2/";
    private String rfaLink = "http://www.radiofreeamerica.com/station/kzsc";
    private ImageView refreshTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String nowPlayingUrl = "http://spinitron.com/public/newestsong.php?station=kzsc&stylesheet=https://goo.gl/GU2hYX&num=5";

        setContentView(R.layout.activity_main);



        final WebView spinitron = (WebView) findViewById(R.id.nowPlaying);
        spinitron.setBackgroundColor(303030);
        spinitron.getSettings().setJavaScriptEnabled(true);
        spinitron.loadUrl(nowPlayingUrl);


        btn = (Button) findViewById(R.id.play);
        donateBtn = (TextView) findViewById(R.id.donate);
        sendFeedback = (TextView) findViewById(R.id.feedback);
        scheduleBtn = (Button) findViewById(R.id.schedule);
        resetBtn = (Button) findViewById(R.id.reset);
        rfaBtn = (Button) findViewById(R.id.radioFreeAmerica);
        callBtn = (Button) findViewById(R.id.call);
        textBtn = (Button) findViewById(R.id.text);
        refreshTxt = (ImageView) findViewById(R.id.refresh);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog = new ProgressDialog(this);

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.kzsc.org/schedule/"));
                startActivity(i);
            }
        });

        refreshTxt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                spinitron.reload();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:8314594036"));
                startActivity(i);
            }
        });

        textBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:8314594036");
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(i);
            }
        });

        sendFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String uriText =
                        "mailto:WebTeam@kzsc.org" +
                                "?subject=" + Uri.encode("KZSC Android App Feedback");
                Uri uri = Uri.parse(uriText);
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setData(uri);
                startActivity(i);
            }
        });

        donateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(donateLink));
                startActivity(i);
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mediaPlayer.reset();
                playPause = false;
                initialStage = true;
                btn.setText("Play");

            }
        });

        rfaBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(rfaLink));
                startActivity(i);
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if (!playPause){
                    btn.setText("Pause");
                    if(initialStage){
                        new Player().execute(high);
                    }else{
                        if(!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }

                    playPause = true;

                }else{
                    btn.setText("Play");
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }

                    playPause = false;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    class Player extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared = false;
            try {
                mediaPlayer.setDataSource(high);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        initialStage = true;
                        playPause = false;
                        btn.setText("Play");
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });

                mediaPlayer.prepare();
                prepared = true;

            } catch (Exception e){
                Log.e("KZSCStreamingTest", e.getMessage());
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(progressDialog.isShowing()){
                progressDialog.cancel();
            }

            mediaPlayer.start();
            initialStage = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Buffering...");
            progressDialog.show();
        }
    }
}
