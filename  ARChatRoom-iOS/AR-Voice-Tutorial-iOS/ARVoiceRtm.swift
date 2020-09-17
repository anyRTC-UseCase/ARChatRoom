//
//  ARVoiceRtm.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/3.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

enum ARLoginStatus {
    case online, offline
}

class ARVoiceRtm: NSObject {
    
    static let rtmKit = ARtmKit.init(appId: AppID, delegate: nil)
    static var status: ARLoginStatus = .offline
    
    static func updateRtmkit(delegate: ARtmDelegate) {
        guard let rtmKit = rtmKit else {
            return
        }
        rtmKit.aRtmDelegate = delegate
    }
}
