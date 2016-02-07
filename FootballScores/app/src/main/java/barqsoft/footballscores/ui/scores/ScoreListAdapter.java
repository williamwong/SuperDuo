package barqsoft.footballscores.ui.scores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Util;
import barqsoft.footballscores.db.ScoresDBContract.ScoresTable;

public class ScoreListAdapter extends CursorAdapter {
    private static final String FOOTBALL_SCORES_HASHTAG = "#FootballScores";

    private double mSelectedMatchId;

    public ScoreListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View scoreListItem = LayoutInflater.from(context)
                .inflate(R.layout.list_item_score, parent, false);
        ViewHolder holder = new ViewHolder(scoreListItem);
        scoreListItem.setTag(holder);
        return scoreListItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        final int matchIdColumnIndex = cursor.getColumnIndex(ScoresTable.MATCH_ID);
        final int homeColumnIndex = cursor.getColumnIndex(ScoresTable.HOME_COL);
        final int awayColumnIndex = cursor.getColumnIndex(ScoresTable.AWAY_COL);
        final int timeColumnIndex = cursor.getColumnIndex(ScoresTable.TIME_COL);
        final int homeGoalsColumnIndex = cursor.getColumnIndex(ScoresTable.HOME_GOALS_COL);
        final int awayGoalsColumnIndex = cursor.getColumnIndex(ScoresTable.AWAY_GOALS_COL);
        final int matchDayColumnIndex = cursor.getColumnIndex(ScoresTable.MATCH_DAY);
        final int leagueColumnIndex = cursor.getColumnIndex(ScoresTable.LEAGUE_COL);

        holder.matchId = cursor.getDouble(matchIdColumnIndex);

        holder.homeName.setText(cursor.getString(homeColumnIndex));
        holder.homeName.setContentDescription(context.getString(R.string.a11y_home_name, holder.homeName.getText()));

        holder.awayName.setText(cursor.getString(awayColumnIndex));
        holder.awayName.setContentDescription(context.getString(R.string.a11y_away_name, holder.awayName.getText()));

        holder.time.setText(cursor.getString(timeColumnIndex));
        holder.time.setContentDescription(context.getString(R.string.a11y_match_date, holder.time.getText()));

        holder.score.setText(Util.getScores(cursor.getInt(homeGoalsColumnIndex), cursor.getInt(awayGoalsColumnIndex)));
        holder.score.setContentDescription(context.getString(R.string.a11y_match_score, holder.score.getText()));

        holder.homeCrest.setImageResource(Util.getTeamCrestByTeamName(cursor.getString(homeColumnIndex)));
        holder.homeCrest.setContentDescription(context.getString(R.string.a11y_home_crest, holder.homeName.getText()));

        holder.awayCrest.setImageResource(Util.getTeamCrestByTeamName(cursor.getString(awayColumnIndex)));
        holder.awayCrest.setContentDescription(context.getString(R.string.a11y_away_crest, holder.awayName.getText()));

        if (holder.matchId == mSelectedMatchId) {
            // If this view is the selected match, add detail view
            LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.fragment_match_detail, holder.container, false);

            TextView matchDayTextView = (TextView) v.findViewById(R.id.matchday_text_view);
            TextView leagueTextView = (TextView) v.findViewById(R.id.league_text_view);
            Button shareButton = (Button) v.findViewById(R.id.share_button);

            matchDayTextView.setText(context.getString(
                    Util.getMatchDay(cursor.getInt(matchDayColumnIndex), cursor.getInt(leagueColumnIndex)),
                    String.valueOf(cursor.getInt(matchDayColumnIndex))));
            matchDayTextView.setContentDescription(context.getString(
                    R.string.a11y_match_day, matchDayTextView.getText()));

            leagueTextView.setText(context.getString(Util.getLeagueName(cursor.getInt(leagueColumnIndex))));
            leagueTextView.setContentDescription(context.getString(R.string.a11y_league_name, leagueTextView.getText()));

            shareButton.setContentDescription(context.getString(R.string.a11y_share_button));
            shareButton.setOnClickListener(v1 -> {
                String shareText = holder.homeName.getText() + " "
                        + holder.score.getText() + " "
                        + holder.awayName.getText() + " "
                        + FOOTBALL_SCORES_HASHTAG;
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                context.startActivity(shareIntent);
            });

            holder.container.addView(v, 0, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            holder.container.removeAllViews();
        }

    }

    public void setSelectedMatchId(double matchId) {
        mSelectedMatchId = matchId;
    }

    public static class ViewHolder {
        public final TextView homeName;
        public final TextView awayName;
        public final TextView score;
        public final TextView time;
        public final ImageView homeCrest;
        public final ImageView awayCrest;
        public final ViewGroup container;
        public double matchId;

        public ViewHolder(View view) {
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score = (TextView) view.findViewById(R.id.score_text_view);
            time = (TextView) view.findViewById(R.id.time_text_view);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
            container = (ViewGroup) view.findViewById(R.id.match_detail_container);
        }
    }
}
