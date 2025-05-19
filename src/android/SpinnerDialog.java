// cordova-plugin-native-spinner
package com.greybax.spinnerdialog;

import java.util.Stack;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.ProgressBar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class SpinnerDialog extends CordovaPlugin {

  private Stack<ProgressDialog> spinnerDialogStack = new Stack<ProgressDialog>();
  private boolean isActivityFinishing = false;

  @Override
  public void onDestroy() {
    dismissAllDialogs();
    super.onDestroy();
  }

  @Override
  public void onReset() {
    dismissAllDialogs();
    super.onReset();
  }

  private void dismissAllDialogs() {
    if (cordova.getActivity() != null) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          while (!spinnerDialogStack.empty()) {
            ProgressDialog dialog = spinnerDialogStack.pop();
            if (dialog != null && dialog.isShowing()) {
              dialog.dismiss();
            }
          }
        }
      });
    }
  }

  public SpinnerDialog() {
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (this.cordova.getActivity() == null || this.cordova.getActivity().isFinishing()) {
      isActivityFinishing = true;
      return true;
    }

    if (action.equals("show")) {
      final String title = "null".equals(args.getString(0)) ? null : args.getString(0);
      final String message = "null".equals(args.getString(1)) ? null : args.getString(1);
      final boolean isFixed = args.getBoolean(2);
                
      final CordovaInterface cordova = this.cordova;
      Runnable runnable = new Runnable() {
        public void run() {
          if (isActivityFinishing) {
            return;
          }
          
          DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
              if (!isFixed) {
                dismissAllDialogs();
                callbackContext.success();
              }
            }
          };
          
          ProgressDialog dialog;
          if (isFixed) {
            if (!spinnerDialogStack.empty()) {
              dialog = spinnerDialogStack.peek();
              updateDialogContent(dialog, title, message);
            } else {
              dialog = CallbackProgressDialog.show(cordova.getActivity(), title, message, true, false, null, callbackContext);
              spinnerDialogStack.push(dialog);
            }
          } else {
            if (!spinnerDialogStack.empty()) {
              dialog = spinnerDialogStack.peek();
              updateDialogContent(dialog, title, message);
            } else {
              dialog = ProgressDialog.show(cordova.getActivity(), title, message, true, true, onCancelListener);
              spinnerDialogStack.push(dialog);
            }
          }
          
          if (title == null && message == null) {
            dialog.setContentView(new ProgressBar(cordova.getActivity()));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          }
        }
      };
      this.cordova.getActivity().runOnUiThread(runnable);

    } else if (action.equals("hide")) {
      Runnable runnable = new Runnable() {
        public void run() {
          dismissAllDialogs();
        }
      };
      this.cordova.getActivity().runOnUiThread(runnable);
    }
    
    return true;
  }

  private void updateDialogContent(ProgressDialog dialog, String title, String message) {
    if (dialog != null) {
      if (title != null) {
        dialog.setTitle(title);
      }
      if (message != null) {
        dialog.setMessage(message);
      }
    }
  }
}
