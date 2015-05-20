package com.example.phototaker;

import java.io.File;  
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View.OnClickListener;
import android.graphics.Bitmap; 
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; 
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {
 
	public static Activity activity;
	private static final int TAKE_PICTURE_REQUEST_B = 100;   
	private static final String AUTHORIZATION_URL = "https://api.imgur.com/oauth2/authorize";
    private static final String CLIENT_ID = "e77cb69dbecaaa0";
	private Logger logger = Logger.getLogger("MainActivity.class");
    private ImageView mCameraImageView;  
    private Boolean picture_available = false;
	private Bitmap photoBitmap;
	private String accessToken; 
	private String refreshToken;
    private Bitmap mCameraBitmap;   
    private String uploadedImageUrl = "";
	private LinearLayout rootView;   
    
    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startImageCapture();
        }
    };
    private OnClickListener mUploadImgurButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	if (picture_available==false)
        	{
        		//if a picture was not taken, use a popup toast
        		Context context = getApplicationContext();
        		CharSequence text = "Take a picture first!";
        		int duration = Toast.LENGTH_LONG;

        		Toast toast = Toast.makeText(context, text, duration);
        		toast.show();
        	}
        	else
        	{
        		//there is an image, upload to imgur 
        		//first save the image to temporary file
        		File outputDir = getApplicationContext().getCacheDir(); // context being the Activity pointer
        		File outputFile;
        		 OutputStream outStream = null;
        		 String picturePath = "";
				try {
					outputFile = File.createTempFile("temporary", ".jpg", outputDir);
					   outStream = new FileOutputStream(outputFile);
	        	        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
	        	        outStream.flush();
	        	        outStream.close();
	        	         picturePath = outputFile.getAbsolutePath();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					  logger.info("Temporary File failed to be create");
					e1.printStackTrace();
				}
              
        		
        		if (!picturePath.isEmpty())
        		{
        		 (new UploadToImgurTask()).execute(picturePath);
        		}
        		}
            
        }
    };

	

 

 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);	 
        rootView = new LinearLayout(this);
        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);
        
        findViewById(R.id.capture_image_button).setOnClickListener(mCaptureImageButtonClickListener);
        findViewById(R.id.upload_imgur_button).setOnClickListener(mUploadImgurButtonClickListener);
        String action = getIntent().getAction();
        logger.info(action);
        if (action == null || !action.equals(Intent.ACTION_VIEW)) { // We need access token to use Imgur's api
        	 logger.info("Start OAuth Authorization");

            Uri uri = Uri.parse(AUTHORIZATION_URL).buildUpon()
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .appendQueryParameter("response_type", "token")
                    .appendQueryParameter("state", "init")
                    .build();

            Intent intent = new Intent();
            intent.setData(uri);
            logger.info("Lets do this");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            logger.info(uri.toString());
            startActivity(intent);
            finish();

        } else { // Now we have the token, can do the upload

            logger.info("Got Access Token");

            Uri uri = getIntent().getData();
            Log.d("Got imgur's access token", uri.toString());
            String uriString = uri.toString();
            String paramsString = "http://callback?" + uriString.substring(uriString.indexOf("#") + 1);
            Log.d("tag", paramsString);
            List<NameValuePair> params = URLEncodedUtils.parse(URI.create(paramsString), "utf-8");
            Log.d("tag", Arrays.toString(params.toArray(new NameValuePair[0])));
            
            for (NameValuePair pair : params) {
                if (pair.getName().equals("access_token")) {
                    accessToken = pair.getValue();
                } else if (pair.getName().equals("refresh_token")) {
                    refreshToken = pair.getValue();
                }
            }

        } 
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_REQUEST_B) {
            if (resultCode == RESULT_OK) {
                // Recycle the previous bitmap.
                if (mCameraBitmap != null) {
                    mCameraBitmap.recycle();
                    mCameraBitmap = null;
                }
                Bundle extras = data.getExtras();
                mCameraBitmap = (Bitmap) extras.get("data");
                mCameraImageView.setImageBitmap(mCameraBitmap); 
                photoBitmap = mCameraBitmap;
                picture_available = true;
            } else {
                mCameraBitmap = null;
                picture_available= false;
            }
        }
    }
    
    private void startImageCapture() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PICTURE_REQUEST_B);
    }
    
    

// Here is the upload task
class UploadToImgurTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
        final String upload_to = "https://api.imgur.com/3/upload";

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(upload_to);

        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addPart("image", new FileBody(new File(params[0])))
                    .build();

            httpPost.setHeader("Authorization", "Bearer " + accessToken);
            httpPost.setEntity(entity);

            final HttpResponse response = httpClient.execute(httpPost,
                    localContext);

            final String response_string = EntityUtils.toString(response
                    .getEntity());

            final JSONObject json = new JSONObject(response_string);

            Log.d("tag", json.toString());

            JSONObject data = json.optJSONObject("data");
            uploadedImageUrl = data.optString("link");
            Log.d("tag", "uploaded image url : " + uploadedImageUrl);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean.booleanValue()) { // after sucessful uploading, show the image in web browser
            Button openBrowser = new Button(MainActivity.this);
            rootView.addView(openBrowser);
            openBrowser.setText("Open Browser");
            openBrowser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(uploadedImageUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }
}
}
 