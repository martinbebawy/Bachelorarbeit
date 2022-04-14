package com.example.weathermoodbac;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Anmelden extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    Button signInButton;
    GoogleSignInAccount account;
    RequestQueue queue;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anmelden);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        queue = Volley.newRequestQueue(Anmelden.this);
    }

    private void insertPerson(final VolleyCallback callback, GoogleSignInAccount account) {
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        Runnable hideDialog = new Runnable() {
            public void run() {
                alert.dismiss();
                finish();
            }
        };
        String tempUrl = urlWebserver + "/insertPerson.php";
        StringRequest requestLocalWebserver = new StringRequest(Request.Method.POST, tempUrl, response -> {
            Log.d("repsonseLocalWebserver", response);
            callback.onSuccess();
        }, error -> {
            Log.d("errorLocalWebserver", "" + error);
            AlertDialog.Builder builder = new AlertDialog.Builder(Anmelden.this, R.style.RoundedDialog);
            builder.setMessage("Keine Verbindung zum Server möglich. Bitte prüfen Sie ihre Internetverbindung.")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
            executor.schedule(hideDialog, 5, TimeUnit.SECONDS);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("personId", account.getId());
                map.put("vorname", account.getGivenName());
                map.put("nachname", account.getFamilyName());
                map.put("email", account.getEmail());
                return map;
            }
        };
        queue.add(requestLocalWebserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (didChangeAnmeldung) {
            recreate();
            didChangeAnmeldung = false;
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            insertPerson(() -> {
                Intent intent = new Intent(this.getApplicationContext(), Startseite.class);
                startActivity(intent);
            }, account);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("sss", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
}