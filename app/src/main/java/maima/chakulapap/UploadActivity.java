package maima.chakulapap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import maima.chakulapap.AndroidMultiPartEntity.ProgressListener;
import maima.chakulapap.ViewPager.ProviderActivity;

public class UploadActivity extends Activity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private String filePath = null;
    private String providerName = null;

    private String food_Price = null;
    private String food_Item = null;
    private String food_Category = null;
    private String food_Size = null;

    private TextView txtPercentage;

    private TextView foodItem_;
    private TextView foodPrice_;
    private TextView foodCat_;
    private TextView foodSize_;

    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    private Toolbar tbar;

    private AppBarLayout appBar;
    private CollapsingToolbarLayout collapsingToolbar;
    long totalSize = 0;

    UserLocalStore userLocalStore;

    InputStream is = null;
    String line = null;
    String result = null;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);
        foodItem_ = (TextView) findViewById(R.id.txtItemName);
        foodPrice_ = (TextView) findViewById(R.id.txtItemPrice);
        foodCat_ = (TextView) findViewById(R.id.txtItemCategory);
        foodSize_ = (TextView) findViewById(R.id.txtItemSize);
        appBar = (AppBarLayout) findViewById(R.id.appbar);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity and food item values
        providerName = i.getStringExtra("provider_name");
        filePath = i.getStringExtra("filePath");
        food_Item = i.getStringExtra("foodItem");
        food_Price = i.getStringExtra("foodPrice");
        food_Category = i.getStringExtra("foodCategory");
        food_Size = i.getStringExtra("foodSize");


        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadFileToServer().execute();

            }
        });

    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            foodItem_.setVisibility(View.VISIBLE);
            foodItem_.setText(food_Item);

            foodPrice_.setVisibility(View.VISIBLE);
            foodPrice_.setText(food_Price);

            foodCat_.setVisibility(View.VISIBLE);
            foodCat_.setText(food_Category);

            foodSize_.setVisibility(View.VISIBLE);
            foodSize_.setText(food_Size);

            //Toast.makeText(this, "Provider name: "+providerName, Toast.LENGTH_SHORT).show();

            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);


        } else {
            imgPreview.setVisibility(View.GONE);

            foodItem_.setVisibility(View.GONE);
            foodPrice_.setVisibility(View.GONE);
            foodCat_.setVisibility(View.GONE);
            foodSize_.setVisibility(View.GONE);

            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("providerName", new StringBody(providerName));
                entity.addPart("food_Item", new StringBody(food_Item));
                entity.addPart("food_Price", new StringBody(food_Price));
                entity.addPart("food_Category", new StringBody(food_Category));
                entity.addPart("food_Size", new StringBody(food_Size));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(food_Item + " has been added successfully") //Was intially "builder.setMessage(message)" for testing purposes
                .setTitle("ChakulaPap")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Redirect back to add menu activity
                        Intent back_toPrevious = new Intent(getApplicationContext(), ProviderActivity.class);
                        back_toPrevious.putExtra("back_from_cam", "yes");
                        back_toPrevious.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(back_toPrevious);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
