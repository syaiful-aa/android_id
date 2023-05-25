package dev.fluttercommunity.android_id

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import android.os.Build
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** AndroidIdPlugin */
class AndroidIdPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private lateinit var contentResolver : ContentResolver

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    contentResolver = flutterPluginBinding.applicationContext.contentResolver
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "android_id")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getId") {
      result.success(getAndroidId())
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  @SuppressLint("HardwareIds", "deprecation")
  private fun getAndroidId(): String? {
    var id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

    if (id.isNullOrBlank()) {
      val abi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Build.SUPPORTED_ABIS.first()
      } else {
        Build.CPU_ABI
      }

      id = "35" + //we make this look like a valid IMEI
              Build.BOARD.length % 10 + Build.BRAND.length % 10 +
              abi.length % 10 + Build.DEVICE.length % 10 +
              Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
              Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
              Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
              Build.TAGS.length % 10 + Build.TYPE.length % 10 +
              Build.USER.length % 10 ; //13 digits
    }

    return id
  }
}
