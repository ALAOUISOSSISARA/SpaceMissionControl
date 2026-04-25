package com.example.spacemissioncontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtMissionStatus;
    private ProgressBar orbitProgressBar;
    private ImageView satelliteImg;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        txtMissionStatus = findViewById(R.id.txtMissionStatus);
        orbitProgressBar = findViewById(R.id.orbitProgressBar);
        satelliteImg     = findViewById(R.id.satelliteImg);

        Button btnLoadSatellite = findViewById(R.id.btnLoadSatellite);
        Button btnOrbitalCalc   = findViewById(R.id.btnOrbitalCalc);
        Button btnPingControl   = findViewById(R.id.btnPingControl);

        // Handler for UI thread
        uiHandler = new Handler(Looper.getMainLooper());

        // Toast — always instant
        btnPingControl.setOnClickListener(v ->
                Toast.makeText(
                        getApplicationContext(),
                        "📶 Control Tower: UI is responsive!",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Thread button
        btnLoadSatellite.setOnClickListener(v -> loadSatelliteFeed());

        // AsyncTask button
        btnOrbitalCalc.setOnClickListener(v -> new OrbitalCalcTask().execute());
    }

    // -----------------------------------------
    // PART 1 : THREAD — Load Satellite Feed
    // -----------------------------------------
    private void loadSatelliteFeed() {

        orbitProgressBar.setVisibility(View.VISIBLE);
        orbitProgressBar.setProgress(0);
        satelliteImg.setVisibility(View.INVISIBLE);
        txtMissionStatus.setText("🛰️ Mission Status: Receiving satellite feed...");

        new Thread(() -> {

            // Simulate progress 0 → 100 over 2 seconds
            for (int i = 0; i <= 100; i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int progress = i;
                uiHandler.post(() -> orbitProgressBar.setProgress(progress));
            }

            // Done — update UI on main thread
            uiHandler.post(() -> {
                satelliteImg.setImageResource(R.drawable.satellite_icon);
                satelliteImg.setVisibility(View.VISIBLE);
                orbitProgressBar.setVisibility(View.INVISIBLE);
                txtMissionStatus.setText("Mission Status: Satellite feed loaded!");
            });

        }).start();
    }

    // -----------------------------------------
    // PART 2 : ASYNCTASK — Orbital Calculation
    // -----------------------------------------
    private class OrbitalCalcTask extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {
            orbitProgressBar.setVisibility(View.VISIBLE);
            orbitProgressBar.setProgress(0);
            txtMissionStatus.setText("🔭 Mission Status: Computing orbital path...");
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long orbitalData = 0;

            for (int pass = 1; pass <= 100; pass++) {
                for (int tick = 0; tick < 200000; tick++) {
                    orbitalData += (pass * tick) % 7;
                }
                publishProgress(pass);
            }

            return orbitalData;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            orbitProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            orbitProgressBar.setVisibility(View.INVISIBLE);
            txtMissionStatus.setText(
                    " Orbital calculation complete — Data: " + result
            );
        }
    }
}