package integration.comm.project.localsearch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helper.PersonalData;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS_READONLY };

    private GoogleApiClient mGoogleApiClient;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if(isDeviceOnline()) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(new Scope(SheetsScopes.SPREADSHEETS_READONLY))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection!",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG, "Signed in with: " + acct.getDisplayName());

            if (isGooglePlayServicesAvailable()) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                        Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
                if (credential.getSelectedAccountName() == null ) {
                    credential.setSelectedAccountName(acct.getEmail());
                    credential.setSelectedAccount(acct.getAccount());
                }
                try {
                    new BackgroundDownload(credential, getApplicationContext()).execute();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Unable to call Background download of sheet, Aborting...", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            } else {
                Toast.makeText(MainActivity.this, "Google Play Services not available, Cannot read remote data without that!",Toast.LENGTH_LONG).show();
                finish();
                return;
            }

        } else {
            Toast.makeText(MainActivity.this, "Unable to Sign in to Google Account, Please retry...",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(MainActivity.this, "No Connection! "+connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
        finish();
        return;
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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

    private class BackgroundDownload extends AsyncTask<Void, Void, ArrayList<PersonalData>> {

        private static final String TAG = "BackgroundDownload";
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Context context = null;
        private Exception error = null;

        BackgroundDownload(GoogleAccountCredential credential, Context context) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            Log.i(TAG, "Account in use: "+credential.getSelectedAccountName());
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
            this.context = context;
        }

        @Override
        protected ArrayList<PersonalData> doInBackground(Void... params) {
            try {
                return getDataFromRemoteSheet();
            } catch (Exception e) {
                Log.e(TAG, "ERROR in downloading Data", e);
                error = e;
                cancel(true);
                return null;
            }
        }

        private ArrayList<PersonalData> getDataFromRemoteSheet() throws Exception {
            String sheetId = context.getString(R.string.sheet_id);
            String range = context.getString(R.string.range);

            ArrayList<PersonalData> list = new ArrayList<PersonalData>();
            Log.i(TAG, "Loading Data from Remote sheet now...");
            ValueRange response = null;
            try {
                response = this.mService.spreadsheets().values()
                        .get(sheetId, range)
                        .execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), 1001);
            }
            List<List<Object>> values = response.getValues();
            if(values != null) {
                for (List row: values) {
                    list.add(new PersonalData(row.get(0)+"", row.get(1)+"", row.get(2)+"", row.get(3)+"", row.get(4)+"", row.get(5)+""));
                }
            }
            Log.i(TAG, "Loaded Data from Remote sheet");
            return list;
        }


        @Override
        protected void onPostExecute(ArrayList<PersonalData> map) {
            progressBar.setVisibility(View.GONE);
            Intent searchPage = new Intent(getApplicationContext(),
                    SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("load_data", map);
            searchPage.putExtras(bundle);
            startActivity(searchPage);
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
}
