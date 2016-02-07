package barqsoft.footballscores.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class FixtureData {

    @SerializedName("timeFrameStart")
    @Expose
    public String timeFrameStart;
    @SerializedName("timeFrameEnd")
    @Expose
    public String timeFrameEnd;
    @SerializedName("count")
    @Expose
    public int count;
    @SerializedName("fixtures")
    @Expose
    public final List<Fixture> fixtures = new ArrayList<Fixture>();

    @Generated("org.jsonschema2pojo")
    public class Fixture {

        @SerializedName("_links")
        @Expose
        public Links links;
        @SerializedName("date")
        @Expose
        public Date date;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("matchday")
        @Expose
        public int matchday;
        @SerializedName("homeTeamName")
        @Expose
        public String homeTeamName;
        @SerializedName("awayTeamName")
        @Expose
        public String awayTeamName;
        @SerializedName("result")
        @Expose
        public Result result;

        public int getLeagueId() {
            return Integer.parseInt(links.soccerseason.href
                    .replace("http://api.football-data.org/alpha/soccerseasons/", ""));
        }

        public int getMatchId() {
            return Integer.parseInt(links.self.href
                    .replace("http://api.football-data.org/alpha/fixtures/", ""));
        }

        public String getDate() {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        public String getTime() {
            return new SimpleDateFormat("HH:mm").format(date);
        }

    }

    @Generated("org.jsonschema2pojo")
    public class Links {

        @SerializedName("self")
        @Expose
        public Link self;
        @SerializedName("soccerseason")
        @Expose
        public Link soccerseason;
        @SerializedName("homeTeam")
        @Expose
        public Link homeTeam;
        @SerializedName("awayTeam")
        @Expose
        public Link awayTeam;

    }

    @Generated("org.jsonschema2pojo")
    public class Result {

        @SerializedName("goalsHomeTeam")
        @Expose
        public int goalsHomeTeam;
        @SerializedName("goalsAwayTeam")
        @Expose
        public int goalsAwayTeam;

    }

    @Generated("org.jsonschema2pojo")
    public class Link {

        @SerializedName("href")
        @Expose
        public String href;

    }

}

