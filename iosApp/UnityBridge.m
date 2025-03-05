//
//  UnityBridge.m
//  iosApp
//
//  Created by Krishna Poddar on 04/03/25.
//  Copyright © 2025 orgName. All rights reserved.
//


////  Created by Krishna Poddar on 24/02/25.
////
//
//
// UnityBridge.m
// UnityBridge.m
#import "UnityBridge.h"
#import <mach-o/ldsyms.h>

// Handle different Mach-O types
#if EXECUTABLE
extern const struct mach_header_64 _mh_execute_header;
#define MACH_HEADER _mh_execute_header
#elif BUNDLE
extern const struct mach_header_64 _mh_bundle_header;
#define MACH_HEADER _mh_bundle_header
#else
extern const struct mach_header_64 _mh_dylib_header;
#define MACH_HEADER _mh_dylib_header
#endif

@implementation UnityBridge

+ (UnityFramework *)getUnityFramework
{
    NSString* bundlePath = [[NSBundle mainBundle] bundlePath];
    NSString* frameworkPath = [bundlePath stringByAppendingPathComponent: @"Frameworks/UnityFramework.framework"];
    
    NSBundle* bundle = [NSBundle bundleWithPath: frameworkPath];
    if ([bundle isLoaded] == false) [bundle load];
    
    UnityFramework* ufw = [bundle.principalClass getInstance];
    if (![ufw appController])
    {
        [ufw setExecuteHeader:(void*)&MACH_HEADER];
    }
    [ufw setDataBundleId:"com.unity3d.framework"];
    return ufw;
}

@end
