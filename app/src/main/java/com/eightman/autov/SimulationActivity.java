package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Simulation.SimulationView;
import com.eightman.autov.Utils.XY;

public class SimulationActivity extends AppCompatActivity implements View.OnClickListener, StatsInterface {
    SimulationView simulationView;
    Button btnAddCar, btnAdd5Cars;
    TextView tvNumOfCars, tvFPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView) findViewById(R.id.surfaceSimulation);

        tvNumOfCars = (TextView) findViewById(R.id.tvNumOfCars);
        tvFPS = (TextView) findViewById(R.id.tvFPS);
        btnAddCar = (Button) findViewById(R.id.btnAddCar);
        btnAdd5Cars = (Button) findViewById(R.id.btnAdd5Cars);
        btnAddCar.setOnClickListener(this);
        btnAdd5Cars.setOnClickListener(this);

        simulationView.registerStats(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addCar(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        simulationView.clearWorld();
    }

    protected void addCar(View v) {
    }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == btnAddCar.getId()) {
                simulationView.addRandomCar(new XY(0, 0));
            } else if (view.getId() == btnAdd5Cars.getId()) {
                simulationView.addRandomCar(new XY(0, 0));
                simulationView.addRandomCar(new XY(SimConfig.EDGE_METERS, SimConfig.EDGE_METERS));
                simulationView.addRandomCar(new XY(SimConfig.EDGE_METERS, -SimConfig.EDGE_METERS));
                simulationView.addRandomCar(new XY(-SimConfig.EDGE_METERS, -SimConfig.EDGE_METERS));
                simulationView.addRandomCar(new XY(-SimConfig.EDGE_METERS, SimConfig.EDGE_METERS));
            }
            tvNumOfCars.setText(String.valueOf(simulationView.getNumberOfCars()));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFPSChanged(int fps) {
        tvFPS.setText(String.valueOf(fps));
    }
}
