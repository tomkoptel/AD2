package com.paad.earthquake;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.location.Location;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import com.paad.ad2.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class EarthquakeUpdateService extends IntentService {
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public EarthquakeUpdateService() {
        super("EarthquakeUpdateService");
    }

    public EarthquakeUpdateService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_EARTHQUAKE_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int updateFreq = Integer.parseInt(preferences.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdatedChecked = preferences.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

        if(autoUpdatedChecked) {
            int frequency = updateFreq * 60 * 1000;
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + frequency;
            alarmManager.setInexactRepeating(alarmType, timeToRefresh, frequency, alarmIntent);
        } else {
            alarmManager.cancel(alarmIntent);
        }

        refreshEarthquakes();
    }

    public void refreshEarthquakes() {
        URL url;

        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbf.newDocumentBuilder();

                Document dom = builder.parse(in);
                Element docEl = dom.getDocumentElement();

                NodeList nl = docEl.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Quake quake = parseQuake(nl, i);
                        addNewQuake(quake);
                    }
                }
            }

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } finally {
            stopSelf();
        }

    }

    private Quake parseQuake(NodeList nl, int i) {
        Element entry = (Element) nl.item(i);
        Element title = (Element) entry.getElementsByTagName("title").item(0);
        Element g = (Element) entry.getElementsByTagName("georss:point").item(0);
        Element when = (Element) entry.getElementsByTagName("updated").item(0);
        Element link = (Element) entry.getElementsByTagName("link").item(0);

        String details = title.getFirstChild().getNodeValue();

        String linkString = link.getAttribute("href");

        String point = g.getFirstChild().getNodeValue();
        String dt = when.getFirstChild().getNodeValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Date qdate = new GregorianCalendar(0, 0, 0).getTime();
        try {
            qdate = sdf.parse(dt);
        } catch (ParseException e) {
        }

        String[] location = point.split(" ");
        Location l = new Location("dummyGPS");
        l.setLatitude(Double.parseDouble(location[0]));
        l.setLongitude(Double.parseDouble(location[1]));

        String magnitudeString = details.split(" ")[1];
        int end = magnitudeString.length() - 1;
        double magnitude = Double.parseDouble(magnitudeString.substring(0, end));
        details = details.split(",")[1].trim();

        return new Quake(qdate, details, l, magnitude, linkString);
    }

    public void addNewQuake(Quake _quake) {
        ContentResolver cr = getContentResolver();

        String where = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();
        Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, where, null, null);

        if (query.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());
            values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());

            double lat = _quake.getLocation().getLatitude();
            double lng = _quake.getLocation().getLongitude();

            values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
            values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
            values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());
            cr.insert(EarthquakeProvider.CONTENT_URI, values);
        }
        query.close();
    }

}
