package com.norbertotaveras.flixiago.ui.movies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.MovieActivity;
import com.norbertotaveras.flixiago.adapters.MoviesAdapter;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.movie.MovieCertification;
import com.norbertotaveras.flixiago.models.movie.MovieCertificationsResponse;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCertificationsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieGenreLookupCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMoviesCallback;
import com.norbertotaveras.flixiago.services.movie.OnMovieClickCallback;
import com.norbertotaveras.flixiago.services.movie.OnMovieToggleCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MoviesFragment
        extends
        Fragment
        implements
        SearchView.OnQueryTextListener,
        TabLayout.OnTabSelectedListener {

    private MoviesViewModel moviesViewModel;

    private static final String TAG = "MoviesFragment";

    ActivityInterface activityInterface;
    private boolean closing;

    private RecyclerView movieList;
    private MoviesAdapter movieAdapter;
    private TabLayout tabLayout;

    private SearchView searchView;
    private MenuItem searchMenuItem;

    private boolean isFetching;
    private int currentPage = 1;
    private int fetchedPage = 0;
    private String sortBy = MovieDBApi.POPULAR;
    private HashMap<String, Integer> certificationLookup;

    private String currentQuery;
    private long currentGenreId = -1;
    private int currentCertificationLimit = Integer.MAX_VALUE;

    private LongSparseArray<Integer> certificationCache = new LongSparseArray<>();

    public interface ActivityInterface {

        void fragmentFailed(MoviesFragment fragment);
        void setActivityTitle(@StringRes int popular);
        void pressBack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activityInterface = (ActivityInterface)getActivity();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.movies_menu, menu);

        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        moviesViewModel =
                new ViewModelProvider(this).get(MoviesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_movies, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        moviesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
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

        movieList = view.findViewById(R.id.movie_list);
        tabLayout = view.findViewById(R.id.tabLayout);
        actionBar.setElevation(0F);
        tabLayout.addOnTabSelectedListener(this);
        //tabLayout.selectTab(tabLayout.getTabAt(2), true);
        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        certificationLookup = new HashMap<>();

        movieDBApi.getMovieCertifications(new OnGetMovieCertificationsCallback() {
            @Override
            public void onSuccess(MovieCertificationsResponse response) {
                ArrayList<MovieCertification> certList =
                        response.getCertificationForCountry("US");

                for (MovieCertification cert: certList)
                    certificationLookup.put(cert.getCertification(), cert.getOrder());

                tabLayout.post(()-> {tabLayout.getTabAt(0).select();});

                setupMovieListAdapter();
            }

            @Override
            public void onFailure(Throwable error) { }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // This gets called very early, so early we might not
        // have a movieAdapter yet
        if (movieAdapter != null)
            resetMovieList(currentQuery);
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

    private void setupMovieListAdapter() {
        final MoviesFragment fragment = this;

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();
        movieDBApi.getMovieGenreList(new OnGetMovieGenreLookupCallback() {
            @Override
            public void onSuccess(HashMap<Long, String> movieGenreLookup) {
                movieAdapter = new MoviesAdapter(
                        new ArrayList<Movie>(), movieGenreLookup,
                        certificationCache, certificationLookup, movieClickCallback, movieToggleCallback);
                movieList.setAdapter(movieAdapter);

                movieListOnScrollListener();

                resetMovieList(null);
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
            case R.id.genre_filter:
                genreMenu();
                return true;

            case R.id.certification_sort:
                certificationMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static class CertificationPair {
        String certification;
        int order;

        public CertificationPair(String certification, Integer order) {
            this.certification = certification;
            this.order = order;
        }
    }

    public void genreMenu() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.e(TAG, "Activity is null!");
            return;
        }

        final View menuButton = activity.findViewById(R.id.genre_filter);

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        movieDBApi.getMovieGenreList(new OnGetMovieGenreLookupCallback() {
            @Override
            public void onSuccess(HashMap<Long, String> movieGenreList) {
                FormHelpers.showGenreMenu(new FormHelpers.OnDynamicMenuItemSelected() {
                    @Override
                    public void genreIdSelected(long genreId) {
                        // Set the current genre id to the item they cicked
                        currentGenreId = genreId;

                        // Reload the list
                        resetMovieList(currentQuery);
                    }
                }, menuButton, movieGenreList, currentGenreId);
            }

            @Override
            public void onFailure(Throwable error) {

            }
        });
    }

    public void certificationMenu() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.e(TAG, "Activity is null!");
            return;
        }

        final View menuButton = activity.findViewById(R.id.certification_sort);

        final CertificationPair[] items = new CertificationPair[certificationLookup.size() + 1];

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

        int highlightIndex = -1;
        for (i = 0; i < items.length; ++i) {
            optionTexts[i] = items[i].certification;
            optionOrders[i] = items[i].order;

            if (items[i].order == currentCertificationLimit)
                highlightIndex = i;
        }

        final FormHelpers.DynamicMenu certificationMenu = new FormHelpers.DynamicMenu(
                menuButton, highlightIndex, optionTexts);

        certificationMenu.show(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int index = certificationMenu.indexOf(item);
                currentCertificationLimit = optionOrders[index];
                resetMovieList(currentQuery);
                return true;
            }
        });
    }

    private void resetMovieList(String query) {
        currentQuery = query;
        currentPage = 1;
        fetchedPage = 0;
        movieAdapter.clearMovies();
        setActivityTitle();
        getMovies(currentPage, ++lastGetMoviesRequestSequence);
    }

    private void setActivityTitle() {
        if (currentQuery != null) {
            //activityInterface.setActivityTitle(R.string.search);
            //return;
        }

        switch (sortBy) {
            case MovieDBApi.POPULAR:
                //activityInterface.setActivityTitle(R.string.movies_popular);
                break;
            case MovieDBApi.TOP_RATED:
                //activityInterface.setActivityTitle(R.string.movies_top_rated);
                break;
            case MovieDBApi.UPCOMING:
                //activityInterface.setActivityTitle(R.string.movies_upcoming);
                break;
            case MovieDBApi.NOW_PLAYING:
                //activityInterface.setActivityTitle(R.string.movies_now_playing);
                break;
            case MovieDBApi.LATEST:
                //activityInterface.setActivityTitle(R.string.movies_latest);
                break;
        }
        activityInterface.setActivityTitle(R.string.movies_title_default);
    }

    private LinearLayoutManager listLayoutManager(Context context) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)movieList.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(context);
            movieList.setLayoutManager(layoutManager);
        }
        return layoutManager;
    }

    private void movieListOnScrollListener() {
        Context context = getContext();

        final LinearLayoutManager manager = listLayoutManager(context);

        movieList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalMovieCount = manager.getItemCount();
                int movieVisibleCount = manager.getChildCount();
                int movieFirstVisibleItem = manager.findFirstVisibleItemPosition();

                if (movieFirstVisibleItem + movieVisibleCount >= totalMovieCount / 2) {
                    getMovies(currentPage + 1, lastGetMoviesRequestSequence);
                }
            }
        });
    }

    private void getMovies(final int page, long requestSequence) {
        Log.v(TAG, "Getting movies page " + page);

        if (page <= fetchedPage)
            return;

        if (page != fetchedPage + 1)
            Log.e(TAG, "getPopular movies skipped pages!");

        fetchedPage = page;

        final MovieDBApi movieDBApi = MovieDBApi.getInstance();

        isFetching = true;

        OnGetMoviesCallback getMoviesCallback = prepareGetMoviesCallback(requestSequence);

        if (currentQuery == null)
            movieDBApi.getMovies(page, sortBy, getMoviesCallback);
        else
            movieDBApi.searchMovies(page, currentQuery, getMoviesCallback);
    }

    long lastGetMoviesRequestSequence = 0;

    private OnGetMoviesCallback prepareGetMoviesCallback(final long requestSequence) {
        final MoviesFragment fragment = this;

        return new OnGetMoviesCallback() {
            @Override
            public void onSuccess(final int page, int totalPages, final ArrayList<Movie> movies) {
                // See if we need to drop late response
                if (lastGetMoviesRequestSequence != requestSequence)
                    return;

                if (page == 1)
                    movieAdapter.clearMovies();

                movieAdapter.appendMovies(movies, currentGenreId, currentCertificationLimit);

                currentPage = page;
                isFetching = false;

                // If there are not enough items to need to scroll
                // and we got items on this page, then request more
                movieList.requestLayout();
                if (!closing && !movieList.canScrollVertically(1) && page < totalPages)
                    getMovies(page + 1, requestSequence);
            }

            @Override
            public void onFailure(Throwable error) {
                activityInterface.fragmentFailed(fragment);
            }
        };
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
                sortBy = MovieDBApi.UPCOMING;
                break;

            case 3:
                sortBy = MovieDBApi.NOW_PLAYING;
                break;

            default:
                Log.w(TAG, "Got unexpected menu item click");
                break;
        }

        if (!searchView.isIconified())
            searchMenuItem.collapseActionView();

        resetMovieList(null);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        resetMovieList(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    OnMovieClickCallback movieClickCallback = new OnMovieClickCallback() {
        @Override
        public void onClick(Movie movie) {
            MovieActivity.show(getActivity(), movie);
        }

        @Override
        public void onFavoriteClick(Movie movie) {
            FlixiagoDatabase database = FlixiagoDatabase.getInstance(getContext());
        }
    };

    OnMovieToggleCallback movieToggleCallback = new OnMovieToggleCallback() {
        @Override
        public void onToggle(Movie movie) {
        }
    };
}