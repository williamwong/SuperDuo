package barqsoft.footballscores;

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

public class ScoreListAdapter extends CursorAdapter {
    private static final String FOOTBALL_SCORES_HASHTAG = "#FootballScores";

    private static final int COL_DATE = 1;
    private static final int COL_MATCH_TIME = 2;
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_LEAGUE = 5;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_ID = 8;
    private static final int COL_MATCH_DAY = 9;

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

        holder.homeName.setText(cursor.getString(COL_HOME));
        holder.awayName.setText(cursor.getString(COL_AWAY));
        holder.date.setText(cursor.getString(COL_MATCH_TIME));
        holder.score.setText(Util.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        holder.matchId = cursor.getDouble(COL_ID);
        holder.homeCrest.setImageResource(Util.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
        holder.awayCrest.setImageResource(Util.getTeamCrestByTeamName(cursor.getString(COL_AWAY)));

        ViewGroup container = (ViewGroup) view.findViewById(R.id.match_detail_container);

        if (holder.matchId == mSelectedMatchId) {
            // If this view is the selected match, add detail view
            LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.fragment_match_detail, container, false);

            TextView matchDayTextView = (TextView) v.findViewById(R.id.matchday_text_view);
            TextView leagueTextView = (TextView) v.findViewById(R.id.league_text_view);
            Button shareButton = (Button) v.findViewById(R.id.share_button);

            matchDayTextView.setText(Util.getMatchDay(cursor.getInt(COL_MATCH_DAY),
                    cursor.getInt(COL_LEAGUE)));
            leagueTextView.setText(Util.getLeagueName(cursor.getInt(COL_LEAGUE)));
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareText = holder.homeName.getText() + " "
                            + holder.score.getText() + " "
                            + holder.awayName.getText() + " "
                            + FOOTBALL_SCORES_HASHTAG;
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    context.startActivity(shareIntent);
                }
            });

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            container.removeAllViews();
        }

    }

    public void setSelectedMatchId(double matchId) {
        mSelectedMatchId = matchId;
    }

    public static class ViewHolder {
        public final TextView homeName;
        public final TextView awayName;
        public final TextView score;
        public final TextView date;
        public final ImageView homeCrest;
        public final ImageView awayCrest;
        public double matchId;

        public ViewHolder(View view) {
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score = (TextView) view.findViewById(R.id.score_text_view);
            date = (TextView) view.findViewById(R.id.data_text_view);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        }
    }
}
