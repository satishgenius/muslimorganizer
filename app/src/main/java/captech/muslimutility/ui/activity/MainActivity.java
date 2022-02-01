package captech.muslimutility.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.Manager.DBManager;
import captech.muslimutility.R;
import captech.muslimutility.SharedData.SharedDataClass;
import captech.muslimutility.adapter.ViewPagerAdapter;
import captech.muslimutility.calculator.location.LocationReader;
import captech.muslimutility.calculator.prayer.PrayerTimes;
import captech.muslimutility.calculator.quibla.QuiblaCalculator;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.model.ZekerType;
import captech.muslimutility.service.FusedLocationService;
import captech.muslimutility.ui.fragments.AzkarFragment;
import captech.muslimutility.ui.fragments.CalendarFragment;
import captech.muslimutility.ui.fragments.IslamicEventsFragment;
import captech.muslimutility.ui.fragments.PrayingFragment;
import captech.muslimutility.ui.fragments.WeatherFragment;
import captech.muslimutility.ui.popup.CountryPrayerPopup;
import captech.muslimutility.ui.popup.DataConvertPopup;
import captech.muslimutility.utility.Alarms;
import captech.muslimutility.utility.Validations;

public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener {
    private static final int REQUEST_GPS_LOCATION = 113;
    public static LocationInfo locationInfo;
    public static int quiblaDegree;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    ViewPagerAdapter adapter;
    FusedLocationService gps;
    ProgressDialog progressDialog;
    public static List<ZekerType> zekerTypeList = new ArrayList<>();
    Location gps_loc = null, network_loc = null, final_loc = null;
    private Dialog dialog;
    Typeface Roboto_Bold, Roboto_Light, Roboto_Reg, Roboto_Thin, ProximaNovaReg, ProximaNovaBold;
    TabLayout tabLayout;
    public DBManager dbManager;

    private int[] tabIcons = {
            R.drawable.mosqueone,
            R.drawable.calendar,
            R.drawable.hands,
            R.drawable.event,
            R.drawable.cloud
    };

    private AdView mAdView;
    PrayingFragment prayingFragment;
    CalendarFragment calendarFragment;
    AzkarFragment azkarFragment;
    IslamicEventsFragment islamicEventsFragment;
    WeatherFragment weatherFragment;
    TelephonyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load application language
        String languageToLoad = ConfigPreferences.getApplicationLanguage(this);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getResources().getString(R.string.app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dbManager = new DBManager(this);
        dbManager.open();
        try {
            dbManager.copyDataBase();
        } catch (IOException e) {

        }

//        zekerTypeList = dbManager.getAllAzkarTypes();

        ProximaNovaReg = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNovaBold.ttf");
        Roboto_Bold = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Bold.ttf");
        Roboto_Light = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_Reg = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Thin = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Thin.ttf");

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ProximaNovaReg.ttf");
        manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gps_loc != null) {
            final_loc = gps_loc;
            SharedDataClass.longitude = final_loc.getLongitude();
            SharedDataClass.latitude = final_loc.getLatitude();

        } else if (network_loc != null) {
            final_loc = network_loc;
            SharedDataClass.longitude = final_loc.getLongitude();
            SharedDataClass.latitude = final_loc.getLatitude();

        } else {
            SharedDataClass.longitude = 33.692959;
            SharedDataClass.latitude = 73.042072;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(SharedDataClass.latitude, SharedDataClass.longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {
                SharedDataClass.locationArea = listAddresses.get(0).getAddressLine(0);
                SharedDataClass.locationCity = listAddresses.get(0).getAddressLine(1);
                SharedDataClass.locationCountry = listAddresses.get(0).getAddressLine(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationInfo = ConfigPreferences.getLocationConfig(this);
        quiblaDegree = ConfigPreferences.getQuibla(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        if (ConfigPreferences.getPrayingNotification(this))
            Alarms.setNotificationAlarmMainPrayer(this);

        //clickable application title
        TextView applicationTitle = (TextView) findViewById(R.id.title);
        applicationTitle.setTypeface(ProximaNovaBold);
        applicationTitle.setText(getString(R.string.main));
        applicationTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (ConfigPreferences.getLocationConfig(this) == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                getLocation();
            }
        }

        new AzkarTypes().execute();

    }

    public void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setMessage(getString(R.string.location_dialog_message));
        dialogBuilder.setTitle(android.R.string.dialog_alert_title);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getLocation();
            }
        });

        dialog = dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, CompassActivity.class));
            return true;
        } else if (id == R.id.action_location) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_GPS_LOCATION);
            } else {
                showDialog();
                return true;
            }

        } else if (id == R.id.action_convert_date) {
            new DataConvertPopup(this);
        } else if (id == R.id.settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 16);
        } else if (id == R.id.mosques) {
            //check gps enable or not
            if (Validations.gpsEnabled(this)) {
                if (Validations.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, MosquesActivity.class));
                }
            }

        } else if (id == R.id.worldpraye) {
            new CountryPrayerPopup(this, true, false);
        } else if (id == R.id.action_rate_app) {
            String url = "https://play.google.com/store/apps/details?id=com.captech.muslimutility";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.action_about_app) {
            //start about activity
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "#" + getString(R.string.app_name) + "\n https://play.google.com/store/apps/details?id=com.captech.muslimutility");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GPS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Validations.REQUEST_CODE && resultCode == 0) {
            getLocation();
        } else if (requestCode == 16) {
        }

    }

    public void getLocation() {
        if (Validations.gpsEnabledInLocation(this, true, true)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.detecting_location));
            progressDialog.show();
            gps = new FusedLocationService(this, this);
        }
    }

    Location currLocation = null;

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && currLocation == null) {
            currLocation = location;
            gps.setFusedLatitude(location.getLatitude());
            gps.setFusedLongitude(location.getLongitude());
            if (gps.getFusedLatitude() != 0 && gps.getFusedLongitude() != 0) {
                LocationInfo locationInfo = dbManager.getLocationInfo((float) gps.getFusedLatitude(), (float) gps.getFusedLongitude());
                Calendar calendar = Calendar.getInstance();
                LocationReader lr = new LocationReader(this);
                lr.read(gps.getFusedLatitude(), gps.getFusedLongitude());
                int dst = calendar.getTimeZone().getDSTSavings();
                locationInfo.dls = dst;
                switch (PrayerTimes.getDefaultMazhab(manager.getSimCountryIso().toUpperCase())) {
                    case PTC_MAZHAB_HANAFI:
                        locationInfo.mazhab = 1;
                        break;
                    case PTC_MAZHAB_SHAFEI:
                        locationInfo.mazhab = 0;
                        break;
                }
                switch (PrayerTimes.getDefaultWay(manager.getSimCountryIso().toUpperCase())) {
                    case PTC_WAY_EGYPT:
                        locationInfo.way = 0;
                        break;
                    case PTC_WAY_UMQURA:
                        locationInfo.way = 3;
                        break;

                    case PTC_WAY_MWL:
                        locationInfo.way = 4;
                        break;

                    case PTC_WAY_KARACHI:
                        locationInfo.way = 1;
                        break;

                    case PTC_WAY_ISNA:
                        locationInfo.way = 2;
                        break;
                }
                ConfigPreferences.setLocationConfig(this, locationInfo);
                ConfigPreferences.setQuibla(this, (int) QuiblaCalculator.doCalculate((float) location.getLatitude(), (float) location.getLongitude()));
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("mazhab", locationInfo.mazhab + ""); // value to store
                editor.putString("calculations", locationInfo.way + "");
                editor.commit();
                progressDialog.cancel();
                gps.stopFusedLocation();
                Intent intent = getIntent();
                sendBroadcast(new Intent().setAction("prayer.information.change"));
                finish();
                startActivity(intent);
                ConfigPreferences.setPrayingNotification(this, true);
                Alarms.startCalculatePrayingBroadcast(this);
            }
        } else {
            new CountryPrayerPopup(this, true, true);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new PrayingFragment();
                case 1:
                    return new CalendarFragment();
                case 2:
                    return new AzkarFragment();
                case 3:
                    return new IslamicEventsFragment();
                default:
                    return new WeatherFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.praying_tab);
                case 1:
                    return getString(R.string.calender_tab);
                case 2:
                    return getString(R.string.azkar_tab);
                case 3:
                    return getString(R.string.islamic_tab);
                case 4:
                    return getString(R.string.weather_tab);

            }
            return null;
        }
    }

    private class AzkarTypes extends AsyncTask<Void, Void, List<ZekerType>> {

        @Override
        protected List<ZekerType> doInBackground(Void... voids) {
            zekerTypeList = new ArrayList<>();
            return dbManager.getAllAzkarTypes();
        }

        @Override
        protected void onPostExecute(List<ZekerType> zekerTypes) {
            super.onPostExecute(zekerTypes);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        prayingFragment = new PrayingFragment();
        calendarFragment = new CalendarFragment();
        azkarFragment = new AzkarFragment();
        islamicEventsFragment = new IslamicEventsFragment();
        weatherFragment = new WeatherFragment();
        adapter.addFragment(prayingFragment, "Azan");
        adapter.addFragment(calendarFragment, "Calendar");
        adapter.addFragment(azkarFragment, "Azkar");
        adapter.addFragment(islamicEventsFragment, "Events");
        adapter.addFragment(weatherFragment, "Weather");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps_loc = null;
        network_loc = null;
    }

}
