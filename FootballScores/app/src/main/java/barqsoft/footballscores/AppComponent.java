package barqsoft.footballscores;

import javax.inject.Singleton;

import barqsoft.footballscores.api.ApiModule;
import barqsoft.footballscores.api.ScoresUpdateService;
import dagger.Component;

/**
 * Application level component
 */
@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface AppComponent {
    void inject(ScoresUpdateService scoresUpdateService);
}
