package com.ibm.mysampleapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mysampleapp.R;
import com.ibm.mysampleapp.camera.CameraHelper;
import com.ibm.mysampleapp.camera.GalleryHelper;
import com.ibm.mysampleapp.gestures.OnSwipeTouchListener;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Face;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
        public static final String TAG = "MainActivity";
	private GalleryHelper galleryHelper;
	private ImageView loadedImage;
        private File imageFile;
        private VisualRecognition service;
        private VisualClassification response;
        private final String API_KEY = "20908a8b0ad9f1e2221037d62de08820bf1053b6";
        private CameraHelper cameraHelper;
        private ArrayAdapter<String> classifyAdapter;
        private ArrayAdapter<String>  faceDetectAdapter;
//        private StringBuilder classifyList;
//        private StringBuilder faceDetectList;
        private List<String> classifyList;
        private List <String> faceDetectList;
        private DetectedFaces detectFaces;
        private ListView faceListView;
        private ListView classifyListView;
        ProgressDialog progressDialog;
        private  SwipeRefreshLayout swipeView;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
         Log.d(TAG, "onCreate fired...");
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
//	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//	setSupportActionBar(toolbar);

        service = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20
        );
        Log.d(TAG, "service = " + service);
        service.setApiKey(API_KEY);
         Log.d(TAG, "API = " + R.string.visualrecognitionApi_key);

        galleryHelper = new GalleryHelper(this);
        cameraHelper = new CameraHelper(this);
        loadedImage = (ImageView) findViewById(R.id.loadedImage);
        faceListView = (ListView) findViewById(R.id.listOfFaceDetect);
        classifyListView = (ListView) findViewById(R.id.listOfClassify);
        classifyList = new ArrayList<>();
//        classifyList.add("Classified");
        faceDetectList = new ArrayList<>();
//        faceDetectList.add("Facial");

       swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeView);
       swipeView.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
//        swipeView.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        swipeView.setRefreshing(true);
//                                            try {
//                                                    processImage(imageFile);
//                                            } catch (SSLException e) {
//                                                    e.printStackTrace();
//                                            }
//                                    }
//                                }
//        );
       final ImageButton fab = (ImageButton) findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View view) {
		    galleryHelper.dispatchGalleryIntent();
	    }
	});

