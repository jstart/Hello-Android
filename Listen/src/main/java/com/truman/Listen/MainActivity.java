package com.truman.Listen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;

import static java.util.Locale.CANADA;

public class MainActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private static RequestQueue queue;
    ArrayAdapter<String> mArrayAdapter;
    ArrayList<String> mNameList = new ArrayList<String>();

    private TextToSpeech tts;

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    ListView mainListView;

    ReadabilityRequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainButton = (Button) findViewById(R.id.main_button);
        mainEditText = (EditText) findViewById(R.id.main_edittext);
        mainListView = (ListView) findViewById(R.id.main_listview);

        mainTextView.setText("Set in Java!");

        mainButton.setOnClickListener(this);

        mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mNameList);
        mainListView.setAdapter(mArrayAdapter);

        tts = new TextToSpeech(this, this);
        tts.setPitch(0.8f);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            public void onStart(String utteranceId) {
                Log.d("com.truman.listen", utteranceId);
            }

            public void onDone(String utteranceId) {
                Log.d("com.truman.listen", utteranceId);
            }

            public void onError(String utteranceId) {
                Log.d("com.truman.listen", utteranceId);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        requestManager = new ReadabilityRequestManager(queue, this);
    }

    void handleSendText(Intent intent) throws JSONException {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            String[] stringArray = sharedText.split("\n");
//            speakOut(stringArray[0]);
            try {
                requestManager.loadURL(stringArray[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mainTextView.setText(sharedText);
        }
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        mainTextView.setText("Button pressed!");

        mainTextView.setText(mainEditText.getText().toString()
                + " is learning Android development!");

        // Also add that value to the list shown in the ListView
        mNameList.add(mainEditText.getText().toString());
        mArrayAdapter.notifyDataSetChanged();

        speakOut(mainEditText.getText().toString());

        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the message, woot!");

        startActivity(Intent.createChooser(intent, "How do you want to share?"));
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(CANADA);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                mainButton.setEnabled(true);
                // Get intent, action and MIME type
                Intent intent = getIntent();
                String action = intent.getAction();
                String type = intent.getType();

                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if ("text/plain".equals(type)) {
                        try {
                            handleSendText(intent); // Handle text being sent
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        // Handle other intents, such as being started from the home screen
                    }
                }
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        Log.d("com.truman.listen", utteranceId);

    }
}
