package rohanisuhadi.com.sampledownloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/*
TO DO
1. Download File
2. Set notification
3. Get status and location file Download Finish
4. Move file
 */


/**
 * Created by yanto on 3/22/18.
 */

public class DownloadManagerHelper {

    private DownloadManager downloadManager;

    private long byteMusicDownload;

    public void downloadFile(Uri url, Context context, String title, String discription){
        byteMusicDownload=downloadFileFromUrl(url,context, title, discription);
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);
    }

    //download file and set notification
    private long downloadFileFromUrl (Uri uri, Context context, String title, String discription) {
        long downloadReference;

        downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);


        //Setting title of request
        request.setTitle(title);

        //Setting description of request
        request.setDescription(discription);

        File rootDirectory = new File(context.getExternalFilesDir(null).getAbsoluteFile().toString());
        if(!rootDirectory.exists()){
            rootDirectory.mkdirs();
        }


        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(title)
                .setDescription(discription)
//                .setDestinationInExternalFilesDir(context,null ,"/" + "images12.jpg");
                .setDestinationInExternalFilesDir(context, "audiobookChapters/", "test.jpg");

        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

//    ceck status download finish
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            Toast toast = Toast.makeText(context,
                    "Image Download Complete", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
            ceckDownloadSatus(byteMusicDownload,context);
        }
    };

    private void ceckDownloadSatus(long Image_DownloadId, Context context) {

        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(Image_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if(cursor.moveToFirst()){
            DownloadStatus(cursor, Image_DownloadId, context);
        }


    }


    private void DownloadStatus(Cursor cursor, long DownloadId, Context context){

//        column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
//        column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);

//        get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

//        show message ceck
        Toast toast = Toast.makeText(context,
                "Image Download Status:"+ "\n" + statusText + "\n" +
                        reasonText,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

        if (statusText.equals("STATUS_SUCCESSFUL")){
            if (!moveFile(filename, context,"nama.mp3").equals(null)){

            }
        }

        // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 3000);
    }

//  move file
    private String moveFile(String lokasi, Context context, String namaFile){
        Log.v("URL",lokasi);
        File sourceLocation = new File(lokasi);
        Log.v("Source", sourceLocation.toString());
        File path = new File(context.getFilesDir(), "audiobookChapters");
        path.mkdirs();
        File fileTujuan = new File(path, namaFile);

        Log.v("Tujuan", fileTujuan.toString());
        InputStream in = null;
        OutputStream out = null;

        try {
            if(sourceLocation.renameTo(fileTujuan)){
                Log.v("Download", "Move file successful");
            }
            else{
                Log.v("Download", "Move file failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//      Copy the bits from instream to outstream
        try{
            if(sourceLocation.exists()){

                in = new FileInputStream(sourceLocation);
                out = new FileOutputStream(fileTujuan);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
                Log.v("Download", "Copy file successful.");
                if(sourceLocation.delete()){
                    System.out.println(sourceLocation.getName() + " is deleted!");
                }else{
                    System.out.println("Delete operation is failed.");
                }
                return fileTujuan.toString();

            }else{
                Log.v("Download", "Copy file failed. Source file missing.");
                return null;
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
