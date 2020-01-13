package com.example.stocksfirebasepushnotifications;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "MainActivity";
    private RequestQueue _queue;
    // you should probably replace this url with your own
    private static final String REQUEST_URL = "http://192.168.43.51:3000/";
    public static final String userName = "user";
    private static String token = "this is my token";
    IntentFilter messageFilter;
    IntentFilter errorFilter;
    static ProgressDialog progressDialog;
    EditText stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _queue = Volley.newRequestQueue(this);
        messageFilter = new IntentFilter("com.example.stockapp.MESSAGE_RECEIVEED");
        errorFilter = new IntentFilter("com.example.stocksapp.MESSAGE_ERROR");
        registerReceiver(messageReceiver, messageFilter);
        registerReceiver(messageReceiver, errorFilter);
        progressDialog = new ProgressDialog(this);
        _queue.start();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                JSONObject requestObject = new JSONObject();
                try {
                    requestObject.put("token", token);
                }catch (JSONException e){
                    Log.e(TAG_MAIN, token);
                }

                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL  + userName +  token,
                        requestObject, new Response.Listener<JSONObject>() {
                    //@param response refers to the response we get from the server after we send the token at the app activation.
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG_MAIN, "Token saved successfully" + response.toString());
                    }
                },
                        new Response.ErrorListener(){
                    @Override
                            public void onErrorResponse(VolleyError error){
                        Log.e(TAG_MAIN, "Failed to save the token " + token + "\n and the error is - " + error);
                    }
                });
                _queue.add(req);
            }
        });

        Button button = findViewById(R.id.button);
        stock = (EditText) findViewById(R.id.stockName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the Keyboard from the user.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                //get the stock name and send it forward.
                String stockName = stock.getText().toString();
                fetchStocks(v, stockName);
            }
        });
    }

    public void fetchStocks(final View v, final String stock){
        progressDialog.setMessage("Fetching " + stock);
        progressDialog.show();
        sendStockToServer(stock);
    }


    public void sendStockToServer(final String stock) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("stock", stock);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG_MAIN, "Message did no sent, could not put `stocks` in the request object " + e.toString());
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL + stock + "/" + token,
                requestObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.i("The Response is", response.toString());

                    String stockPrice = response.getString("stockPrice");
                    String message = (stockPrice.equals("null")) ?
                            "I am terribly sorry, but there is currently no such stock available.":
                            ("The current stock price for " + stock + " is " + stockPrice);

                    ((TextView) MainActivity.this.findViewById(R.id.CurrentStock)).setText(message);
                    String finePrint = "You can exit the app now.\n*as Requested, you will get a new notification of the stock price every 15 seconds.";
                    // Setting the text for the "fine print"
                    ((TextView) MainActivity.this.findViewById(R.id.finePrint))
                            .setText(finePrint);
                    progressDialog.hide();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        Log.i(TAG_MAIN, "Message did no sent, VolleyError was sent - " + e.toString());
                        Intent errorIntent = new Intent();
                        errorIntent.setAction("com.example.stocksapp.MESSAGE_ERROR");
                        sendBroadcast(errorIntent);
                    }
                });
        // Sometimes it takes more than 2.5 seconds for the message to reach so I have updated the time to 5 seconds.
        req.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {}
        });

        _queue.add(req);
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.hide();
            try {
                if (intent.getAction().equalsIgnoreCase("com.example.stocksapp.MESSAGE_RECEIVED")) {
                    String textToDisplay = intent.getStringExtra("com.example.stocksapp.SYMBOL");
                    textToDisplay += " : " + intent.getStringExtra("com.example.stocksapp.VALUE") + "$";
                    ((TextView) MainActivity.this.findViewById(R.id.CurrentStock)).setText(textToDisplay);

                } else if (intent.getAction().equalsIgnoreCase("com.example.stocksapp.MESSAGE_ERROR")) {
                    Toast.makeText(context, "Please Enter a Stock Name.", Toast.LENGTH_LONG).show();
                }
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    };


}