//            FloatingActionButton cameraFab = (FloatingActionButton) findViewById(R.id.cameraFab);
                ImageButton cameraFab = (ImageButton) findViewById(R.id.cameraFab);
                cameraFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Log.d(TAG, "onClick for cameraFab started");
                        cameraHelper.dispatchTakePictureIntent();
                        Log.d(TAG, "onClick for cameraFab ended");
                }
        });

                RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);

                rl.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                        public void onSwipeTop() {
                                 fab.setVisibility(View.INVISIBLE);
//                                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                        }
                        public void onSwipeRight() {
//                                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                                cameraHelper.dispatchTakePictureIntent();
                        }
                        public void onSwipeLeft() {
//                                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                        }
                        public void onSwipeBottom() {
                                fab.setVisibility(View.VISIBLE);
//                                Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                                try {
                                        processImage(imageFile);
                                } catch (SSLException e) {
                                        e.printStackTrace();
                                }
//                                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                        }

                });
                findViewById(R.id.facialLayout).setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
                         public void onSwipeTop() {
                                 Log.d(TAG, "onSwipeTop is fired (Facial Layout)");
                                 fab.setVisibility(View.INVISIBLE);
//                                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                        }
                        public void onSwipeRight() {
//                                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//                                cameraHelper.dispatchTakePictureIntent();
                        }
                        public void onSwipeLeft() {
//                                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                        }
                        public void onSwipeBottom() {
                                fab.setVisibility(View.VISIBLE);
                                Log.d(TAG, "onSwipeBottom is fired (Facial Layout)");
//                                Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
//                                try {
//                                        processImage(imageFile);
//                                } catch (SSLException e) {
//                                        e.printStackTrace();
//                                }
//                                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                        }
                });
	// Core SDK must be initialized to interact with Bluemix Mobile services.
	BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_UK);

    }

        @Override
        protected void onStart() {
                super.onStart();
                Log.d(TAG, "onStart fired");
                Bundle bundle = getIntent().getExtras();
                String Value = bundle.getString(LauncherActivity.KEY, "null");
                Log.d(TAG, "Value = "+Value);
                if(Value.equals(LauncherActivity.CAMERA_FULL)) {
                        LauncherActivity.KEY = null;
                        cameraHelper.dispatchTakePictureIntent();
//                        bundle.putString(LauncherActivity.KEY, "null");
                }

                else if(Value.equals(LauncherActivity.GALLERY_FULL)) {
                        LauncherActivity.KEY = null;
                        galleryHelper.dispatchGalleryIntent();
//                        bundle.putString(LauncherActivity.KEY, "null");
                }

        }

        @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
                Log.d(TAG, "onActivityResult is fired");
                if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                        loadedImage.setImageBitmap(cameraHelper.getBitmap(resultCode));
                        imageFile = cameraHelper.getFile(resultCode);
                        try {
                                processImage(imageFile);
                        } catch (SSLException e) {
                                Log.e(TAG, "SSL EXCEPTION has occured");
                                e.printStackTrace();
                        }
                }

		if (requestCode == GalleryHelper.PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

			loadedImage.setImageBitmap(galleryHelper.getBitmap(resultCode, data));
                        imageFile = galleryHelper.getFile(resultCode, data);
                        try {
                                processImage(imageFile);
                        } catch (SSLException e) {
                                Log.e(TAG, "SSL EXCEPTION has occured");
                                e.printStackTrace();
                        }
                }
	}

        private void processImage(final File imageFile) throws SSLException {

                Log.d(TAG, "ImageFile = "+ imageFile);
//                if(imageFile != null) {
                if(!isInternetOn()) {
                        Toast.makeText(MainActivity.this, "Internet is not working", Toast.LENGTH_SHORT).show();
                        return;
                }
                if(imageFile == null) {
                        return;
                }
                classifyListView.setAdapter(null);
                faceListView.setAdapter(null);
                classifyList.clear();
                faceDetectList.clear();
//                        final ProgressDialog progressDialog = new ProgressDialog(this);
//                        progressDialog.setTitle("VisualRecognizer");
//                        progressDialog.setMessage("Analyzing image...");
//                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                        progressDialog.setIndeterminate(true);
//                        progressDialog.setMax(100);
//                        progressDialog.show();
////                        progressDialog.setCancelable(false);
//                        new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            while (progressDialog.getProgress() <= progressDialog.getMax()) {
//                                Thread.sleep(200);
//                                if (progressDialog.getProgress() == progressDialog.getMax()) {
//                                    progressDialog.dismiss();
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                  }).start();
//            }
            AnalyzeImage analyzeImage = new AnalyzeImage();
            analyzeImage.execute();
