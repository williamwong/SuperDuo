package barqsoft.footballscores.api;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.Util;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static barqsoft.footballscores.db.ScoresDBContract.ScoresTable;

public class UpdateScoreService extends IntentService {
    private static final String TAG = "UpdateScoreService";

    public UpdateScoreService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Retrofit retrofit = getRetrofit();

        FootballDataApi footballDataApi = retrofit.create(FootballDataApi.class);

        Observable.merge(footballDataApi.getFixtures("n2"), footballDataApi.getFixtures("p2"))
                .flatMap(fixtureData -> Observable.from(fixtureData.fixtures))
                .filter(this::filterByLeague)
                .map(this::toContentValues)
                .doOnNext(contentValues -> Log.d(TAG, "onHandleIntent: " + contentValues.toString()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveValues,
                        throwable -> Log.d(TAG, "onHandleIntent: " + throwable.toString()),
                        () -> Log.d(TAG, "onHandleIntent: Completed"));

    }

    @NonNull
    private Retrofit getRetrofit() {
        Interceptor authInterceptor = chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY)
                    .build();
            return chain.proceed(request);
        };

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://api.football-data.org/alpha/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private boolean filterByLeague(FixtureData.Fixture fixture) {
        int leagueId = fixture.getLeagueId();

        return leagueId == Util.SERIE_A ||
                leagueId == Util.BUNDESLIGA1 ||
                leagueId == Util.PREMIER_LEAGUE ||
                leagueId == Util.PRIMERA_DIVISION ||
                leagueId == Util.CHAMPIONS_LEAGUE;
    }

    private ContentValues toContentValues(FixtureData.Fixture fixture) {
        ContentValues matchValues = new ContentValues();
        matchValues.put(ScoresTable.MATCH_ID, fixture.getMatchId());
        matchValues.put(ScoresTable.DATE_COL, fixture.getDate());
        matchValues.put(ScoresTable.TIME_COL, fixture.getTime());
        matchValues.put(ScoresTable.HOME_COL, fixture.homeTeamName);
        matchValues.put(ScoresTable.AWAY_COL, fixture.awayTeamName);
        matchValues.put(ScoresTable.HOME_GOALS_COL, fixture.result.goalsHomeTeam);
        matchValues.put(ScoresTable.AWAY_GOALS_COL, fixture.result.goalsAwayTeam);
        matchValues.put(ScoresTable.LEAGUE_COL, fixture.getLeagueId());
        matchValues.put(ScoresTable.MATCH_DAY, fixture.matchday);

        return matchValues;
    }

    private void saveValues(List<ContentValues> values) {
        ContentValues[] data = new ContentValues[values.size()];
        values.toArray(data);
        int insertedCount = getApplicationContext().getContentResolver().bulkInsert(ScoresTable.CONTENT_URI, data);
        Log.d(TAG, "Saved " + insertedCount + " items to the database");
    }
}

