package barqsoft.footballscores.api;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import barqsoft.footballscores.ScoresApplication;
import barqsoft.footballscores.Util;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static barqsoft.footballscores.db.ScoresDBContract.ScoresTable;

public class ScoresUpdateService extends IntentService {
    private static final String TAG = "ScoresUpdateService";

    @Inject
    Retrofit mRetrofit;

    public ScoresUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((ScoresApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FootballDataApi footballDataApi = mRetrofit.create(FootballDataApi.class);

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

