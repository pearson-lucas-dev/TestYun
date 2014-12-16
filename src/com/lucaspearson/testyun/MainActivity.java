package com.lucaspearson.testyun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity implements OnTaskCompleted{
	Button startCar, stopCar, lockCar, unlockCar;
	HttpGetter httpGetter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This sets what view should be on the screen on startup of the application
        setContentView(R.layout.activity_main);
        //This sets up the local instance variable with the buttons that are on the view
        startCar = (Button) findViewById(R.id.startCar);
        stopCar = (Button)findViewById(R.id.stopCar);
        lockCar = (Button) findViewById(R.id.lockCar);
        unlockCar = (Button) findViewById(R.id.unlockCar);
        
        httpGetter = new HttpGetter(this);
        startCar.setOnClickListener(new OnClickListener() {
			//This code looks for a press on the start car button
			
			@Override
			public void onClick(View v) {
				httpGetter = new HttpGetter(MainActivity.this);
				try {
					httpGetter.execute(new URL("http://192.168.240.1/arduino/start/1"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
        stopCar.setOnClickListener(new OnClickListener() {
			//This code looks for a press on the stop car button
			
			@Override
			public void onClick(View v) {
				httpGetter = new HttpGetter(MainActivity.this);
				try {
					httpGetter.execute(new URL("http://192.168.240.1/arduino/stop/1"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
        
        unlockCar.setOnClickListener(new OnClickListener() {
			//This code looks for a press on the unlock car button
			
			@Override
			public void onClick(View v) {
				httpGetter = new HttpGetter(MainActivity.this);
				try {
					httpGetter.execute(new URL("http://192.168.240.1/arduino/unlock/1"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
        
        lockCar.setOnClickListener(new OnClickListener() {
			//This code looks for a press on the lock car button
			@Override
			public void onClick(View v) {
				httpGetter = new HttpGetter(MainActivity.this);
				try {
					httpGetter.execute(new URL("http://192.168.240.1/arduino/lock/1"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    private class HttpGetter extends AsyncTask<URL, Void, Boolean> {
    	public Boolean pinStatus = false;
    	private OnTaskCompleted listener;
    	public HttpGetter(OnTaskCompleted listener){
            this.listener=listener;
        }
        protected void onPostExecute(Boolean b){
            // This calls the onTaskCompleted handler so that you can make sure everything went all right with the HttpGetter 
            listener.onTaskCompleted(b);
            
        }
        @Override
        protected Boolean doInBackground(URL... urls) {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(urls[0].toString());
                Boolean test = false;
                
                try {
                        HttpResponse response = client.execute(httpGet);
                        StatusLine statusLine = response.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) { //This code makes sure 
                                HttpEntity entity = response.getEntity();
                                InputStream content = entity.getContent();
                                BufferedReader reader = new BufferedReader(
                                                new InputStreamReader(content));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                        builder.append(line);
                                }
                                if(builder.toString().equals("1")){
                                	test = false;
                                }else if(builder.toString().equals("0")){
                                	test = true;
                                }
                                Log.v("Getter", "Your data: " + builder.toString()); //response data
                                
                        } else {
                                Log.e("Getter", "Failed to communicate with Arduino");
                        }
                } catch (ClientProtocolException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return test;
        }

    }

	@Override
	public void onTaskCompleted(Boolean b) {
		Log.i("Tag","onTaskCompleted: " +b.toString());
		// Look for problems being returned from Arduino
		
		 
	}
}

