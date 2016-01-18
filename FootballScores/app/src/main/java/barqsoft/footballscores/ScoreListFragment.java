package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ScoreListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int SCORES_LOADER = 0;

    private ScoreListAdapter mAdapter;
    private String mFragmentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        View view = inflater.inflate(R.layout.fragment_score_list, container, false);

        final ListView scoreList = (ListView) view.findViewById(R.id.score_list);
        mAdapter = new ScoreListAdapter(getActivity(), null, 0);
        mAdapter.setSelectedMatchId(MainActivity.sSelectedMatchId);
        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ScoreListAdapter.ViewHolder holder = (ScoreListAdapter.ViewHolder) view.getTag();
                mAdapter.setSelectedMatchId(holder.matchId);
                MainActivity.sSelectedMatchId = (int) holder.matchId;
                mAdapter.notifyDataSetChanged();
            }
        });

        scoreList.setAdapter(mAdapter);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String[] args = {mFragmentDate};
        return new CursorLoader(getActivity(), ScoresDBContract.ScoresTable.getScoreWithDateUri(),
                null, null, args, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

    public void setFragmentDate(String date) {
        mFragmentDate = date;
    }
}
