package com.example.ProductComparison;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.ProductComparison.Data.getProducts;
import com.example.ProductComparison.Data.productJson;
/**
 * @author Dillon Scott 1604465
 */

/**
 * Creates new Notes by scanning barcode
 * Implements Notes recyclerviews by grabbing values from database
 *
 */
public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.codinginflow.architectureexample.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.codinginflow.architectureexample.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.codinginflow.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY =
            "com.codinginflow.architectureexample.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    Button scanButton, previewButton, websiteButton;
    public static TextView barcodeResult;
    public static TextView title;
    public String link;
    public String productName;
    public static final String Shared_prefs = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String LINK= "link";
    private String barcode;

    //Barcode variables
    String result;
    getProducts get;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        barcodeResult = findViewById(R.id.barcodeTV);

        scanButton = findViewById(R.id.scan);
        previewButton = findViewById(R.id.preview);
        title = findViewById(R.id.edit_text_title);
        websiteButton = findViewById(R.id.goToWebsite);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);


        //Adds ability to edit a note
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
            loadData();
            updateViews();
        }
        setTitle("Add Note");

        /**
         * Starts scan activity
         */
        scanButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScanActivity.class));
            }
        }));

        /**
         * takes user to website of cheapest shop selling the product
         */
        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(link == null){
                    Toast.makeText(AddEditNoteActivity.this, "Press insert First!", Toast.LENGTH_SHORT).show();
                }else {
                    Uri uri = Uri.parse(link);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }
        });

        /**
         * Makes request to API
         * Stores barcode in global variable
         * inputs Json values to notes
         */
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = (String) barcodeResult.getText();
                Log.d("result", result);

                OkHttpClient client = new OkHttpClient();
                get = new getProducts(result);
                Request request = get.getRequest();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String myResponse = response.body().string();
                            AddEditNoteActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //check output in log
                                    Log.d("test", myResponse);

                                    productJson pJ = new productJson(myResponse);
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray = pJ.getPriceList();
                                    JSONObject cheapest = getCheapest(jsonArray);
                                    JSONObject Temp = new productJson(myResponse).getProductName();
                                    try {
                                        Log.d("name", Temp.getString("title"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        editTextTitle.setText(Temp.getString("title"));
                                        editTextDescription.setText("$"+Double.toString(cheapest.getDouble("price")));

                                        link = (cheapest.getString("link"));
                                        Log.d("cheapest",Double.toString(cheapest.getDouble("price")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    String arraysize = Integer.toString(jsonArray.length());
                                    Log.d("array size", arraysize);

                                    if(Integer.parseInt(arraysize) == 0){
                                        Toast.makeText(AddEditNoteActivity.this, "Sorry not available online", Toast.LENGTH_SHORT).show();
                                    }
                                    saveName();

                                }
                            });
                        }
                    }
                });


            }
        });
    }

    /**
     * get cheapest product in selection
     * code could be put into 'productJson' but was easier to manipulate here
     * @param array
     * @return
     */
    private JSONObject getCheapest(JSONArray array) {
        int temp, size;
        size = array.length();

        int price = 0;
        int price2 = 0;
        JSONObject MAX = new JSONObject();
        try {
            MAX = array.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("length", Integer.toString(array.length()));
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j<size; j++) {
                try {
                    price = array.getJSONObject(i).getInt("price");
                    price2 = array.getJSONObject(i + 1).getInt("price");
                    if (price >= price2) {
                        MAX = array.getJSONObject(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return MAX;
    }

    /**
     * Saves note in database before being displayed
     * Notes are displayed in mainActivity
     */
    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id!=-1){
            data.putExtra(EXTRA_ID,id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * saves barcode for product and stores it in sharedpreferences
     */
    public void saveName(){
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LINK,link);
        editor.putString(TEXT, barcodeResult.getText().toString());
        editor.apply();
        Toast.makeText(this,"data saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * loads barcode from shared preferences
     */
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Shared_prefs,MODE_PRIVATE);
        barcode = sharedPreferences.getString(TEXT,"");
        link = sharedPreferences.getString(LINK,"");

    }

    /**
     * sets barcode view to barcode number
     */
    public void updateViews(){
        barcodeResult.setText("barcode: " + barcode);


    }





}