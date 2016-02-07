package barqsoft.footballscores;

import android.app.Application;
import android.content.Intent;

import barqsoft.footballscores.api.ApiModule;
import barqsoft.footballscores.api.ScoresUpdateService;

/**
 * Custom Application class
 */
public class ScoresApplication extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule("http://api.football-data.org/alpha/"))
                .build();

        startService(new Intent(this, ScoresUpdateService.class));
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
