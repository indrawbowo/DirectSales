package com.djarum.directsales.View;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.djarum.directsales.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity{

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    Fragment addNewBuyerFragment, buyerListFragment, databaseFragment, dataAnalyticsFragment, sendSurveyFragment;
    final private static String ARGS_READ = "READ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        buyerListFragment = new BuyerListFragment();
        databaseFragment = new DatabaseFragment();
        dataAnalyticsFragment = new DataAnalyticsFragment();
        sendSurveyFragment = new SendSurveyFragment();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        LinearLayout logout = (LinearLayout) findViewById(R.id.logout);
        LinearLayout sendSurvey = (LinearLayout) findViewById(R.id.send_survey);
        LinearLayout directSales = (LinearLayout) findViewById(R.id.direct_sales);
        LinearLayout addNewBuyer = (LinearLayout) findViewById(R.id.add_new_buyer);
        LinearLayout database = (LinearLayout) findViewById(R.id.database);
        LinearLayout dataAnalytics = (LinearLayout) findViewById(R.id.data_analytics);

        addNewBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBuyerFragment = new AddNewBuyerFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.rootLayout, addNewBuyerFragment).commit();
                transaction.addToBackStack(HomeActivity.class.getSimpleName());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finishAffinity();
            }
        });

        directSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ARGS_READ,false);
                buyerListFragment.setArguments(bundle);
                transaction.add(R.id.rootLayout, buyerListFragment).commit();
                transaction.addToBackStack(HomeActivity.class.getSimpleName());
            }
        });

        sendSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.rootLayout, sendSurveyFragment).commit();
                transaction.addToBackStack(HomeActivity.class.getSimpleName());

            }
        });
        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.rootLayout, databaseFragment).commit();
                transaction.addToBackStack(HomeActivity.class.getSimpleName());
            }
        });

        dataAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.rootLayout, dataAnalyticsFragment).commit();
                transaction.addToBackStack(HomeActivity.class.getSimpleName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        getMenuInflater().inflate(R.menu.home_menu,menu);
        MenuItem welcome = menu.findItem(R.id.welcome);
        welcome.setTitle("Welcome, " + firebaseUser.getDisplayName());

        return true;
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else finish();
    }

}
