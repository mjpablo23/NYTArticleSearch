package com.insequence.newyorktimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.insequence.newyorktimessearch.Article;
import com.insequence.newyorktimessearch.ArticleArrayAdapter;
import com.insequence.newyorktimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

//    EditText etQuery;
//    GridView gvResults;
//    Button btnSearch;

    @BindView(R.id.etQuery) EditText etQuery;
    @BindView(R.id.gvResults) GridView gvResults;
    @BindView(R.id.btnSearch) Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

//    @OnClick(R.id.gvResults)
//    public void

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
//        etQuery = (EditText) findViewById(R.id.etQuery);
//        gvResults = (GridView) findViewById(R.id.gvResults);
//        btnSearch = (Button) findViewById(R.id.btnSearch);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

//        hi guys, i started assignment 2, and towards the end of the tutorial video, i’m having trouble with setting the onClickListener.  ›

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

                // pass in that article into intent
//                i.putExtra("url", article.getWebUrl());
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // NY Times search api: Here's your API Key for the Article Search API: 0093eba3ab344318ab5be88dd94f91e0
    // normal api call with api key
    // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=0093eba3ab344318ab5be88dd94f91e0
    // with query for android topics
    // // https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=0093eba3ab344318ab5be88dd94f91e0&q=android

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        // toast makes message show up then disappear
        // Toast.makeText(this, "searching for " + query, Toast.LENGTH_LONG).show();

        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        // request handler
        RequestParams params = new RequestParams();
        params.put("api-key", "0093eba3ab344318ab5be88dd94f91e0"); // my api key
        params.put("page", 0);
        params.put("q", query);  // search term

        // network request.  url request.  response handler is the json http response handler
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
}
