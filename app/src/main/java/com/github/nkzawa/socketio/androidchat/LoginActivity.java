package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;

    private Socket mSocket;

    private String TAG = "TTT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Log.d("TTT", "mSocket.on login");
        mSocket.on("login", onLogin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("TTT", "mSocket.off login");
        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.d("TTT", "attemptLogin");
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // perform the user login attempt.
//         mSocket.emit("add user", username);

//        Log.d("TTT", "join:" + username);
        mSocket.emit("join", username);









        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String sessionId = mSocket.id();
                Log.i(TAG, "onConnected");
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onConnected();
//                }
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Log.e(TAG, "onConnectError: " + args.toString());
                Log.e(TAG, "onConnectError: " + args[0]);
            }
        });

        mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "onError: " + args[0]);
            }
        });

        mSocket.on("joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                String userId = (String) args[1];
//                if (/*!mUserId.equals(userId) &&*/ mOnSignalEventListener != null) {
//                    //mOnSignalEventListener.onRemoteUserJoined(userId);
//                    mOnSignalEventListener.onUserJoined(roomName, userId);
//                }
                //Log.i(TAG, "onRemoteUserJoined: " + userId);
                Log.i(TAG, "onUserJoined, room:" + roomName + "uid:" + userId);
            }
        });



    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TTT", "Emitter.Listener onLogin");
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
                Log.d("TTT", "Emitter.Listener numUsers");
            } catch (JSONException e) {
                Log.d("TTT", "Emitter.Listener e");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}



