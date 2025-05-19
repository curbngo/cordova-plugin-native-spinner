// cordova-plugin-native-spinner
package com.greybax.spinnerdialog;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.MotionEvent;

public class CallbackProgressDialog extends ProgressDialog {
  private CallbackContext callbackContext;
  private boolean isCallbackSent = false;

  public CallbackProgressDialog(Context context) {
    super(context);
  }

  public static CallbackProgressDialog show(Context context,
      CharSequence title, CharSequence message, boolean indeterminate,
      boolean cancelable, OnCancelListener cancelListener,
      CallbackContext callbackContext) {
    CallbackProgressDialog dialog = new CallbackProgressDialog(context);
    dialog.callbackContext = callbackContext;
    dialog.setTitle(title);
    dialog.setMessage(message);
    dialog.setIndeterminate(indeterminate);
    dialog.setCancelable(cancelable);
    dialog.setOnCancelListener(cancelListener);
    dialog.show();
    return dialog;
  }

  private void sendCallback() {
    if (!isCallbackSent && callbackContext != null) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
      isCallbackSent = true;
      callbackContext = null; // Clear the reference
    }
  }

  @Override
  public void onBackPressed() {
    sendCallback();
    super.onBackPressed();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      sendCallback();
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void dismiss() {
    sendCallback();
    super.dismiss();
  }
}