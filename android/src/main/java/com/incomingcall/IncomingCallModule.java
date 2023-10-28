package com.incomingcall;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.react.bridge.ReadableArray;


@ReactModule(name = IncomingCallModule.NAME)
public class IncomingCallModule extends ReactContextBaseJavaModule {
  public static final String NAME = "IncomingCall";
  private static final String TAG = "IncomingCallModule";
  private static ReactApplicationContext reactContext;

  public IncomingCallModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void displayNotification(String uuid, @Nullable String avatar,@Nullable int timeout, ReadableMap foregroundOptions) throws JSONException {
    Log.d(TAG, "displayNotification ui"  );
    if(foregroundOptions == null){
      Log.d(TAG, "foregroundOptions can't null"  );
      return;
    }

    Intent intent = new Intent(getReactApplicationContext(), IncomingCallService.class);
    intent.putExtra("uuid", uuid);
    intent.putExtra("name",foregroundOptions.getString("notificationTitle") );
    intent.putExtra("avatar", avatar);
    intent.putExtra("info", foregroundOptions.getString("notificationBody"));
    intent.putExtra("channelId", foregroundOptions.getString("channelId"));
    intent.putExtra("channelName", foregroundOptions.getString("channelName"));
    intent.putExtra("timeout", timeout);
    intent.putExtra("icon",foregroundOptions.getString("notificationIcon"));
    intent.putExtra("answerText",foregroundOptions.getString("answerText"));
    intent.putExtra("declineText",foregroundOptions.getString("declineText"));
    intent.putExtra("notificationColor",foregroundOptions.getString("notificationColor"));
    intent.putExtra("notificationSound",foregroundOptions.getString("notificationSound"));
    intent.putExtra("mainComponent",foregroundOptions.getString("mainComponent"));
    if(foregroundOptions.hasKey("payload")){
      JSONObject payload= convertMapToJson(foregroundOptions.getMap("payload"));
      intent.putExtra("payload",payload.toString());
    }
    intent.setAction(Constants.ACTION_SHOW_INCOMING_CALL);
    reactContext.startService(intent);
  }

  @ReactMethod
  public void hideNotification(Boolean hide) {
    if (IncomingCallActivity.active && hide) {
      IncomingCallActivity.getInstance().destroyActivity(false);
    }
    Intent intent = new Intent(getReactApplicationContext(), IncomingCallService.class);
    intent.setAction(Constants.HIDE_NOTIFICATION_INCOMING_CALL);
    this.reactContext.stopService(intent);
  }

  private Context getAppContext() {
    return this.reactContext.getApplicationContext();
  }

  public Activity getCurrentReactActivity() {
    return this.reactContext.getCurrentActivity();
  }

  @ReactMethod
  public void backToApp() {
    Context context = getAppContext();
    if (context == null) {
      return;
    }
    String packageName = context.getApplicationContext().getPackageName();
    Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
    Activity activity = getCurrentReactActivity();
    boolean isOpened = activity != null;

    if (!isOpened) {
      Log.d("IncomingCallModule", "activity is not running, starting activity");
      focusIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(focusIntent);
    }else{
      Log.d("IncomingCallModule", "activity is running");
      focusIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
      activity.startActivity(focusIntent);
    }
  }

  @ReactMethod
  public void addListener(String eventName) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  public static void sendEventToJs(String eventName,@Nullable WritableMap params) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }

  private static JSONObject convertMapToJson(ReadableMap readableMap) throws JSONException {
    JSONObject object = new JSONObject();
    ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
    while (iterator.hasNextKey()) {
      String key = iterator.nextKey();
      switch (readableMap.getType(key)) {
        case Null:
          object.put(key, JSONObject.NULL);
          break;
        case Boolean:
          object.put(key, readableMap.getBoolean(key));
          break;
        case Number:
          object.put(key, readableMap.getDouble(key));
          break;
        case String:
          object.put(key, readableMap.getString(key));
          break;
        case Map:
          object.put(key, convertMapToJson(readableMap.getMap(key)));
          break;
        case Array:
          object.put(key, convertArrayToJson(readableMap.getArray(key)));
          break;
      }
    }
    return object;
  }

  private static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
    JSONArray array = new JSONArray();
    for (int i = 0; i < readableArray.size(); i++) {
      switch (readableArray.getType(i)) {
        case Null:
          break;
        case Boolean:
          array.put(readableArray.getBoolean(i));
          break;
        case Number:
          array.put(readableArray.getDouble(i));
          break;
        case String:
          array.put(readableArray.getString(i));
          break;
        case Map:
          array.put(convertMapToJson(readableArray.getMap(i)));
          break;
        case Array:
          array.put(convertArrayToJson(readableArray.getArray(i)));
          break;
      }
    }
    return array;
  }
}