//                        AsyncTask.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                        classifyImage();
//                                        imageFaceDetection();
//                                }
//
//                        });

                }

        private void imageFaceDetection() {
                try {
                        if(service != null) {
                                detectFaces = service.detectFaces(
                                        new VisualRecognitionOptions.Builder()
                                                .images(imageFile)
                                                .build()
                                ).execute();
                        }
                        else {
                                Log.e(TAG, "Service is null");
                                return;
                        }
                }
                catch (Exception e) {
                        Log.d(TAG, "Unable to load information");
                        Log.e(TAG, "SSL EXCEPTION");
                        return;
                }
                if(detectFaces != null) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        List<Face> faces = detectFaces.getImages().get(0).getFaces();
                                        Log.d(TAG, "FACES = "+faces.toString());
                                        Log.d(TAG, "FACES.size() = "+faces.size());
                                        int i = 0;
                                        for(Face face : faces) {
                                                String s="";
                                                if(i != 0) {
                                                        //something to be added
                                                }
                                                Log.d(TAG, "face.toString = "+face.toString());
                                                if(face.getIdentity() != null) {
                                                        faceDetectList.add(String.valueOf(face.getIdentity().getName()));Log.d(TAG, "face.getIdentity() = "+ face.getIdentity().getName());
                                                }
                                                if(face.getGender() != null) {
                                                        faceDetectList.add(String.valueOf(face.getGender().getGender()));     Log.d(TAG, "face.getGender() = " +face.getGender().getGender());
                                                }
                                                if(face.getAge() != null) {
                                                        faceDetectList.add(String.valueOf(face.getAge().getMin()+"-"+face.getAge().getMax()));Log.d(TAG,"face.getAge() = " +face.getAge().getMin()+"-"+face.getAge().getMax());
                                                }
//                                                if(face.getLocation() != null) {
//                                                        faceDetectList.add(String.valueOf(face.getLocation().getWidth()));               Log.d(TAG, "face.getLocation() = " + face.getLocation().getWidth());
//                                                }
                                                i++;
                                        }
                                        Log.d(TAG, "FaceDetectList ="+faceDetectList);
                                        if(faceDetectList != null) {
                                                faceDetectAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, faceDetectList);
                                                faceListView.setAdapter(faceDetectAdapter);
                                                faceDetectAdapter.notifyDataSetChanged();
                                        }
                                }
                        });

                }
        }

        private void classifyImage() {

                try {
                        response = service.classify(
                                new ClassifyImagesOptions.Builder()
                                        .images(imageFile)
                                        .build()
                        ).execute();

                }
                catch (Exception e) {
                        Log.d(TAG, "Unable to load information");
                        Log.e(TAG, "SSL EXCEPTION");
                        return;
                }

//                Log.d(TAG, "response = " + response);
                if(response != null) {
//                                                Log.d(TAG, "respone = " + response);
                       runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                       ImageClassification classification =
                                               response.getImages().get(0);
                                       VisualClassifier classifier =
                                               classification.getClassifiers().get(0);
                                       for(VisualClassifier.VisualClass object : classifier.getClasses()) {
                                               Log.d(TAG, "getName = " + object.getName());
                                               Log.d(TAG, "getScore = " + object.getScore());
                                               if(object.getName().length() < 10 && !classifyList.contains(object.getName())) {
                                                       classifyList.add(object.getName());
                                               }
                                       }
                                       Log.d(TAG, "CLASSIFYLIST = "+classifyList);
                                       if(classifyList != null) {
                                               classifyAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, classifyList);
                                               classifyListView.setAdapter(classifyAdapter);
                                               classifyAdapter.notifyDataSetChanged();
                                       }
                               }
                       });


//                        runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                        TextView multiLineText =
//                                                (TextView) findViewById(R.id.multiLineText);
//                                        multiLineText.setText(output);
//                                }
//                        });
                }
                else {
                        Log.d(TAG, "respone is null");
                }
        }
