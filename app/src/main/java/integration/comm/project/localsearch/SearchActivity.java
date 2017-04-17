package integration.comm.project.localsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import helper.DataAdapter;
import helper.PersonalData;


public class SearchActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private ListView listView;

    private DataAdapter adapter;

    private String selectedItem = "Name";

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

        textView = (TextView) findViewById(R.id.autoCompleteTextView);



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem mi = menu.getItem(R.id.item3);
        button.setText(mi.getTitle());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        button.setText(item.getTitle());
        selectedItem = item.getTitle().toString();
        return true;
    }

}
