package com.ibm.mysampleapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
//        private TextView classifyTextView;
//        private TextView facialTextView;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
                 Log.d(TAG, "MainActivity onCreate fired...");
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
                faceDetectList = new ArrayList<>();
//                classifyTextView = (TextView) findViewById(R.id.classifyText);
//                facialTextView = (TextView) findViewById(R.id.facialText);

                swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeView);
//                classifyTextView.setVisibility(View.INVISIBLE);
//                facialTextView.setVisibility(View.INVISIBLE);
                swipeView.setOnRefreshListener(this);

               final ImageButton fab = (ImageButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            galleryHelper.dispatchGalleryIntent();
                    }
                });

                ImageButton cameraFab = (ImageButton) findViewById(R.id.cameraFab);
                cameraFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Log.d(TAG, "onClick for cameraFab started");
                                cameraHelper.dispatchTakePictureIntent();
                                Log.d(TAG, "onClick for cameraFab ended");
                        }
                });

                loadedImage.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

                        public void onSwipeRight() {
                                cameraHelper.dispatchTakePictureIntent();
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
                        } catch (RemoteException e) {
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
                        } catch (RemoteException e) {
                                e.printStackTrace();
                        }
                }
	}

        private void processImage(final File imageFile) throws SSLException, RemoteException {

                        Log.d(TAG, "ImageFile = "+ imageFile);
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
//                        classifyTextView.setVisibility(View.VISIBLE);
//                        facialTextView.setVisibility(View.VISIBLE);
                        try {
                                AnalyzeImage analyzeImage = new AnalyzeImage();
                                analyzeImage.execute();
                        } catch (RuntimeException e) {
                                Log.d(TAG, "RUN TIME EXCEPTION : " + e.getMessage());
                        }

         }

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

        public boolean isInternetOn () {

//                Log.d(TAG, "isInternetOn fired");
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
                } catch (SSLException | RemoteException e) {
                        e.printStackTrace();
                }
        }

        private class AnalyzeImage extends AsyncTask<Void, Void, Void>  {

                @Override
                protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("VisualRecognizer");
                        progressDialog.setMessage("Analyzing image...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                }
                @Override
                protected Void doInBackground(Void... params) {
                        response = service.classify(
                                new ClassifyImagesOptions.Builder()
                                        .images(imageFile)
                                        .build()
                        ).execute();
                        detectFaces = service.detectFaces(
                                new VisualRecognitionOptions.Builder()
                                        .images(imageFile)
                                        .build()
                        ).execute();
                        return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        progressDialog  = null;
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
