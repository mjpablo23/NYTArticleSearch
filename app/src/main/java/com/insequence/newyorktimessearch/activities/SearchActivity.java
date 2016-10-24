package com.insequence.newyorktimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.insequence.newyorktimessearch.Article;
import com.insequence.newyorktimessearch.ArticleArrayAdapter;
import com.insequence.newyorktimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.gvResults) GridView gvResults;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    // stuff for filter
    private final int REQUEST_CODE_FILTER = 20;
    Boolean filterOn = false;
    boolean checkArts = false;
    boolean checkFashion = false;
    boolean checkSports = false;
    private int year = 0, month = 0, day = 0;
    private String spinnerResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    // use butterknife here (try after initial setup?)
    public void setupViews() {
        // setup with butterknife
        ButterKnife.bind(this);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);


        // people from codepath class
        // pragyan.bits@gmail.com -- riverbed, sunnyvale,
        // pritish.karanjkar@gmail.com -- ebay, has many ideas

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                // get the article to display
                Article article = articles.get(position);

                i.putExtra("article", article);
                // launch activity
                startActivity(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem filterItem = menu.findItem(R.id.filter);
        System.out.println("filter button created");

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getApplicationContext(), FilterActivity.class);
                System.out.println("start filter activity");

                startActivityForResult(i, REQUEST_CODE_FILTER);
                return true;
            }
        });

        // search menu
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                //fetchBooks(query);
                sendQuery(query);

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FILTER) {
            // Extract name value from result extras
            filterOn = true;
            checkArts = data.getExtras().getBoolean("checkArts");
            checkFashion = data.getExtras().getBoolean("checkFashion");
            checkSports = data.getExtras().getBoolean("checkSports");
            spinnerResult = data.getExtras().getString("spinnerResult");
            year = data.getExtras().getInt("year");
            month = data.getExtras().getInt("month");
            day = data.getExtras().getInt("day");
            String dateStr = "mm/dd/yyyy: " + month + "" + day + "" + year;
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "spinner:" + spinnerResult + dateStr, Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // instructions:  https://developer.nytimes.com/article_search_v2.json#/README
    // NY Times search api: Here's your API Key for the Article Search API: 0093eba3ab344318ab5be88dd94f91e0
    // normal api call with api key
    // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=0093eba3ab344318ab5be88dd94f91e0
    // with query for android topics
    // // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=0093eba3ab344318ab5be88dd94f91e0&q=android

//    &facet_field=day_of_week
//    By default, facet counts ignore all filters and return the count for all results of a query. For the following queries, the facet count in each response will be identical, even though the results returned in one set is restricted to articles published in 2012:
//    q=obama&facet_field=source&begin_date=20120101&end_date=20121231
//    q=obama&facet_field=source

//    Filter Query Examples
//
//    Restrict your search to articles with The New York Times as the source:
//
//            &fq=source:("The New York Times")
//    Restrict your search to articles from either the Sports or Foreign desk:
//
//            &fq=news_desk:("Sports" "Foreign")
//    Restrict your search to articles that are about New York City and from the Sports desk:
//
//            &fq=news_desk:("Sports") AND glocations:("NEW YORK CITY")

//    public void onArticleSearch(View view) {
//        String query = etQuery.getText().toString();
//        sendQuery(query);
//    }

    public void sendQuery(String query) {

        // https://developer.android.com/guide/topics/ui/notifiers/toasts.html#Basics
        if (!isOnline() || !isNetworkAvailable()) {
            Context context = getApplicationContext();
            CharSequence text = "Not online";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
//            Toast.makeText(app.getBaseContext(), "Not Online");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        // request handler
        RequestParams params = new RequestParams();
        params.put("api-key", "0093eba3ab344318ab5be88dd94f91e0"); // my api key
        params.put("page", 0);
        params.put("q", query);  // search term

        if (filterOn) {
            // try query for begin date
            String begin_date = String.format("%04d%02d%02d", year, month, day);
            Log.d("Debug date", begin_date);
            // params.put("begin_date", "20161001");
            params.put("begin_date", begin_date);
            // params.put("end_date", "20161020");
            params.put("sort", spinnerResult);

            String news_desk_string = "news_desk:(";
            if (checkArts) {
                news_desk_string += "\"Arts\"";
            }
            if (checkSports) {
                news_desk_string += "\"Sports\"";
            }
            if (checkFashion) {
                news_desk_string += "\"Fashion & Style\"";
            }

            news_desk_string += ")";
            Log.d("Debug news desk", news_desk_string);

            params.put("fq", news_desk_string);
        }
        // network request.  url request.  response handle is the json http response handler
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Debug", response.toString());
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    // adapter.notifyDataSetChanged();  // don't need this since adding in adapter
                    Log.d("Debug", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
