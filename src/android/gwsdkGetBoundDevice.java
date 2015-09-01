package com.heytz.gwsdkGetBoundDevice;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class wrapping Gizwits WifiSDK called from JavaScript.
 */
public class gwsdkGetBoundDevice extends CordovaPlugin {

    private CallbackContext airLinkCallbackContext;
    private Context context;
    private String _appID;
    private String _productKey;
    private String _uid;


    private XPGWifiSDKListener wifiSDKListener = new XPGWifiSDKListener() {
        @Override
        public void didSetDeviceWifi(int error, XPGWifiDevice device) {
            JSONObject json = new JSONObject();
            if (error == 0) {

                try {
                    json.put("did", device.getDid());
                    json.put("ipAddress", device.getIPAddress());
                    json.put("macAddress", device.getMacAddress());
                    json.put("passcode", device.getPasscode());
                    json.put("productKey", device.getProductKey());
                    json.put("productName", device.getProductName());
                    json.put("remark", device.getRemark());
                    json.put("ui", device.getUI());
                    json.put("isConnected", device.isConnected());
                    json.put("isDisabled", device.isDisabled());
                    json.put("isLAN", device.isLAN());
                    json.put("isOnline", device.isOnline());
                    json.put("error", "");
                } catch (JSONException e) {
                    //e.printStackTrace();
                }

                if (device.getDid().length() == 22 && device.getProductKey().length() == 32) {
                    airLinkCallbackContext.success(json);
                }
            } else {
                if (device.getProductKey().length() == 32) {
                    try {
                        json.put("did", device.getDid());
                        json.put("ipAddress", device.getIPAddress());
                        json.put("macAddress", device.getMacAddress());
                        json.put("passcode", device.getPasscode());
                        json.put("productKey", device.getProductKey());
                        json.put("productName", device.getProductName());
                        json.put("remark", device.getRemark());
                        json.put("ui", device.getUI());
                        json.put("isConnected", device.isConnected());
                        json.put("isDisabled", device.isDisabled());
                        json.put("isLAN", device.isLAN());
                        json.put("isOnline", device.isOnline());
                        json.put("error", "");
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                    airLinkCallbackContext.error(json);
                } else {
                    try {
                        json.put("error", "timeout");
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                    airLinkCallbackContext.error(json);
                }
            }
        }

        @Override
        @Override
        protected void didDiscovered(int result, List<XPGWifiDevice> devicesList) {
            if(result==XPGWifiErrorCode.XPGWifiError_NONE && devicesList.size() > 0){
                //获取设备列表
                xpgWifiDeviceList = devicesList;
            }else{
                //获取失败或未发现设备，重试
            }
        }
    };


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
        context = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("start")) {
            this._appID = args.getString(0);
            this._productKey = args.getString(2);
            this._uid = args.getString(3);
            this._token = args.getString(1);
            this.airLinkCallbackContext = callbackContext;
            this.start();
            return true;
        }
        return false;
    }

    private void start() {
       // String appID = appId;

        XPGWifiSDK.sharedInstance().startWithAppID(context, _appID);

        // set listener
        XPGWifiSDK.sharedInstance().setListener(wifiSDKListener);

      //  if (wifiSSID != null && wifiSSID.length() > 0 && wifiKey != null && wifiKey.length() > 0) {
        //    airLinkCallbackContext = callbackContext;
            XPGWifiSDK.sharedInstance().gwsdkGetBoundDevice(_uid,_token,_productKey);

//        } else {
//            callbackContext.error("args is empty or null");
//        }
    }
}