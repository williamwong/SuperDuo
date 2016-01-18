package barqsoft.footballscores;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static barqsoft.footballscores.ScoresDBContract.ScoresTable;

public class UpdateScoreService extends IntentService {
    private static final String TAG = "UpdateScoreService";

    public UpdateScoreService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData("n2");
        getData("p2");
    }

    private void getData(String timeFrame) {
        //Creating URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days

        Uri updateUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_TIME_FRAME, timeFrame)
                .build();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;

        //Opening Connection
        try {
            URL url = new URL(updateUri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error Closing Stream");
                }
            }
        }

        try {
            if (jsonData != null) {
                // Check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    // If there is no data, call the function on dummy data.
                    // This is expected behavior during the off-season.
                    processJsonData(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }
                processJsonData(jsonData, getApplicationContext(), true);
            } else {
                Log.d(TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void processJsonData(String jsonData, Context context, boolean isReal) {

        // JSON property keys
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String league;
        String date;
        String time;
        String homeTeam;
        String awayTeam;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;

        try {
            JSONArray matches = new JSONObject(jsonData).getJSONArray(FIXTURES);

            List<ContentValues> values = new ArrayList<>();
            for (int i = 0; i < matches.length(); i++) {
                JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href");
                league = league.replace(SEASON_LINK, "");
                // This if statement controls which leagues we're interested in the data from.
                // add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                int leagueId = Integer.parseInt(league);
                if (leagueId == Util.SERIE_A ||
                        leagueId == Util.BUNDESLIGA1 ||
                        leagueId == Util.PREMIER_LEAGUE ||
                        leagueId == Util.PRIMERA_DIVISION ||
                        leagueId == Util.CHAMPIONS_LEAGUE) {
                    matchId = matchData.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId = matchId + Integer.toString(i);
                    }

                    date = matchData.getString(MATCH_DATE);
                    time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                    date = date.substring(0, date.indexOf("T"));
                    SimpleDateFormat matchDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    matchDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parsedDate = matchDateFormat.parse(date + time);
                        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        newDateFormat.setTimeZone(TimeZone.getDefault());
                        date = newDateFormat.format(parsedDate);
                        time = date.substring(date.indexOf(":") + 1);
                        date = date.substring(0, date.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * TimeUnit.DAYS.toMillis(1)));
                            date = new SimpleDateFormat("yyyy-MM-dd").format(fragmentDate);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    homeTeam = matchData.getString(HOME_TEAM);
                    awayTeam = matchData.getString(AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = matchData.getString(MATCH_DAY);

                    ContentValues matchValues = new ContentValues();
                    matchValues.put(ScoresTable.MATCH_ID, matchId);
                    matchValues.put(ScoresTable.DATE_COL, date);
                    matchValues.put(ScoresTable.TIME_COL, time);
                    matchValues.put(ScoresTable.HOME_COL, homeTeam);
                    matchValues.put(ScoresTable.AWAY_COL, awayTeam);
                    matchValues.put(ScoresTable.HOME_GOALS_COL, homeGoals);
                    matchValues.put(ScoresTable.AWAY_GOALS_COL, awayGoals);
                    matchValues.put(ScoresTable.LEAGUE_COL, league);
                    matchValues.put(ScoresTable.MATCH_DAY, matchDay);

                    values.add(matchValues);
                }
            }

            // Save data
            ContentValues[] data = new ContentValues[values.size()];
            values.toArray(data);
            int insertedCount = context.getContentResolver().bulkInsert(ScoresTable.CONTENT_URI, data);
            Log.d(TAG, "Saved " + insertedCount + " items to the database");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}

