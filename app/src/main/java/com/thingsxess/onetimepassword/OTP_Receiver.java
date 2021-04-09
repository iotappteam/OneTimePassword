package com.thingsxess.onetimepassword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.chaos.view.PinView;

public class OTP_Receiver extends BroadcastReceiver {

    private static PinView pinView;

    public void setEditText(PinView pinView)
    {
        OTP_Receiver.pinView = pinView;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (SmsMessage sms : messages)
        {
            String message = sms.getMessageBody();
            String otp = message.split(" is")[0];
            Toast.makeText(context, "otp", Toast.LENGTH_LONG).show();
            pinView.setText(otp);

        }
    }
}

