package com.djarum.directsales.View;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataAnalyticsFragment extends Fragment {
    HorizontalBarChart horizontalBarChart;
    FirebaseDatabase database;
    DatabaseReference buyer;


    public DataAnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        horizontalBarChart = view.findViewById(R.id.horizontalChart);
        database = FirebaseDatabase.getInstance();
        buyer = database.getReference("Buyer");

        setDataChart();
    }

    private void setDataChart() {
        final ArrayList<BarEntry> yVals = new ArrayList<>();
        final ArrayList<Buyer> buyerList = new ArrayList<>();
        final float barWidth = 9f;
        buyer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    buyerList.add(dsp.getValue(Buyer.class));
                }
                Multiset<String> multiset = HashMultiset.create();
                final ArrayList<String> listDomisili = new ArrayList<>();
                for (Buyer b : buyerList) {
                    if(!listDomisili.contains(b.getDomisili())) listDomisili.add(b.getDomisili());
                    multiset.add(b.getDomisili());
                }

                horizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(listDomisili));
                horizontalBarChart.fitScreen();
                horizontalBarChart.setScaleMinima(0.25f, 0.5f);

                for(int i = 0 ; i < listDomisili.size() ; i++){
                    yVals.add(new BarEntry(i, multiset.count(listDomisili.get(i))));
                }

                BarDataSet set1 = new BarDataSet(yVals, "Data set 1");
                BarData data = new BarData(set1);
                data.setBarWidth(0.5f);
                horizontalBarChart.setData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
