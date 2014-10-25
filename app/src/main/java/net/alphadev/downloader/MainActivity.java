package net.alphadev.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final TextView urlText = (TextView) findViewById(R.id.url_text);
        final View downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                final DownloadManager.Request req = getRequest(urlText);
                download(req);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            urlText.setText(intent.getDataString());
        }
    }

    private void download(DownloadManager.Request request) {
        if (request != null) {
            final DownloadManager dlManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dlManager.enqueue(request);
        }
    }

    private DownloadManager.Request getRequest(TextView urlText) {
        final Uri uri = Uri.parse(urlText.getText().toString());
        final String filename = uri.getLastPathSegment();

        if (filename == null) {
            Toast.makeText(this, "Are you sure you got the URL right? This is not a scraper!", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(uri)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                    .setAllowedOverRoaming(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                request.setAllowedOverMetered(true);
            }

            return request;
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
