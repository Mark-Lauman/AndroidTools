package ca.marklauman.tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.security.InvalidParameterException;

/** This activity presents the user with a list of items, and returns
 *  when they click one. Return codes are provided by {@link #PARAM_RETURN},
 *  and are always Strings. The names for those codes are set by {@link #PARAM_DISPLAY}.
 *  The activity title is set by {@link #PARAM_TITLE}. */
@SuppressWarnings("WeakerAccess")
@SuppressLint("Registered")
public class SingleItemSelector extends AppCompatActivity
        implements ListView.OnItemClickListener {
    /** The title to display on the activity's action bar */
    public static final String PARAM_TITLE = "title";
    /** The array of options to choose from (as displayed) */
    public static final String PARAM_DISPLAY = "display";
    /** The array of result codes to return. */
    public static final String PARAM_RETURN = "codes";
    /** The key for the resulting language code */
    public static final String RES_CODE = "code";

    /** Return codes for each item on display. */
    private String[] returnCodes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View and action bar setup
        ListView list = new ListView(this);
        setContentView(list);
        ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // Load the bundled arguments
        Bundle params = getIntent().getExtras();
        if(params == null) return;
        String title = params.getString(PARAM_TITLE);
        if(title != null && ab != null)
            ab.setTitle(title);
        String[] displayNames = params.getStringArray(PARAM_DISPLAY);
        returnCodes = params.getStringArray(PARAM_RETURN);
        if(displayNames == null || returnCodes == null)
            throw new InvalidParameterException("PARAM_DISPLAY / PARAM_RETURN not set");

        // Adapter and list setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                displayNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Return the result code for the clicked item.
        Intent i = new Intent();
        i.putExtra(RES_CODE, returnCodes[position]);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Cancel if the back button is clicked
        if(item.getItemId() != android.R.id.home)
            return super.onOptionsItemSelected(item);
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}