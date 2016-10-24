package com.insequence.newyorktimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.insequence.newyorktimessearch.Article;
import com.insequence.newyorktimessearch.R;

public class ArticleActivity extends AppCompatActivity {

    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Article article = (Article) getIntent().getParcelableExtra("article");
        WebView webView = (WebView) findViewById(R.id.wvArticle);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        url = article.getWebUrl();
        webView.loadUrl(article.getWebUrl());
    }

    // http://stackoverflow.com/questions/19358510/why-menuitemcompat-getactionprovider-returns-null
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        MenuItem shareItem = menu.findItem(R.id.share);
        final ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

              // get reference to WebView
                WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
                // pass in the URL currently being used by the WebView
                shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

                miShare.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);
    }
}