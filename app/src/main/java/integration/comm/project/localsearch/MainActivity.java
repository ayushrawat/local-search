package integration.comm.project.localsearch;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.AsyncTask;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import java.util.Arrays;
import java.util.List;

import integration.comm.project.localsearch.helper.BackgroundDownload;


public class MainActivity extends AppCompatActivity {

    static final String PREF_USER_NAME = "LOCAL_SEARCH_USERNAME";

    EditText email;
    CheckBox keepSignedIn;
    Button signIn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.editText);
        keepSignedIn = (CheckBox) findViewById(R.id.checkBox);
        signIn = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        email.setVisibility(View.GONE);
        keepSignedIn.setVisibility(View.GONE);
        signIn.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if(info !=null && info.isConnected() && isGooglePlayServicesAvailable()) {
            GoogleAccountCredential credentials = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(new String[]{SheetsScopes.SPREADSHEETS_READONLY})
            ).setBackOff(new ExponentialBackOff());
            credentials.setSelectedAccountName(getText(R.string.username).toString());
            //new BackgroundDownload(credentials).execute();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "!!! No Internet Connection OR Connection to Play Services is down !!! Aborting...", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }



}
