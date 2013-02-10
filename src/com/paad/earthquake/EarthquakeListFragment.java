package com.paad.earthquake;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class EarthquakeListFragment extends ListFragment {
    private ArrayList<Quake> earthquakes = new ArrayList<Quake>();
    private ArrayAdapter<Quake> adapter;
    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        int layoutId = android.R.layout.simple_list_item_1;
        adapter = new ArrayAdapter<Quake>(getActivity(), layoutId, earthquakes);
        setListAdapter(adapter);

        Thread t = new Thread(new Runnable() {
            public void run() {
                refreshEarthquakes();
            }
        });
        t.start();
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

                earthquakes.clear();

                NodeList nl = docEl.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        final Quake quake = parseQuake(nl, i);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                addNewQuake(quake);
                            }
                        });
                    }
                }
            }

        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        }
    }

    private Quake parseQuake(NodeList nl, int i) {
        Element entry = (Element)nl.item(i);
        Element title = (Element)entry.getElementsByTagName("title").item(0);
        Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
        Element when = (Element)entry.getElementsByTagName("updated").item(0);
        Element link = (Element)entry.getElementsByTagName("link").item(0);

        String details = title.getFirstChild().getNodeValue();

        String linkString = link.getAttribute("href");

        String point = g.getFirstChild().getNodeValue();
        String dt = when.getFirstChild().getNodeValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Date qdate = new GregorianCalendar(0,0,0).getTime();
        try {
            qdate = sdf.parse(dt);
        } catch (ParseException e) {
        }

        String[] location = point.split(" ");
        Location l = new Location("dummyGPS");
        l.setLatitude(Double.parseDouble(location[0]));
        l.setLongitude(Double.parseDouble(location[1]));

        String magnitudeString = details.split(" ")[1];
        int end = magnitudeString.length()-1;
        double magnitude = Double.parseDouble(magnitudeString.substring(0, end));
        details = details.split(",")[1].trim();

        return new Quake(qdate, details, l, magnitude, linkString);
    }

    private void addNewQuake(Quake _quake) {
        earthquakes.add(_quake);
        adapter.notifyDataSetChanged();
    }
}
