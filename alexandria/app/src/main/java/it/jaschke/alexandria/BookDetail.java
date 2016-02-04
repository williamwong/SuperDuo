package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;

    private String mEan;
    private String mBookTitle;
    private ShareActionProvider mShareActionProvider;

    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mEan = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        View rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEan);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        setShareBookIntent();
    }

    private void setShareBookIntent() {
        // Check that options menu has been created and load has finished before setting intent
        if (mShareActionProvider != null && mBookTitle != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + mBookTitle);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(mEan)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        View view = getView();

        if (view != null) {
            mBookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
            ((TextView) view.findViewById(R.id.fullBookTitle)).setText(mBookTitle);

            String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
            ((TextView) view.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

            String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
            ((TextView) view.findViewById(R.id.fullBookDesc)).setText(desc);

            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            if (authors != null && !authors.equals("")) {
                String[] authorsArr = authors.split(",");
                ((TextView) view.findViewById(R.id.authors)).setLines(authorsArr.length);
                ((TextView) view.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
            }

            String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
            if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
                new DownloadImage((ImageView) view.findViewById(R.id.fullBookCover)).execute(imgUrl);
                view.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
            }

            String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
            ((TextView) view.findViewById(R.id.categories)).setText(categories);

            setShareBookIntent();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if (MainActivity.IS_TABLET && getView() != null && getView().findViewById(R.id.right_container) == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}