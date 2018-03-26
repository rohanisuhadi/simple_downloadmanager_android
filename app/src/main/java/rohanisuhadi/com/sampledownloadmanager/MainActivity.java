package rohanisuhadi.com.sampledownloadmanager;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnDownload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownload = (Button) findViewById(R.id.DownloadMusic);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri image_uri = Uri.parse("http://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
                DownloadManagerHelper downloadManagerHelper = new DownloadManagerHelper();
                downloadManagerHelper.downloadFile(image_uri,MainActivity.this,"Download File","Download file image");
            }
        });


    }



}