//                                                        if(object.getScore() > 0.7f) {
//                                output.append("\" ")
//                                        .append(object.getName())
//                                                                        .append(", ")
//                                                                        .append(object.getScore())
//                                        .append("\" \n");
//                                                        }
        @Override
    public void onResume() {
	super.onResume();
    }

    @Override
    public void onPause() {
	super.onPause();
	if(progressDialog != null) {
                progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.menu_main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();

	if (id == R.id.action_settings) {
	    return true;
	}

	return super.onOptionsItemSelected(item);
    }
        public boolean isInternetOn () {

//                Log.e(TAG, "isInternetOn fired");
                ConnectivityManager connec =
                        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                // Check for network connections
                if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {

                        // if connected with internet
                        return true;

                } else if (
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {

                        return false;
                }
                return  false;

        }

        @Override
        public void onRefresh() {
                if(imageFile == null) {
                        return;
                }
                try {
                        swipeView.setRefreshing(false);
                        processImage(imageFile);
                } catch (SSLException e) {
                        e.printStackTrace();
                }
        }

        private class AnalyzeImage extends AsyncTask<Void, Void, Void> {

                @Override
                protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("VisualRecognizer");
                        progressDialog.setMessage("Analyzing image...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                }
                @Override
                protected Void doInBackground(Void... params)  {

                        response = service.classify(
                                new ClassifyImagesOptions.Builder()
                                        .images(imageFile)
                                        .build()
                        ).execute();
                        //                        try {
//                                if(service != null) {
                                        detectFaces = service.detectFaces(
                                                new VisualRecognitionOptions.Builder()
                                                        .images(imageFile)
                                                        .build()
                                        ).execute();
//                                }
//                                else {
//                                        Log.e(TAG, "Service is null");
//                                        return null;
//                                }
//                        }
//                        catch (Exception e) {
//                                Log.d(TAG, "Unable to load information");
//                                Log.e(TAG, "SSL EXCEPTION");
//                                Toast.makeText(MainActivity.this, "Internet is not working", Toast.LENGTH_SHORT).show();
//                                return null;
//                        }
                        return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        if(response != null) {
//                                                Log.d(TAG, "respone = " + response);
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                ImageClassification classification =
                                                        response.getImages().get(0);
                                                VisualClassifier classifier =
                                                        classification.getClassifiers().get(0);
                                                for(VisualClassifier.VisualClass object : classifier.getClasses()) {
                                                        Log.d(TAG, "getName = " + object.getName());
                                                        Log.d(TAG, "getScore = " + object.getScore());
                                                        if(object.getName().length() < 20 && !classifyList.contains(object.getName())) {
                                                                classifyList.add(object.getName());
                                                        }
                                                }
                                                Log.d(TAG, "CLASSIFYLIST = "+classifyList);
                                                if(classifyList != null) {
                                                        classifyAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_of_items, classifyList);
                                                        classifyListView.setAdapter(classifyAdapter);
                                                        classifyAdapter.notifyDataSetChanged();
                                                }
                                        }
                                });


//                        runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                        TextView multiLineText =
//                                                (TextView) findViewById(R.id.multiLineText);
//                                        multiLineText.setText(output);
//                                }
//                        });
                        }
                        else {
                                Log.d(TAG, "respone is null");
                                Toast.makeText(MainActivity.this, "Internet is not working", Toast.LENGTH_SHORT).show();
                        }

                        if(detectFaces != null) {
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                                List<Face> faces = detectFaces.getImages().get(0).getFaces();
                                                Log.d(TAG, "FACES = "+faces.toString());
                                                Log.d(TAG, "FACES.size() = "+faces.size());
                                                int i = 0;
                                                for(Face face : faces) {
                                                        String s="";
                                                        if(i != 0) {
                                                                //something to be added
                                                        }
                                                        Log.d(TAG, "face.toString = "+face.toString());
                                                        if(face.getIdentity() != null) {
                                                                faceDetectList.add(String.valueOf(face.getIdentity().getName()));Log.d(TAG, "face.getIdentity() = "+ face.getIdentity().getName());
                                                        }
                                                        if(face.getGender() != null) {
                                                                faceDetectList.add(String.valueOf(face.getGender().getGender()));     Log.d(TAG, "face.getGender() = " +face.getGender().getGender());
                                                        }
                                                        if(face.getAge() != null) {
                                                                faceDetectList.add(String.valueOf(face.getAge().getMin()+"-"+face.getAge().getMax()));Log.d(TAG,"face.getAge() = " +face.getAge().getMin()+"-"+face.getAge().getMax());
                                                        }
//                                                        if(face.getLocation() != null) {
//                                                                faceDetectList.add(String.valueOf(face.getLocation().getWidth()));               Log.d(TAG, "face.getLocation() = " + face.getLocation().getWidth());
//                                                        }
                                                        i++;
                                                }
                                                Log.d(TAG, "FaceDetectList ="+faceDetectList);
                                                if(faceDetectList != null) {
                                                        faceDetectAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_of_items, faceDetectList);
                                                        faceListView.setAdapter(faceDetectAdapter);
                                                        faceDetectAdapter.notifyDataSetChanged();
                                                }
                                        }
                                });

                        }
                        else {
                                Log.e(TAG, "detectFace is null");
                                Toast.makeText(MainActivity.this, "Internet is not working", Toast.LENGTH_SHORT).show();
                        }
                        swipeView.setRefreshing(false);
                }

        }
}
