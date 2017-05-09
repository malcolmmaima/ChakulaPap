package maima.chakulapap.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import maima.chakulapap.Config;
import maima.chakulapap.R;
import maima.chakulapap.UploadActivity;
import maima.chakulapap.UserLocalStore;
import maima.chakulapap.ViewPager.ProviderActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentAddMenu extends Fragment {
    UserLocalStore userLocalStore;

    // LogCat tag
    private static final String TAG = FragmentAddMenu.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnAddItem;
    private TextView FoodItem, FoodPrice, etFoodItem, etFoodPrice;

    private Spinner foodCategory, foodSize;
    private ProviderActivity providerActivity;

    public FragmentAddMenu() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        userLocalStore = new UserLocalStore(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_menu, container, false);
        // Inflate the layout for this fragment

        //Buttons
        btnCapturePicture = (Button) view.findViewById(R.id.btnCapturePicture);

        //TextViews
        FoodItem = (TextView) view.findViewById(R.id.etFoodItem);
        FoodPrice = (TextView) view.findViewById(R.id.etFoodPrice);
        etFoodItem = (TextView) view.findViewById(R.id.etFoodItem);
        etFoodPrice = (TextView) view.findViewById(R.id.etFoodPrice);


        //Spinners
        foodCategory = (Spinner) view.findViewById(R.id.category_spinner);
        foodSize = (Spinner) view.findViewById(R.id.size_spinner);


        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

        if(CheckFields()){
                // capture picture
                captureImage();
            }

        else {
            Toast.makeText(getContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
            }
        }
        });


        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            getActivity().finish();
        }


        //Our category drop down list
        Spinner staticSpinner = (Spinner) view.findViewById(R.id.category_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getContext(), R.array.category_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);



        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }


        });

        //Our size drop down list
        Spinner item_size = (Spinner) view.findViewById(R.id.size_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> item_size_adapter = ArrayAdapter
                .createFromResource(getContext(), R.array.size_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        item_size_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        item_size.setAdapter(item_size_adapter);



        item_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }


        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
        /**
         * Checking device has camera hardware or not
         * */
        private boolean isDeviceSupportCamera() {
            if (getContext().getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA)) {
                // this device has a camera
                return true;
            } else {
                // no camera on this device
                return false;
            }
        }

        /**
         * Launching camera app to capture image
         */
        private void captureImage() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }

        /**
         * Launching camera app to record video
         */
        private void recordVideo() {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

            // set video quality
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
            // name

            // start the video capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }

        /**
         * Here we store the file url as it will be null after returning from camera
         * app
         */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                // save file url in bundle as it will be null on screen orientation
                // changes
                savedInstanceState.putParcelable("file_uri", fileUri);
            }
        }
/*
        @Override
        protected void onViewStateRestored(Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);

            // get the file url
            fileUri = savedInstanceState.getParcelable("file_uri");
        }
*/


        /**
         * Receiving activity result method will be called after closing the camera
         * */
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // if the result is capturing Image
            if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    // successfully captured the image
                    // launching upload activity
                    launchUploadActivity(true);


                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled Image capture
                    Toast.makeText(getContext(),
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to capture image
                    Toast.makeText(getContext(),
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }

            } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                    // video successfully recorded
                    // launching upload activity
                    launchUploadActivity(false);

                } else if (resultCode == RESULT_CANCELED) {

                    // user cancelled recording
                    Toast.makeText(getContext(),
                            "User cancelled video recording", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    // failed to record video
                    Toast.makeText(getContext(),
                            "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        /*
        * As we launch UploadActivity, we need to grab the info entered in this Fragment/Activity and pass it to
        * the Upload activity which will deal with uploading image + food content
        * */
        private void launchUploadActivity(boolean isImage){
            String provName = userLocalStore.getLoggedInUser().name;
            Intent i = new Intent(getContext(), UploadActivity.class);
            i.putExtra("provider_name", provName);
            i.putExtra("filePath", fileUri.getPath());
            i.putExtra("isImage", isImage);
            i.putExtra("foodItem", FoodItem.getText().toString());
            i.putExtra("foodPrice", FoodPrice.getText().toString());
            i.putExtra("foodCategory", foodCategory.getSelectedItem().toString());
            i.putExtra("foodSize", foodSize.getSelectedItem().toString());
            startActivity(i);
        }

        /**
         * ------------ Helper Methods ----------------------
         * */

        /**
         * Creating file uri to store image/video
         */
        public Uri getOutputMediaFileUri(int type) {
            return Uri.fromFile(getOutputMediaFile(type));
        }

        /**
         * returning image / video
         */
        private static File getOutputMediaFile(int type) {

            // External sdcard location
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    Config.IMAGE_DIRECTORY_NAME);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Oops! Failed create "
                            + Config.IMAGE_DIRECTORY_NAME + " directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else {
                return null;
            }

            return mediaFile;
        }

    /**
     * Lets make sure user has entered the data before we allow them to press on upload picture button
     * */
    public boolean CheckFields(){

        boolean valid = true;

        if (etFoodItem.getText().toString().equals("")) {
            etFoodItem.setError("Can't be Empty");
            valid = false;
        }

        else if (etFoodPrice.getText().toString().equals("")) {
            etFoodPrice.setError("Can't be Empty");
            valid = false;
        }

        else if(foodCategory.getSelectedItem().toString().equals("")){
            Toast.makeText(getContext(), "Item category cannot be empty", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if(foodSize.getSelectedItem().toString().equals("")){
            Toast.makeText(getContext(), "Item size cannot be empty", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    }


