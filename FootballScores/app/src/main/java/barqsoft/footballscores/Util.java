package barqsoft.footballscores;

import android.content.Context;
import android.text.format.Time;

import java.text.SimpleDateFormat;

public class Util {

    // League numbers for the 2016/2017 season
    public static final int CHAMPIONS_LEAGUE = 405;
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int BUNDESLIGA3 = 403;
    public static final int EREDIVISIE = 404;

    public static int getLeagueName(int leagueId) {
        switch (leagueId) {
            case SERIE_A:
                return R.string.serie_a;
            case PREMIER_LEAGUE:
                return R.string.premier_league;
            case CHAMPIONS_LEAGUE:
                return R.string.champions_league;
            case PRIMERA_DIVISION:
                return R.string.primera_division;
            case BUNDESLIGA1:
                return R.string.bundesliga;
            default:
                return R.string.league_unknown;
        }
    }

    public static int getMatchDay(int matchDay, int leagueId) {
        if (leagueId == CHAMPIONS_LEAGUE) {
            if (matchDay <= 6) {
                return R.string.group_stage_text;
            } else if (matchDay == 7 || matchDay == 8) {
                return R.string.first_knockout_round;
            } else if (matchDay == 9 || matchDay == 10) {
                return R.string.quarter_final;
            } else if (matchDay == 11 || matchDay == 12) {
                return R.string.semi_final;
            } else {
                return R.string.final_text;
            }
        } else {
            return R.string.match_day_text;
        }
    }

    public static String getScores(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return " - ";
        } else {
            return String.valueOf(homeGoals) + " - " + String.valueOf(awayGoals);
        }
    }

    public static int getTeamCrestByTeamName(String name) {
        if (name == null) return R.drawable.no_icon;

        switch (name) {
            //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }

    /**
     * Formats the name of the day relative to the current day. If the date is today, return
     * the localized version of "Today" instead of the actual day name. Likewise if it is
     * tomorrow or yesterday. Otherwise, the format is just the day of the week (e.g "Wednesday").
     *
     * @param context      An instance of the context, used to access string resources
     * @param dateInMillis A date represented in milliseconds
     * @return The name of the day either relative to today, or a day of the week
     */
    public static String getDayName(Context context, long dateInMillis) {
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else {
            return new SimpleDateFormat("EEEE").format(dateInMillis);
        }
    }
}
