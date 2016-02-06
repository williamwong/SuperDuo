package barqsoft.footballscores;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wwong on 2/6/2016.
 */
public interface FootballDataApi {
    @GET("fixtures")
    Observable<FixtureData> getFixtures(@Query("timeFrame") String timeFrame);
}
