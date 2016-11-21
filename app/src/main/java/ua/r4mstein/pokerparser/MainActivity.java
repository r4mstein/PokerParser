package ua.r4mstein.pokerparser;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.r4mstein.pokerparser.adapter.RecyclerAdapter;
import ua.r4mstein.pokerparser.services.GTIntentService;
import ua.r4mstein.pokerparser.services.PSIntentService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecyclerAdapter.OnRecyclerViewClickListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    public static final String TAG_PS_LINK = "ps_link";
    public static final String TAG_GT_LINK = "gt_link";
    public static final String PS_LINK_1 = "https://ru.pokerstrategy.com/forum/board.php?boardid=387";
    public static final String PS_LINK_2 = "https://ru.pokerstrategy.com/forum/board.php?boardid=388";
    public static final String PS_LINK_3 = "https://ru.pokerstrategy.com/forum/board.php?boardid=389";
    public static final String PS_LINK_4 = "https://ru.pokerstrategy.com/forum/board.php?boardid=413";
    public static final String GT_LINK_1 = "http://forum.gipsyteam.ru/backing/forum?fid=43";
    public static final String GT_LINK_2 = "http://forum.gipsyteam.ru/backing/forum?fid=75";
    private static final String PS_MONITOR_STATUS = "ps_monitor_status";
    private static final String GT_MONITOR_STATUS = "gt_monitor_status";
    private static final String HEADER = "header";

    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private ProgressBar mProgressBar;
    private TextView mHeader;
    private TextView mPSMonitoringStatus;
    private TextView mGTMonitoringStatus;

    ArrayList<MyModel> mModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_bar);
        setSupportActionBar(mToolbar);

        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        mDrawer.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mHeader = (TextView) findViewById(R.id.headerTextView);
        mPSMonitoringStatus = (TextView) findViewById(R.id.psMonitoringStatus);
        mGTMonitoringStatus = (TextView) findViewById(R.id.gtMonitoringStatus);
        boolean alarmPSIsOn = PSIntentService.isServiceAlarmOn(getApplicationContext());
        boolean alarmGTIsOn = GTIntentService.isServiceAlarmOn(getApplicationContext());
        psMonitorStatus(alarmPSIsOn);
        gtMonitorStatus(alarmGTIsOn);

        if (savedInstanceState == null) {
            mSelectedId = R.id.nav_item_1;
            navigate(mSelectedId);
        } else {
            mPSMonitoringStatus.setText(savedInstanceState.getString(PS_MONITOR_STATUS));
            mGTMonitoringStatus.setText(savedInstanceState.getString(GT_MONITOR_STATUS));
            String header = savedInstanceState.getString(HEADER);
            mSelectedId = savedInstanceState.getInt(SELECTED_ITEM_ID);
            if (mSelectedId != R.id.nav_item_7 && mSelectedId != R.id.nav_item_8) {
                navigate(mSelectedId);
            } else {
                if (header.equals(getResources().getString(R.string.ps_link_1))){
                    navigate(R.id.nav_item_1);
                } else if (header.equals(getResources().getString(R.string.ps_link_2))){
                    navigate(R.id.nav_item_2);
                } else if (header.equals(getResources().getString(R.string.ps_link_3))){
                    navigate(R.id.nav_item_3);
                } else if (header.equals(getResources().getString(R.string.ps_link_4))){
                    navigate(R.id.nav_item_4);
                } else if (header.equals(getResources().getString(R.string.gt_link_1))){
                    navigate(R.id.nav_item_5);
                } else if (header.equals(getResources().getString(R.string.gt_link_2))){
                    navigate(R.id.nav_item_6);
                }
            }
        }

        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAdapter = new RecyclerAdapter(mModels);
        mRecyclerAdapter.setOnClickListener(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onClickRecyclerView(int id) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(mModels.get(id).getLink()));
            startActivity(intent);
    }

    public class ParsingPSTask extends AsyncTask<String, Void, ArrayList<MyModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MyModel> doInBackground(String... params) {
            ArrayList<MyModel> myModels = new ArrayList<>();
            GetData.getDataFromPSLinks(params[0], myModels);

            return myModels;
        }

        @Override
        protected void onPostExecute(ArrayList<MyModel> list) {
            super.onPostExecute(list);
            mModels.clear();
            for (MyModel model: list) {
                mModels.add(model);
            }
            deleteItemsFromList(mModels);
            mRecyclerAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mRecyclerAdapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public class ParsingGTTask extends AsyncTask<String, Void, ArrayList<MyModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MyModel> doInBackground(String... params) {
            ArrayList<MyModel> my_Models = new ArrayList<>();
            GetData.getDataFromGTLinks(params[0], my_Models);

            return my_Models;
        }

        @Override
        protected void onPostExecute(ArrayList<MyModel> myModels) {
            super.onPostExecute(myModels);
            mProgressBar.setVisibility(View.GONE);
            mModels.clear();
            for (MyModel model : myModels){
                mModels.add(model);
            }
            mRecyclerAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }

    private void navigate(int selectedId) {
        ParsingPSTask parsingPSTask = new ParsingPSTask();
        ParsingGTTask parsingGTTask = new ParsingGTTask();
        switch (selectedId) {
            case R.id.nav_item_1:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingPSTask.execute(PS_LINK_1);
                mHeader.setText(R.string.ps_link_1);
                break;
            case R.id.nav_item_2:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingPSTask.execute(PS_LINK_2);
                mHeader.setText(R.string.ps_link_2);
                break;
            case R.id.nav_item_3:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingPSTask.execute(PS_LINK_3);
                mHeader.setText(R.string.ps_link_3);
                break;
            case R.id.nav_item_4:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingPSTask.execute(PS_LINK_4);
                mHeader.setText(R.string.ps_link_4);
                break;
            case R.id.nav_item_5:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingGTTask.execute(GT_LINK_1);
                mHeader.setText(R.string.gt_link_1);
                break;
            case R.id.nav_item_6:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                parsingGTTask.execute(GT_LINK_2);
                mHeader.setText(R.string.gt_link_2);
                break;
            case R.id.nav_item_7:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                boolean alarmPSIsOn = !PSIntentService.isServiceAlarmOn(getApplicationContext());
                PSIntentService.setServiceAlarm(getApplicationContext(), alarmPSIsOn);
                psMonitorStatus(alarmPSIsOn);
                break;
            case R.id.nav_item_8:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                boolean alarmGTIsOn = !GTIntentService.isServiceAlarmOn(getApplicationContext());
                GTIntentService.setServiceAlarm(getApplicationContext(), alarmGTIsOn);
                gtMonitorStatus(alarmGTIsOn);
                break;
        }
    }

    public void psMonitorStatus(boolean alarmPSIsOn){
        if (alarmPSIsOn){
            mPSMonitoringStatus.setText(R.string.ps_monitor_on);
        } else {
            mPSMonitoringStatus.setText(R.string.ps_monitor_off);
        }
    }

    public void gtMonitorStatus(boolean alarmGTIsOn){
        if (alarmGTIsOn){
            mGTMonitoringStatus.setText(R.string.gt_monitor_on);
        } else {
            mGTMonitoringStatus.setText(R.string.gt_monitor_off);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mSelectedId = item.getItemId();
        navigate(mSelectedId);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
        outState.putString(PS_MONITOR_STATUS, mPSMonitoringStatus.getText().toString());
        outState.putString(GT_MONITOR_STATUS, mGTMonitoringStatus.getText().toString());
        outState.putString(HEADER, mHeader.getText().toString());
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static void deleteItemsFromList(ArrayList list) {
        for (int i = 0; i < 3; i++) {
            list.remove(0);
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
