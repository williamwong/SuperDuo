package barqsoft.footballscores.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Football-Data API interface for Retrofit
 */
interface FootballDataApi {
    @GET("fixtures")
    Observable<FixtureData> getFixtures(@Query("timeFrame") String timeFrame);
}
