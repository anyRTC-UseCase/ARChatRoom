//
//  ARChatModel.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/7.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARChatModel: NSObject {
    //频道id
    var channelId: String?
    //房主id
    var channelUid: String?
    //密码房
    var isLock: String?
    //上麦模式
    var isMicLock: Bool = false
    //房间公告
    var announcement: String?
    //主持人、游客
    var isHoster: Bool?
    //房间名称
    var roomName: String = "一起来聊天吧"
    //欢迎语
    var welcome: String?
    //麦位
    var seatDic: NSMutableDictionary! = NSMutableDictionary()
    
    var seat0: String?
    var seat1: String?
    var seat2: String?
    var seat3: String?
    var seat4: String?
    var seat5: String?
    var seat6: String?
    var seat7: String?
    var seat8: String?
    
    //禁麦
    var muteMicList: NSArray!
    //禁言
    var muteInputList: NSArray!
    //音效开关
    var sound: Bool = false
    //0 ~ 8
    var currentMic: NSInteger = 9
    //录音开关
    var record: Bool = false
    //音乐播放开关
    var musicDic: NSMutableDictionary! = NSMutableDictionary()
    //非自由模式麦序
    var waitList: NSMutableArray = NSMutableArray()
    //非自由模式麦序model
    var waitModelList: NSMutableArray = NSMutableArray()
}
