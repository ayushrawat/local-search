package integration.comm.project.localsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import helper.DataAdapter;
import helper.PersonalData;


public class SearchActivity extends AppCompatActivity {

    private EditText textView;
    private Button button;
    private ListView listView;

    private DataAdapter adapter;

    private String selectedItem = "Area";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        button = (Button) findViewById(R.id.button);
        registerForContextMenu(button);

        Bundle bundle = getIntent().getExtras();
        ArrayList<PersonalData> listData = bundle.getParcelableArrayList("load_data");

        adapter = new DataAdapter(this, listData);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        textView = (EditText) findViewById(R.id.autoCompleteTextView);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Do Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = textView.getText().toString();
                adapter.Filter(text, selectedItem);
            }
        });

        textView.requestFocus();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem mi = menu.getItem(3);
        mi.setChecked(true);
        button.setText(mi.getTitle());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        button.setText(item.getTitle());
        selectedItem = item.getTitle().toString();
        item.setChecked(true);
        return true;
    }

}
