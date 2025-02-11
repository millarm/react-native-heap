package com.heapanalytics.reactnative;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.heapanalytics.android.Heap;
import com.heapanalytics.android.internal.HeapImpl;

import java.util.HashMap;
import java.util.Map;

public class RNHeapLibraryModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNHeapLibraryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNHeap";
  }

  @ReactMethod
  public void setAppId(String appId) {
    // Currently, the Android tracker will refuse to initialize if it detects that
    // the bytecode hasn't been instrumented for autotrack. Until we are ready
    // to incorporate autotrack, instruct the Android tracker to skip those
    // checks.
    HeapImpl.skipInstrumentorChecks = true;
    Heap.init(this.reactContext, appId);
  }

  @ReactMethod
  public void identify(String identity) {
    Heap.identify(identity);
  }

  @ReactMethod
  public void resetIdentity() {
    Heap.resetIdentity();
  }

  private static Map<String, String> convertToStringMap(ReadableMap readableMap) {
    Map<String, String> stringMap = new HashMap<>();
    ReadableMapKeySetIterator mapIterator = readableMap.keySetIterator();
    while (mapIterator.hasNextKey()) {
      String key = mapIterator.nextKey();
      switch (readableMap.getType(key)) {
      case Null:
        stringMap.put(key, null);
        break;
      case Number:
        stringMap.put(key, String.valueOf(readableMap.getDouble(key)));
        break;
      case Boolean:
        stringMap.put(key, String.valueOf(readableMap.getBoolean(key)));
        break;
      case String:
        stringMap.put(key, readableMap.getString(key));
        break;
      case Array:
      case Map:
        // The JS bridge will flatten maps and arrays in a uniform manner across both
        // platforms.
        // If we get them at this point, we shouldn't continue.
        throw new RNHeapException("Property objects must be flattened before being sent across the JS bridge.");
      }
    }
    return stringMap;
  }

  @ReactMethod
  public void addUserProperties(ReadableMap properties) {
    Heap.addUserProperties(convertToStringMap(properties));
  }

  @ReactMethod
  public void addEventProperties(ReadableMap properties) {
    Heap.addEventProperties(convertToStringMap(properties));
  }

  @ReactMethod
  public void clearEventProperties() {
    Heap.clearEventProperties();
  }

  @ReactMethod
  public void removeEventProperty(String property) {
    Heap.removeEventProperty(property);
  }

  @ReactMethod
  public void track(String event, ReadableMap payload) {
    Heap.track(event, convertToStringMap(payload));
  }
}
