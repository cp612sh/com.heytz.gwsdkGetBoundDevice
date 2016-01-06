package com.heytz.gwsdkGetBoundDevice;

import android.content.Context;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiErrorCode;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * This class wrapping Gizwits WifiSDK called from JavaScript.
 */
public class gwsdkGetBoundDevice extends CordovaPlugin {

    private CallbackContext airLinkCallbackContext;
    private Context context;
    private String _appID;
    private String _productKey;
    private String _uid;
    private String _token;


    private XPGWifiSDKListener wifiSDKListener = new XPGWifiSDKListener() {

        private JSONObject toJSONObjfrom(XPGWifiDevice device) {
            JSONObject json = new JSONObject();
            try {
                json.put("did", device.getDid());
                json.put("macAddress", device.getMacAddress());
                json.put("isLAN", device.isLAN() ? "1" : "0");
                json.put("isOnline", device.isOnline() ? "1" : "0");
                json.put("isConnected", device.isConnected() ? "1" : "0");
                json.put("isDisabled", device.isDisabled() ? "1" : "0");
                json.put("isBind", device.isBind(_uid) ? "1" : "0");
            } catch (JSONException e) {

            }
            return json;
        }

        private List<XPGWifiDevice> _devicesList;

        private Boolean hasDone(List<XPGWifiDevice> deviceList) {
            if (_devicesList == null) return false;
            return _devicesList.size() == deviceList.size();
        }

        @Override
        public void didDiscovered(int result, List<XPGWifiDevice> devicesList) {
            if (result == XPGWifiErrorCode.XPGWifiError_NONE && devicesList.size() > 0) {
                if (hasDone(devicesList)) {
                    JSONArray cdvResult = new JSONArray();

                    for (int i = 0; i < devicesList.size(); i++) {
                        cdvResult.put(toJSONObjfrom(devicesList.get(i)));
                    }
                    _devicesList = null;
                    airLinkCallbackContext.success(cdvResult);

                } else {
                    _devicesList = devicesList;
                }
            } else {
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
            if (_appID == null) {
            XPGWifiSDK.sharedInstance().startWithAppID(context, _appID);
            // set listener
            XPGWifiSDK.sharedInstance().setListener(wifiSDKListener);
            }
            this._productKey = args.getString(2);
            this._uid = args.getString(3);
            this._token = args.getString(1);
            //todo: check parameters
            this.airLinkCallbackContext = callbackContext;
            this.start();
            return true;
        }
        if(action.equals("dealloc")){
            this.dealloc();
            return true;
        }
        return false;
    }

    private void start() {
        XPGWifiSDK.sharedInstance().getBoundDevices(_uid, _token, _productKey);
    }

    private void dealloc() {
        XPGWifiSDK.sharedInstance().setListener((XPGWifiSDKListener) null);
    }
}
