package com.eightman.autov;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.eightman.autov.Configurations.SimConfig;
import com.eightman.autov.Simulation.SimulationView;
import com.eightman.autov.Utils.XY;

public class SimulationActivity extends AppCompatActivity implements View.OnClickListener {
    SimulationView simulationView;
    Button btnAddCar, btnAdd5Cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        simulationView = (SimulationView) findViewById(R.id.surfaceSimulation);

        btnAddCar = (Button) findViewById(R.id.btnAddCar);
        btnAdd5Cars = (Button) findViewById(R.id.btnAdd5Cars);
        btnAddCar.setOnClickListener(this);
        btnAdd5Cars.setOnClickListener(this);
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
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
