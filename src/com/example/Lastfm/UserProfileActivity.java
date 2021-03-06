package com.example.Lastfm;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private final static String USER    = "ShutUpAndSkate";
    private final static String FORMAT  = "json";
    private final static Integer LIMIT  = 3;
    private final static String API_KEY = "4ee413fa8853b3c7d4d06fa6d9809e45";

    GetRecentTracksTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        task = new GetRecentTracksTask();
        task.execute();
    }

    class GetRecentTracksTask extends AsyncTask<Map[], Void, Map[]> {
        @Override
        protected Map[] doInBackground(Map[]... params) {
            HttpURLConnection connection = null;
            StringBuilder sb = new StringBuilder();

            try {
                URL url = new URL("http://ws.audioscrobbler.com/2.0/?" +
                        "method=user.getrecenttracks" +
                        "&user="+ USER + "&api_key=" + API_KEY +
                        "&format=" + FORMAT + "&limit=" + LIMIT);

                connection = (HttpURLConnection)url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ( (line = br.readLine()) != null ) sb.append(line + "\n");
                br.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            JSONObject jsonData, track;
            JSONArray tracks;
            Map[] recentTracks = new Map[LIMIT];

            try {
                jsonData = new JSONObject(sb.toString());
                tracks = (JSONArray) ((JSONObject) jsonData.get("recenttracks")).get("track");

                for(Integer i=0; i<LIMIT; i++) {
                    Map<String, String> m = new HashMap<String, String>();
                    track = (JSONObject) tracks.get(i);

                    m.put("trackName", (String) track.get("name"));
                    m.put("trackTime", (String) ((JSONObject) track.get("date")).get("uts"));
                    m.put("trackArtistName", (String) ((JSONObject) track.get("artist")).get("#text"));
                    m.put("albumImageUrl", (String) ((JSONObject)(((JSONArray) track.get("image"))).get(1)).get("#text"));

                    recentTracks[i] = m;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return recentTracks;
        }

        @Override
        protected void onPostExecute(Map[] data) {
            super.onPostExecute(data);

            for(Integer i=0; i<data.length; i++){
                Log.d("mylog", data[i].get("trackArtistName").toString());
            }

        }

    }
}
