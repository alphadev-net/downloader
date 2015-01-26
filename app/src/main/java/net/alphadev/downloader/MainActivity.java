/**
 * Copyright © 2013-2015 Jan Seeger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.alphadev.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
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
    private DownloadManager dlManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dlManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        final TextView urlText = (TextView) findViewById(R.id.url_text);
        final View downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                final Request request = getRequest(urlText);
                if (request != null) {
                    dlManager.enqueue(request);
                }
            }
        });

        final Intent intent = getIntent();
        if (intent != null) {
            urlText.setText(intent.getDataString());
        }
    }

    private Request getRequest(TextView urlText) {
        final Uri uri = Uri.parse(urlText.getText().toString());
        final String filename = uri.getLastPathSegment();

        if (filename == null) {
            Toast.makeText(this, "Are you sure you got the URL right? This is not a scraper!", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            final Request request = new Request(uri)
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
