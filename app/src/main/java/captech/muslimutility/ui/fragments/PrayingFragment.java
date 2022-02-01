package captech.muslimutility.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azan.types.PrayersType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import captech.muslimutility.Customization.FontsOverride;
import captech.muslimutility.R;
import captech.muslimutility.SharedData.SharedDataClass;
import captech.muslimutility.calculator.calendar.HGDate;
import captech.muslimutility.calculator.location.LocationReader;
import captech.muslimutility.calculator.prayer.PrayerTimes;
import captech.muslimutility.database.ConfigPreferences;
import captech.muslimutility.model.LocationInfo;
import captech.muslimutility.utility.Dates;
import captech.muslimutility.utility.NumbersLocal;

import static com.azan.types.AngleCalculationType.MUHAMMADIYAH;

public class PrayingFragment extends Fragment {
    private TextView monthDay, monthView, weekDay,
            HmonthDay, HmonthView, country, city,
            sunrise, magrib, isha, salahNow, remain, yeartxt, dohaurdu, maghriburdu, ishaurdu,
            fajrEnTxt, sunriseEnTxt, zuhrEnTxt, asrEnTxt, magribEnTxt, ishaEnTxt;
    public static TextView fajr, zuhr, asr, fajrurdu, zohrurdu, asrurdu;
    public static CardView c1, c2, c3, c4, c5, c6;
    private LinearLayout pray1, pray2, pray3, pray4, pray5, pray6;
    private Context context;
    private LocationReader lr;
    private PrayerTimes prayerTimes;
    private SimpleDateFormat format;
    private Timer timer;
    private LocationInfo locationInfo;
    Typeface Roboto_Bold, Roboto_Light, Roboto_Reg, Roboto_Thin, ProximaNovaReg, ProximaNovaBold, tf;
    TelephonyManager manager;
    String flag = "0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_praying, container, false);
        context = getActivity();
        format = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        getMazhabValue();

        ProximaNovaReg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaReg.ttf");
        ProximaNovaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ProximaNovaBold.ttf");
        Roboto_Bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
        Roboto_Light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_Reg = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Roboto_Thin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/simple.otf");
        FontsOverride.setDefaultFont(getActivity(), "DEFAULT", "fonts/ProximaNovaReg.ttf");

        manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View rootView) {

        c1 = (CardView) rootView.findViewById(R.id.c1);
        c2 = (CardView) rootView.findViewById(R.id.c2);
        c3 = (CardView) rootView.findViewById(R.id.c3);
        c4 = (CardView) rootView.findViewById(R.id.c4);
        c5 = (CardView) rootView.findViewById(R.id.c5);
        c6 = (CardView) rootView.findViewById(R.id.c6);

        fajrEnTxt = (TextView) rootView.findViewById(R.id.fajrEnTxt);
        sunriseEnTxt = (TextView) rootView.findViewById(R.id.sunriseEnTxt);
        zuhrEnTxt = (TextView) rootView.findViewById(R.id.zuhrEnTxt);
        asrEnTxt = (TextView) rootView.findViewById(R.id.asrEnTxt);
        magribEnTxt = (TextView) rootView.findViewById(R.id.magribEnTxt);
        ishaEnTxt = (TextView) rootView.findViewById(R.id.ishaEnTxt);
        pray1 = (LinearLayout) rootView.findViewById(R.id.p1);
        pray2 = (LinearLayout) rootView.findViewById(R.id.p2);
        pray3 = (LinearLayout) rootView.findViewById(R.id.p3);
        pray4 = (LinearLayout) rootView.findViewById(R.id.p4);
        pray5 = (LinearLayout) rootView.findViewById(R.id.p5);
        pray6 = (LinearLayout) rootView.findViewById(R.id.p6);
        fajrurdu = (TextView) rootView.findViewById(R.id.fajrurdu);
        dohaurdu = (TextView) rootView.findViewById(R.id.dohaurdu);
        zohrurdu = (TextView) rootView.findViewById(R.id.zohrurdu);
        asrurdu = (TextView) rootView.findViewById(R.id.asrurdu);
        maghriburdu = (TextView) rootView.findViewById(R.id.maghriburdu);
        ishaurdu = (TextView) rootView.findViewById(R.id.ishaurdu);

        fajrurdu.setTypeface(tf);
        dohaurdu.setTypeface(tf);
        zohrurdu.setTypeface(tf);
        asrurdu.setTypeface(tf);
        maghriburdu.setTypeface(tf);
        ishaurdu.setTypeface(tf);

        final HGDate hgDate = new HGDate();
        monthDay = (TextView) rootView.findViewById(R.id.textView3);
        monthDay.setText(NumbersLocal.convertNumberType(getContext(), hgDate.getDay() + ""));
        monthDay.setTypeface(ProximaNovaBold);
        monthView = (TextView) rootView.findViewById(R.id.textView4);
        monthView.setText(Dates.getHalfMonth(getContext(), hgDate.getMonth() - 1) + "");
        weekDay = (TextView) rootView.findViewById(R.id.textView);
        weekDay.setText(Dates.weekDayName(getContext(), hgDate.weekDay() + 1));
        weekDay.setTypeface(ProximaNovaBold);

        hgDate.toHigri();

        yeartxt = (TextView) rootView.findViewById(R.id.yeartxt);
        yeartxt.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        yeartxt.setTypeface(ProximaNovaBold);
        HmonthDay = (TextView) rootView.findViewById(R.id.textView5);
        HmonthDay.setText(NumbersLocal.convertNumberType(getContext(), String.valueOf(hgDate.getDay()).trim()));
        HmonthDay.setTypeface(ProximaNovaBold);
        HmonthView = (TextView) rootView.findViewById(R.id.textView6);
        HmonthView.setText(Dates.islamicMonthName(getContext(), Integer.valueOf(hgDate.getMonth()) - 1).trim());

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_top);
        fajr = (TextView) rootView.findViewById(R.id.fajrTime);
        fajr.setAnimation(animation);
        sunrise = (TextView) rootView.findViewById(R.id.sunriseTime);
        sunrise.setAnimation(animation);
        zuhr = (TextView) rootView.findViewById(R.id.zuhrTime);
        zuhr.setAnimation(animation);
        asr = (TextView) rootView.findViewById(R.id.asrTime);
        asr.setAnimation(animation);
        magrib = (TextView) rootView.findViewById(R.id.magribTime);
        magrib.setAnimation(animation);
        isha = (TextView) rootView.findViewById(R.id.ishaTime);
        isha.setAnimation(animation);
        salahNow = (TextView) rootView.findViewById(R.id.textView7);
        remain = (TextView) rootView.findViewById(R.id.textView8);

        if (flag.equals("2")) {
            zuhr.setText(getResources().getString(R.string.zuhrain));
            zohrurdu.setText(getResources().getString(R.string.zuhrainurdu));
            c5.setVisibility(View.GONE);
            c6.setVisibility(View.GONE);
        } else {
            zuhr.setText(getResources().getString(R.string.zuhr));
            zohrurdu.setText(getResources().getString(R.string.zuhrurdu));
            c5.setVisibility(View.VISIBLE);
            c6.setVisibility(View.VISIBLE);
        }

        locationInfo = ConfigPreferences.getLocationConfig(getContext());
        if (locationInfo != null) {
            lr = new LocationReader(context);
            lr.read(locationInfo.latitude, locationInfo.longitude);
            country = (TextView) rootView.findViewById(R.id.textView2);

            if (SharedDataClass.locationArea == null) {
                country.setText("No location detected");
            } else {
                country.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                country.setText(SharedDataClass.locationArea);
                country.setSelected(true);
                country.setSingleLine(true);
            }

            city = (TextView) rootView.findViewById(R.id.textView32);
            city.setText(getString(R.string.near) + " " + (getResources().getConfiguration()
                    .locale.getDisplayLanguage()
                    .equals("العربية") ? locationInfo.city_ar : locationInfo.city) + ",");

            if (lr.isAvailable()) {
                getPrayer();
            }

        }

    }

    private void getPrayer() {
        if (lr == null || !lr.isAvailable()) return;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset() / (1000 * 60 * 60);
        int dst = calendar.getTimeZone().getDSTSavings();

        if (locationInfo.dls == 1) {
            dst = 1;
        }
        PrayerTimes.Mazhab mazhab = PrayerTimes.Mazhab.PTC_MAZHAB_SHAFEI;
        PrayerTimes.Way way = PrayerTimes.Way.PTC_WAY_EGYPT;
        switch (locationInfo.way) {
            case 0:
                way = PrayerTimes.Way.PTC_WAY_EGYPT;
                break;
            case 1:
                way = PrayerTimes.Way.PTC_WAY_KARACHI;
                break;
            case 2:
                way = PrayerTimes.Way.PTC_WAY_ISNA;
                break;
            case 3:
                way = PrayerTimes.Way.PTC_WAY_UMQURA;
                break;
            case 4:
                way = PrayerTimes.Way.PTC_WAY_MWL;
                break;
        }

        switch (locationInfo.mazhab) {
            case 0:
                mazhab = PrayerTimes.Mazhab.PTC_MAZHAB_SHAFEI;
                break;
            case 1:
                mazhab = PrayerTimes.Mazhab.PTC_MAZHAB_HANAFI;
                break;
        }

        prayerTimes = new PrayerTimes(day, month + 1, year, SharedDataClass.latitude, SharedDataClass.longitude, timeZone, (locationInfo.dls > 0), mazhab, way);
        updateViews();

    }

    Date fajrDate, sunriseDate, duhrDate, asrDate, maghrebDate, ishaDate, midNightDate;

    private void updateViews() {

        GregorianCalendar date = new GregorianCalendar();
        com.azan.PrayerTimes prayerTimes = new com.azan.TimeCalculator().date(date).location(SharedDataClass.latitude, SharedDataClass.longitude,
                0, 0).timeCalculationMethod(MUHAMMADIYAH).calculateTimes();
        prayerTimes.setUseSecond(true);

        Calendar mid = Calendar.getInstance();
        mid.set(Calendar.HOUR_OF_DAY, 0);
        mid.set(Calendar.MINUTE, 0);
        mid.set(Calendar.SECOND, 0);
        midNightDate = mid.getTime();

        try {
            fajrDate = prayerTimes.getPrayTime(PrayersType.FAJR);
            fajr.setText(format.format(prayerTimes.getPrayTime(PrayersType.FAJR)));

            sunriseDate = prayerTimes.getPrayTime(PrayersType.SUNRISE);
            sunrise.setText(format.format(prayerTimes.getPrayTime(PrayersType.SUNRISE)));

            duhrDate = prayerTimes.getPrayTime(PrayersType.ZUHR);
            zuhr.setText(format.format(prayerTimes.getPrayTime(PrayersType.ZUHR)));

            asrDate = prayerTimes.getPrayTime(PrayersType.ASR);
            asr.setText(format.format(prayerTimes.getPrayTime(PrayersType.ASR)));

            maghrebDate = prayerTimes.getPrayTime(PrayersType.MAGHRIB);
            magrib.setText(format.format(prayerTimes.getPrayTime(PrayersType.MAGHRIB)));

            ishaDate = prayerTimes.getPrayTime(PrayersType.ISHA);
            isha.setText(format.format(prayerTimes.getPrayTime(PrayersType.ISHA)));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("fajr", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.FAJR))));
            editor.putString("sunset", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.SUNRISE))));
            editor.putString("zohr", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.ZUHR))));
            editor.putString("asr", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.ASR))));
            editor.putString("maghrib", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.MAGHRIB))));
            editor.putString("isha", String.valueOf(format.format(prayerTimes.getPrayTime(PrayersType.ISHA))));
            editor.apply();
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            fajr.setText(preferences.getString("fajr", ""));
            sunrise.setText(preferences.getString("sunset", ""));
            zuhr.setText(preferences.getString("zohr", ""));
            asr.setText(preferences.getString("asr", ""));
            magrib.setText(preferences.getString("maghrib", ""));
            isha.setText(preferences.getString("isha", ""));
        }

        checkActiveView();

    }

    String nextPray = "";
    Date nextDate, lastDate;

    private void checkActiveView() {
        if (fajrDate == null || sunriseDate == null || duhrDate == null || asrDate == null || maghrebDate == null || ishaDate == null)
            return;
        removeActiveViews();
        Date current = Calendar.getInstance().getTime();


        if (current.after(fajrDate) && current.before(sunriseDate)) {
            pray2.setBackgroundColor(getResources().getColor(R.color.contrast));
            dohaurdu.setTextColor(getResources().getColor(R.color.white));
            sunriseEnTxt.setTextColor(getResources().getColor(R.color.white));
            sunrise.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.sunrise);
            lastDate = fajrDate;
            nextDate = sunriseDate;
        } else if (current.after(sunriseDate) && current.before(duhrDate)) {
            pray3.setBackgroundColor(getResources().getColor(R.color.contrast));
            zohrurdu.setTextColor(getResources().getColor(R.color.white));
            zuhrEnTxt.setTextColor(getResources().getColor(R.color.white));
            zuhr.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.zuhr);
            lastDate = sunriseDate;
            nextDate = duhrDate;
        } else if (current.after(duhrDate) && current.before(asrDate)) {
            pray4.setBackgroundColor(getResources().getColor(R.color.contrast));
            asrurdu.setTextColor(getResources().getColor(R.color.white));
            asrEnTxt.setTextColor(getResources().getColor(R.color.white));
            asr.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.asr);
            lastDate = duhrDate;
            nextDate = asrDate;
        } else if (current.after(asrDate) && current.before(maghrebDate)) {
            pray5.setBackgroundColor(getResources().getColor(R.color.contrast));
            maghriburdu.setTextColor(getResources().getColor(R.color.white));
            magribEnTxt.setTextColor(getResources().getColor(R.color.white));
            magrib.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.magrib);
            lastDate = asrDate;
            nextDate = maghrebDate;
        } else if (current.after(maghrebDate) && current.before(ishaDate)) {
            pray6.setBackgroundColor(getResources().getColor(R.color.contrast));
            ishaurdu.setTextColor(getResources().getColor(R.color.white));
            ishaEnTxt.setTextColor(getResources().getColor(R.color.white));
            isha.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.isha);
            lastDate = maghrebDate;
            nextDate = ishaDate;
        } else {

            if (current.after(midNightDate) && current.before(fajrDate)) {
                lastDate = getPrayerforPreviousDay().get()[5];
                nextDate = fajrDate;
            } else {
                lastDate = ishaDate;
                nextDate = getPrayerforNextDay().get()[0];
            }
            pray1.setBackgroundColor(getResources().getColor(R.color.contrast));
            fajrurdu.setTextColor(getResources().getColor(R.color.white));
            fajrEnTxt.setTextColor(getResources().getColor(R.color.white));
            fajr.setTextColor(getResources().getColor(R.color.white));
            nextPray = getString(R.string.fajr);

        }

        salahNow.setText(NumbersLocal.convertNumberType(getContext(), nextPray + " " + format.format(nextDate)));


        Log.i("DATE_TAg", "last : " + format.format(lastDate));
        Log.i("DATE_TAg", "current : " + format.format(current));
        Log.i("DATE_TAg", "end : " + format.format(nextDate));


        updateTimer(current);

    }

    private void removeActiveViews() {
        pray1.setBackgroundColor(Color.TRANSPARENT);
        pray2.setBackgroundColor(Color.TRANSPARENT);
        pray3.setBackgroundColor(Color.TRANSPARENT);
        pray4.setBackgroundColor(Color.TRANSPARENT);
        pray5.setBackgroundColor(Color.TRANSPARENT);
        pray6.setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(settingsChangeReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();
        context.registerReceiver(settingsChangeReceiver, new IntentFilter("prayer.information.change.in.settings"));
    }

    @Override
    public void onResume() {
        super.onResume();
        checkActiveView();
    }

    int i = 0;
    private BroadcastReceiver settingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (lr != null && lr.isAvailable()) {
                i++;
                Log.i("settingsChanges", "item #" + i);
                locationInfo = ConfigPreferences.getLocationConfig(getContext());
                lr.read(SharedDataClass.latitude, SharedDataClass.longitude);
                getPrayer();
            }
        }
    };
    Calendar endCal = Calendar.getInstance(), startCal = Calendar.getInstance(), currCal = Calendar.getInstance();

    private void updateTimer(Date current) {

        endCal.setTime(nextDate);
        startCal.setTime(lastDate);
        currCal.setTime(current);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final long timeRemaining = endCal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

                int seconds = (int) (timeRemaining / 1000) % 60;
                int minutes = (int) ((timeRemaining / (1000 * 60)) % 60);
                int hours = (int) ((timeRemaining / (1000 * 60 * 60)) % 24);
                int days = (int) (timeRemaining / (1000 * 60 * 60 * 24));
                boolean hasDays = days > 0;
                final String timeNow = String.format("%1$02d%4$s%2$02d%5$s%3$02d%6$s",
                        hasDays ? days : hours,
                        hasDays ? hours : minutes,
                        hasDays ? minutes : seconds,
                        hasDays ? ":" : ":",
                        hasDays ? ":" : ":",
                        hasDays ? "m" : "s");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (timeRemaining <= 0) {
                                checkActiveView();
                                return;
                            }
                            remain.setText(timeNow + " remaining in " + nextPray);
                        }
                    });
                }

            }
        }, 1000, 1000);

    }

    private PrayerTimes getPrayerforPreviousDay() {

        if (lr == null || !lr.isAvailable()) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset() / (1000 * 60 * 60);
        int dst = calendar.getTimeZone().getDSTSavings();
        if (locationInfo.dls == 1) {
            dst = 1;
        }
        return new PrayerTimes(day, month + 1, year, SharedDataClass.latitude, SharedDataClass.longitude, timeZone, !(dst > 0), PrayerTimes.getDefaultMazhab(manager.getSimCountryIso().toUpperCase()), PrayerTimes.getDefaultWay(manager.getSimCountryIso().toUpperCase()));
    }


    private PrayerTimes getPrayerforNextDay() {
        if (lr == null || !lr.isAvailable()) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int timeZone = calendar.getTimeZone().getRawOffset() / (1000 * 60 * 60);
        int dst = calendar.getTimeZone().getDSTSavings();
        if (locationInfo.dls == 1) {
            dst = 1;
        }
        return new PrayerTimes(day, month + 1, year, lr.getLatitude(), lr.getLongitude(), timeZone, !(dst > 0), PrayerTimes.getDefaultMazhab(manager.getSimCountryIso().toUpperCase()), PrayerTimes.getDefaultWay(manager.getSimCountryIso().toUpperCase()));
    }

    public void getMazhabValue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        flag = preferences.getString("mazhabvalue", "");
    }

}
