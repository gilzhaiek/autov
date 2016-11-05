package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eightman.autov.Simulation.SimulationView;

public class SimulationActivity extends AppCompatActivity implements View.OnClickListener {
    SimulationView simulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView)findViewById(R.id.surfaceSimulation);

        Button btnAddCar = (Button)findViewById(R.id.btnAddCar);
        btnAddCar.setOnClickListener(this);
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
        simulationView.addRandomCar();
    }
}
