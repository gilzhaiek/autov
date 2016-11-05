package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.eightman.autov.Simulation.SimulationView;

public class SimulationActivity extends AppCompatActivity implements View.OnClickListener {
    SimulationView simulationView;
    Button btnAddCar, btnAdd10Cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView)findViewById(R.id.surfaceSimulation);

        btnAddCar = (Button)findViewById(R.id.btnAddCar);
        btnAdd10Cars = (Button)findViewById(R.id.btnAdd10Cars);
        btnAddCar.setOnClickListener(this);
        btnAdd10Cars.setOnClickListener(this);
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
        if(view.getId() == btnAddCar.getId()) {
            simulationView.addRandomCar();
        } else if(view.getId() == btnAdd10Cars.getId()) {
            for(int i = 0; i < 10; i++) {
                simulationView.addRandomCar();
            }
        }
    }
}
