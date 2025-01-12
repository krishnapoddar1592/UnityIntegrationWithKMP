//
//  UnityFrameworkWrapper.h
//  iosApp
//
//  Created by Krishna Poddar on 12/01/25.
//  Copyright © 2025 orgName. All rights reserved.
//


// UnityFrameworkWrapper.h
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <UnityFramework/UnityFramework.h>

@interface UnityFrameworkWrapper : NSObject <UnityFrameworkListener>

+ (instancetype)shared;

- (void)initialize;
- (UIView *)getUnityView;
- (void)sendMessage:(NSString *)gameObject method:(NSString *)method message:(NSString *)message;
- (void)cleanup;

@end
