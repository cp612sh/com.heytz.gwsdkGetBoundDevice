/********* gwsdkGetBoundDevice.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <XPGWifiSDK/XPGWifiSDK.h>

@interface gwsdkGetBoundDevice : CDVPlugin<XPGWifiDeviceDelegate,XPGWifiSDKDelegate> {
    // Member variables go here.
    NSString * _appId;
    BOOL isDiscoverLock;
}

-(void)start:(CDVInvokedUrlCommand *)command;

@property (strong,nonatomic) CDVInvokedUrlCommand * commandHolder;
@property (strong, nonatomic) NSArray * _deviceList;

@end

@implementation gwsdkGetBoundDevice

@synthesize commandHolder;
@synthesize _deviceList;

-(void)pluginInitialize{
}

-(void)initSdkWithAppId:(CDVInvokedUrlCommand *) command{
    if(!_appId){
        _appId = command.arguments[0];
        [XPGWifiSDK startWithAppID:_appId];
    }
}

-(void) setDelegate{
    if(!([XPGWifiSDK sharedInstance].delegate)){
        [XPGWifiSDK sharedInstance].delegate = self;
    }
}

/**
 * @brief 回调接口，返回发现设备的结果
 * @param deviceList：为 XPGWifiDevice* 的集合
 * @param result：0为成功，其他失败
 * @see 触发函数：[XPGWifiSDK getBoundDevicesWithUid:token:specialProductKeys:]
 */
-(void)start:(CDVInvokedUrlCommand *)command{
    
    [self initSdkWithAppId:command];
    [self setDelegate];
    
    self.commandHolder = command;
    
    isDiscoverLock = true;
    [[XPGWifiSDK sharedInstance] getBoundDevicesWithUid:command.arguments[3] token:command.arguments[1] specialProductKeys:command.arguments[2], nil];
}

- (BOOL)hasDone:(NSArray *)devicList{
    if(_deviceList == nil) return false;
    return (_deviceList.count == devicList.count);
}

- (void)XPGWifiSDK:(XPGWifiSDK *)wifiSDK didDiscovered:(NSArray *)deviceList result:(int)result{
    //if(isDiscoverLock){
    if ([self hasDone:deviceList]) {
        if (result == 0 && deviceList.count > 0) {
            NSMutableArray *jsonArray = [[NSMutableArray alloc] init];
            for (XPGWifiDevice *device in deviceList){
                NSString *did=device.did;
                NSString * mac = device.macAddress;
                NSString * isOnline =  device.isOnline ? @"1" :@"0";
                NSMutableDictionary * d = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                           did, @"did",
                                           // device.ipAddress, @"ipAddress",
                                           mac, @"macAddress",
                                           // device.passcode, @"passcode",
                                           // device.productKey, @"productKey",
                                           // device.productName, @"productName",
                                           // device.remark, @"remark",
                                           //device.ui, @"ui",
                                           //  device.isConnected, @"isConnected",
                                           //  device.isDisabled, @"isDisabled",
                                           //device.isLAN, @"isLAN",
                                           isOnline, @"isOnline",
                                           @"",@"error",
                                           nil];
                [jsonArray addObject:d];
                
            }
             _deviceList = nil;
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:jsonArray];
            //[pluginResult setKeepCallbackAsBool:true];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.commandHolder.callbackId];
           
            
        }else{
            //[self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR] callbackId:self.commandHolder.callbackId];
        }
    }
    else{
        _deviceList = deviceList;
    }
}

- (void)dealloc
{
    NSLog(@"//====dealloc...====");
    [XPGWifiSDK sharedInstance].delegate = nil;
}

- (void)dispose{
    NSLog(@"//====disposed...====");
}
@end

