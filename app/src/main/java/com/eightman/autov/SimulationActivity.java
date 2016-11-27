package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Simulation.SimTime;
import com.eightman.autov.Simulation.SimulationView;
import com.eightman.autov.Utils.XY;

public class SimulationActivity extends AppCompatActivity implements View.OnClickListener, StatsInterface {
    SimulationView simulationView;
    Button btnAddCar, btnAdd5Cars, btnRunSimulation;
    TextView tvNumOfCars, tvTime, tvFPS, tvTotalCollisions;
    SimulationThread simulationThread = null;
    boolean updateTotalCollision = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView) findViewById(R.id.surfaceSimulation);

        tvNumOfCars = (TextView) findViewById(R.id.tvNumOfCars);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvFPS = (TextView) findViewById(R.id.tvFPS);
        tvTotalCollisions = (TextView) findViewById(R.id.tvTotalCollisions);
        btnAddCar = (Button) findViewById(R.id.btnAddCar);
        btnAdd5Cars = (Button) findViewById(R.id.btnAdd5Cars);
        btnRunSimulation = (Button) findViewById(R.id.btnRunSimulation);

        btnAddCar.setOnClickListener(this);
        btnAdd5Cars.setOnClickListener(this);
        btnRunSimulation.setOnClickListener(this);

        simulationView.registerStats(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simulationView.clearWorld();
    }

    private void addACar(XY xy) {
        try {
            simulationView.addRandomCar(xy);
        } catch (final Exception e) {
            SimulationActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SimulationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void add5Cars() {
        addACar(new XY(0, 0));
        addACar(new XY(SimConfig.EDGE_METERS, SimConfig.EDGE_METERS));
        addACar(new XY(SimConfig.EDGE_METERS, -SimConfig.EDGE_METERS));
        addACar(new XY(-SimConfig.EDGE_METERS, -SimConfig.EDGE_METERS));
        addACar(new XY(-SimConfig.EDGE_METERS, SimConfig.EDGE_METERS));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnAddCar.getId()) {
            addACar(new XY(0, 0));
            tvNumOfCars.setText(String.valueOf(simulationView.getNumberOfCars()));
        } else if (view.getId() == btnAdd5Cars.getId()) {
            add5Cars();
            tvNumOfCars.setText(String.valueOf(simulationView.getNumberOfCars()));
        } else if (view.getId() == btnRunSimulation.getId()) {
            if (simulationThread == null) {
                simulationThread = new SimulationThread();
                simulationThread.start();
            }

            btnRunSimulation.setEnabled(false);
        }
    }

    @Override
    public void onFPSChanged(int fps) {
        tvFPS.setText(String.valueOf(fps));
    }

    @Override
    public void onTotalCollisionsChanged(int totalCollisions) {
        if (!updateTotalCollision) {
            return;
        }
        tvTotalCollisions.setText(String.valueOf(totalCollisions));
    }

    @Override
    public void onTimeChanged(long time) {
        tvTime.setText(String.valueOf(time));
    }

    public class SimulationThread extends Thread {
        public void run() {
            SimTime simTme = SimTime.getInstance();

            simulationView.clearWorld();
            simTme.resetTime();

            for (int i = 0; i < SimConfig.SIM_NUM_OF_5_CAR_SETS; i++) {
                long currentTime = simTme.getTime();
                while (currentTime + SimConfig.SIM_DELAY_ADDITION > simTme.getTime()) {
                    simulationView.waitForPreDraw();
                }
                add5Cars();
                SimulationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvNumOfCars.setText(String.valueOf(simulationView.getNumberOfCars()));
                    }
                });
            }

            updateTotalCollision = false;
        }
    }
}
