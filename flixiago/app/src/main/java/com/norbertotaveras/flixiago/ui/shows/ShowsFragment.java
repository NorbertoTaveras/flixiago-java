
package com.norbertotaveras.flixiago.ui.shows;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.ShowActivity;
import com.norbertotaveras.flixiago.adapters.ShowsAdapter;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowCertification;
import com.norbertotaveras.flixiago.models.show.ShowCertificationsResponse;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCertificationsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowGenreLookupCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowsCallback;
import com.norbertotaveras.flixiago.services.shows.OnShowClickCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;


public class ShowsFragment
        extends
        Fragment
        implements
        PopupMenu.OnMenuItemClickListener,
        SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "ShowsFragment";

    ActivityInterface activityInterface;

    public interface ActivityInterface {
        void fragmentFailed(ShowsFragment fragment);
        void setShowActivityTitle(@StringRes int popular);
        void pressBack();
    }

    private RecyclerView showList;
    private TabLayout tabLayout;
    private ShowsAdapter showsAdapter;
    private OnGetShowsCallback getShowsCallback;
    private SearchView showSearchView;
    private MenuItem showSearchMenuItem;

    private boolean isFetching;
    private int currentPage = 1;
    private int fetchedPage = 0;
    private String sortBy = MovieDBApi.POPULAR;
    private HashMap<String, Integer> certificationLookup;

    private String currentQuery;
    private long currentGenreId = -1;
    private int currentCertificationLimit = Integer.MAX_VALUE;

    private LongSparseArray<Integer> certificationCache = new LongSparseArray<>();

    private boolean closing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activityInterface = (ActivityInterface)getActivity();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.shows_menu, menu);

        showSearchMenuItem = menu.findItem(R.id.search);
        showSearchView = (SearchView) showSearchMenuItem.getActionView();
        showSearchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        resetShowList(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shows, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (view == null) {
            activityInterface.fragmentFailed(this);
            return;
        }

        showList = view.findViewById(R.id.show_list);
        tabLayout = view.findViewById(R.id.tabLayout);
        actionBar.setElevation(0F);
        tabLayout.addOnTabSelectedListener(this);
        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        certificationLookup = new HashMap<>();

        movieDBApi.getShowCertifications(new OnGetShowCertificationsCallback() {
            @Override
            public void onSuccess(ShowCertificationsResponse response) {
                ArrayList<ShowCertification> certList =
                        response.getCertificationForCountry("US");

                for (ShowCertification cert: certList)
                    certificationLookup.put(cert.getCertification(), cert.getOrder());

                tabLayout.post(()-> {tabLayout.getTabAt(0).select();});
                setupShowListAdapter();
            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
    }

    private void setupShowListAdapter() {
        final ShowsFragment fragment = this;

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();
        movieDBApi.getShowGenreList(new OnGetShowGenreLookupCallback() {
            @Override
            public void onSuccess(HashMap<Long, String> showGenreLookup) {
                showsAdapter = new ShowsAdapter(
                        new ArrayList<Show>(), showGenreLookup,
                        certificationCache, certificationLookup, showClickCallback);
                showList.setAdapter(showsAdapter);

                showListOnScrollListener();

                resetShowList(null);
            }

            @Override
            public void onFailure(Throwable error) {
                activityInterface.fragmentFailed(fragment);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            /*case R.id.show_sort:
                showSortMenu();
                break; */

            case R.id.genre_filter:
                showGenreMenu();
                break;

            case R.id.certification_filter:
                certificationMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void showSortMenu() {
        Activity activity = getActivity();
        Context context = getContext();

        assert activity != null;

       /* PopupMenu showSortMenu = new PopupMenu(context, activity.findViewById(R.id.show_sort));

        forceEnableMenuIcons(showSortMenu);

        showSortMenu.setOnMenuItemClickListener(this);

        showSortMenu.inflate(R.menu.shows_sort_menu);

        showSortMenu.show(); */
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                sortBy = MovieDBApi.POPULAR;
                break;

            case 1:
                sortBy = MovieDBApi.TOP_RATED;
                break;

            case 2:
                sortBy = MovieDBApi.ON_THE_AIR;
                break;

            case 3:
                sortBy = MovieDBApi.AIRING_TODAY;
                break;

            default:
                Log.w(TAG, "Got unexpected menu item click");
                break;
        }

        if (!showSearchView.isIconified())
            showSearchMenuItem.collapseActionView();

        resetShowList(null);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.popular:
                sortBy = MovieDBApi.POPULAR;
                break;
            case R.id.top_rated:
                sortBy = MovieDBApi.TOP_RATED;
                break;
            case R.id.on_the_air:
                sortBy = MovieDBApi.ON_THE_AIR;
                break;
            case R.id.airing_today:
                sortBy = MovieDBApi.AIRING_TODAY;
                break;
            case R.id.show_latest:
                sortBy = MovieDBApi.LATEST;
                break;
            default:
                Log.w(TAG, "Got unexpected menu item click" + id);
                return false;
        }

        if (!showSearchView.isIconified())
            showSearchMenuItem.collapseActionView();

        resetShowList(null);
        return true;
    }

    public void showGenreMenu() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.e(TAG, "Activity is null!");
            return;
        }

        final View menuButton = activity.findViewById(R.id.genre_filter);

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        movieDBApi.getShowGenreList(new OnGetShowGenreLookupCallback() {
            @Override
            public void onSuccess(HashMap<Long, String> movieGenreList) {
                FormHelpers.showGenreMenu(new FormHelpers.OnDynamicMenuItemSelected() {
                    @Override
                    public void genreIdSelected(long genreId) {
                        // Set the current genre id to the item they cicked
                        currentGenreId = genreId;

                        // Reload the list
                        resetShowList(currentQuery);
                    }
                }, menuButton, movieGenreList, currentGenreId);
            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
    }

    static class CertificationPair {
        String certification;
        int order;

        public CertificationPair(String certification, Integer order) {
            this.certification = certification;
            this.order = order;
        }
    }

    public void certificationMenu() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.e(TAG, "Activity is null!");
            return;
        }

        final View menuButton = activity.findViewById(R.id.certification_filter);

        final ShowsFragment.CertificationPair[] items =
                new ShowsFragment.CertificationPair[certificationLookup.size() + 1];

        int i = 0;
        items[i++] = new CertificationPair("All", Integer.MAX_VALUE);

        for (Map.Entry<String, Integer> item: certificationLookup.entrySet())
            items[i++] = new CertificationPair(item.getKey(), item.getValue());

        Arrays.sort(items, (o1, o2) -> {
            if (o1.order == Integer.MAX_VALUE)
                return -1;
            if (o2.order == Integer.MAX_VALUE)
                return 1;
            return Integer.compare(o1.order, o2.order);
        });

        String[] optionTexts = new String[items.length];
        final Integer[] optionOrders = new Integer[items.length];

        int highightIndex = -1;
        for (i = 0; i < items.length; ++i) {
            optionTexts[i] = items[i].certification;
            optionOrders[i] = items[i].order;

            if (items[i].order == currentCertificationLimit)
                highightIndex = i;
        }

        final FormHelpers.DynamicMenu certificationMenu = new FormHelpers.DynamicMenu(
                menuButton, highightIndex, optionTexts);

        certificationMenu.show(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int index = certificationMenu.indexOf(item);
                currentCertificationLimit = optionOrders[index];
                resetShowList(currentQuery);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        closing = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        closing = true;
    }

    private void resetShowList(String query) {
        currentQuery = query;
        currentPage = 1;
        fetchedPage = 0;
        showsAdapter.clearShows();
        setActivityTitle();
        getShows(currentPage, ++lastGetShowsRequestSequence);
    }

    private void setActivityTitle() {
        if (currentQuery != null) {
            //activityInterface.setShowActivityTitle(R.string.search);
            //return;
        }

        switch (sortBy) {
            case MovieDBApi.POPULAR:
                //activityInterface.setShowActivityTitle(R.string.shows_popular);
                break;
            case MovieDBApi.TOP_RATED:
                //activityInterface.setShowActivityTitle(R.string.shows_top_rated);
                break;
            case MovieDBApi.ON_THE_AIR:
                //activityInterface.setShowActivityTitle(R.string.shows_on_the_air);
                break;
            case MovieDBApi.AIRING_TODAY:
                //activityInterface.setShowActivityTitle(R.string.shows_airing_today);
                break;
            case MovieDBApi.LATEST:
                //activityInterface.setShowActivityTitle(R.string.shows_latest);
                break;
        }

        activityInterface.setShowActivityTitle(R.string.shows_title_default);
    }

    private void forceEnableMenuIcons(PopupMenu movieSortMenu) {
        try {
            Method method = movieSortMenu.getMenu().getClass()
                    .getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(movieSortMenu.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LinearLayoutManager listLayoutManager(Context context) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)showList.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(context);
            showList.setLayoutManager(layoutManager);
        }
        return layoutManager;
    }

    private void showListOnScrollListener() {
        Context context = getContext();

        final LinearLayoutManager manager = listLayoutManager(context);

        showList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalShowCount = manager.getItemCount();
                int showVisibleCount = manager.getChildCount();
                int showFirstVisibleItem = manager.findFirstVisibleItemPosition();

                if (showFirstVisibleItem + showVisibleCount >= totalShowCount / 2) {
                    getShows(currentPage + 1, lastGetShowsRequestSequence);
                }
            }
        });
    }

    private void getShows(final int page, long requestSequence) {
        Log.v(TAG, "Getting shows page " + page);

        if (page <= fetchedPage)
            return;

        if (page != fetchedPage + 1)
            Log.e(TAG, "getPopular shows skipped pages!");

        fetchedPage = page;

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        isFetching = true;

        OnGetShowsCallback getShowsCallback = prepareGetShowsCallback(requestSequence);

        if (currentQuery == null)
            movieDBApi.getShows(page, sortBy, getShowsCallback);
        else
            movieDBApi.searchShows(page, currentQuery, getShowsCallback);
    }

    long lastGetShowsRequestSequence = 0;

    private OnGetShowsCallback prepareGetShowsCallback(final long requestSequence) {
        final ShowsFragment fragment = this;
        getShowsCallback = new OnGetShowsCallback() {
            @Override
            public void onSuccess(final int page, int totalPages, final ArrayList<Show> shows) {
                if (requestSequence != lastGetShowsRequestSequence) {
                    Log.v(TAG, "Got response for expired sequence number");
                    return;
                }

                if (page == 1)
                    showsAdapter.clearShows();

                showsAdapter.appendShows(shows, currentGenreId, currentCertificationLimit);

                currentPage = page;
                isFetching = false;

                showList.requestLayout();
                if (!closing && !showList.canScrollVertically(1) && page < totalPages)
                    getShows(page + 1, requestSequence);
            }

            @Override
            public void onFailure(Throwable error) {
                activityInterface.fragmentFailed(fragment);
            }
        };

        return getShowsCallback;
    }

    OnShowClickCallback showClickCallback = new OnShowClickCallback() {
        @Override
        public void onClick(Show show) {
            ShowActivity.show(getActivity(), show);
        }
    };

}