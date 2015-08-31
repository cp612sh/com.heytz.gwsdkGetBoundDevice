/********* gwsdkGetBoundDevice.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <XPGWifiSDK/XPGWifiSDK.h>

@interface gwsdkGetBoundDevice : CDVPlugin<XPGWifiDeviceDelegate,XPGWifiSDKDelegate> {
    // Member variables go here.
}

-(void)getBoundDevicesWithUid:(CDVInvokedUrlCommand *)command;

@property (strong,nonatomic) CDVInvokedUrlCommand * commandHolder;

@end

@implementation gwsdkGetBoundDevice

@synthesize commandHolder;

-(void)pluginInitialize{
}

/**
 * @brief 回调接口，返回发现设备的结果
 * @param deviceList：为 XPGWifiDevice* 的集合
 * @param result：0为成功，其他失败
 * @see 触发函数：[XPGWifiSDK getBoundDevicesWithUid:token:specialProductKeys:]
 */
-(void)getBoundDevicesWithUid:(CDVInvokedUrlCommand *)command{

    [XPGWifiSDK startWithAppID:command.arguments[0]];
    [XPGWifiSDK sharedInstance].delegate = self;

    self.commandHolder = command;
    [[XPGWifiSDK sharedInstance] getBoundDevicesWithUid:command.arguments[3] token:command.arguments[1] specialProductKeys:command.arguments[2], nil];
}

- (void)XPGWifiSDK:(XPGWifiSDK *)wifiSDK didDiscovered:(NSArray *)deviceList result:(int)result{
    if (result == 0) {
        for (XPGWifiDevice *device in deviceList){
            NSDictionary *d = [NSDictionary dictionaryWithObjectsAndKeys:
                               device.did, @"did",
                               device.ipAddress, @"ipAddress",
                               device.macAddress, @"macAddress",
                               device.passcode, @"passcode",
                               device.productKey, @"productKey",
                               device.productName, @"productName",
                               device.remark, @"remark",
                               //device.ui, @"ui",
                               device.isConnected, @"isConnected",
                               device.isDisabled, @"isDisabled",
                               device.isLAN, @"isLAN",
                               device.isOnline, @"isOnline",
                               @"",@"error",
                               nil];

            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:d];
            [pluginResult setKeepCallbackAsBool:true];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.commandHolder.callbackId];
        }
    }else{
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR] callbackId:self.commandHolder.callbackId];
    }
}

- (void)dispose{
    NSLog(@"//====disposed...====");
}
@end

