package com.testapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.testapp.service.CallLogService;
import com.facebook.react.HeadlessJsTaskService;

import android.util.Log;

public final class CallLogReceiver extends BroadcastReceiver {

    static String lastState = "";
    static boolean ring = false;
    static boolean callReceived = false;
    static String callerPhoneNumber = "";

    public final void onReceive(Context context, Intent intent) {

        // Get the current Phone State
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Log.e("TEST", state);

        if(state == null || lastState == state)
            return;

        // If phone state "Rininging"
        if( state.equals(TelephonyManager.EXTRA_STATE_RINGING) ) {
            ring = true;

            Log.e("TEST", "in ringing if-a");

            Log.e("TEST const", TelephonyManager.EXTRA_INCOMING_NUMBER);
            // Get the Caller's Phone Number
            callerPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER );

            Log.e("TEST const2", TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.e("TEST RR2", intent.getExtras().toString() );
        }

        // If incoming call is received
        if( state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ) {
            callReceived = true;
        }

        // If phone is Idle
        if( state.equals(TelephonyManager.EXTRA_STATE_IDLE) ) {

            Log.e("TEST", "in iddle If");
            Log.e("TEST ring", Boolean.toString(ring) );
            Log.e("TEST received", Boolean.toString(callReceived) );
            Log.e("TEST numer", callerPhoneNumber);

            // If phone was ringing (ring = true)
            // and not received (callReceived = false)
            // then it is a missed call

            if( ring == true && callReceived == false && callerPhoneNumber != null) {
                Log.e("TEST", "if 2");
                Intent callIntent = new Intent(context, CallLogService.class);

                callIntent.putExtra("phone_number", callerPhoneNumber);

                context.startService(callIntent);
                HeadlessJsTaskService.acquireWakeLockNow(context);

                Log.e("TEST", callerPhoneNumber);
            }

            ring = false;
            callReceived = false;
        }

        lastState = state;
    }
}