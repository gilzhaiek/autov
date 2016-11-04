package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.eightman.autov.Simulation.SimulationView;

public class SimulationActivity extends AppCompatActivity {
    SimulationView simulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView)findViewById(R.id.surfaceSimulation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addCar(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        simulationView.clearCars();
    }

    protected void addCar(View v) {
        simulationView.addRandomCar();
    }
}
