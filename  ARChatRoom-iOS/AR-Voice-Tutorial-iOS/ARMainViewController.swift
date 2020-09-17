//
//  ARMainViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/2.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

let ARScreenHeight = UIScreen.main.bounds.size.height
let ARScreenWidth = UIScreen.main.bounds.size.width

var chatModel: ARChatModel!
var localUserModel: ARUserModel!

class ARMainViewController: UIViewController {

    @IBOutlet weak var channelIdTextField: UITextField!
    
    var status: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        localUserModel = getUserInformation()
        guard let account = localUserModel!.uid, account.count > 0 else {
            return
        }
        
        ARVoiceRtm.updateRtmkit(delegate: self)
        //登录
        ARVoiceRtm.rtmKit?.login(byToken: nil, user: account, completion: { (errorCode) in
            guard errorCode == .ok else {
                return
            }
            ARVoiceRtm.status = .online
            self.setLocalUserAttributes()
        })
    }
    
    func setLocalUserAttributes() {
        let arr: NSMutableArray = NSMutableArray()
        
        let attribute0: ARtmAttribute = ARtmAttribute()
        attribute0.key = "name"
        attribute0.value = localUserModel.name!
        arr.add(attribute0)
        
        let attribute1: ARtmAttribute = ARtmAttribute()
        attribute1.key = "sex"
        (localUserModel.sex!) ? (attribute1.value = "1") : (attribute1.value = "0")
        arr.add(attribute1)
        
        let attribute2: ARtmAttribute = ARtmAttribute()
        attribute2.key = "head"
        attribute2.value = localUserModel.head!
        arr.add(attribute2)
        //设置个人属性
        ARVoiceRtm.rtmKit?.setLocalUserAttributes((arr as! [ARtmAttribute]), completion: {
            (errorCode) in
            print("setLocalUserAttributes")
        })
    }
    
    @IBAction func didClickEnterButton(_ sender: UIButton) {
         if self.children.count != 0 {
            XHToast.showCenter(withText: "当前正在语聊房中")
            return
        }
        
        var channelId: String?
        (sender.tag == 50) ? (channelId = self.channelIdTextField.text) : (channelId = localUserModel.uid)
        if strlen(channelId!) != 0 {
            //获取频道属性
            status = true
            customLoadingView()
            channelIdTextField.resignFirstResponder()
            ARVoiceRtm.rtmKit?.getChannelAllAttributes(channelId!, completion:{ (attributes, errorCode) in
                var hosterId: String?
                chatModel = nil
                chatModel = ARChatModel()
                chatModel.channelId = channelId
                
                if attributes?.count != 0 {
                    let dic: NSMutableDictionary = NSMutableDictionary()
                    for attribute in attributes! {
                        dic.setValue(attribute.value, forKey: attribute.key)
                    }
     
                    hosterId = dic.object(forKey: "host") as? String
                    chatModel.isLock = dic.object(forKey: "isLock") as? String
                    (dic.object(forKey: "isMicLock") as? String == "1") ? (chatModel.isMicLock = true) : (chatModel.isMicLock = false)
                    chatModel.roomName = dic.object(forKey: "roomName") as? String
                    chatModel.announcement = dic.object(forKey: "notice") as? String
                    chatModel.welcome = dic.object(forKey: "welecomeTip") as? String
                    for index in 0...8 {
                        let key: String! = String(format: "seat%d", index)
                        var uid: String? = dic.object(forKey: key as Any) as? String
                        (uid == nil) ? (uid = "") : nil
                        chatModel.seatDic.setValue(uid, forKey: key)
                    }
                } else {
                    print("no Attributes")
                    if localUserModel.uid == channelId {
                        hosterId = localUserModel.uid
                        self.setChannelAttribute(channelId: localUserModel.uid)
                    } else {
                        self.removeLoadingView()
                        XHToast.showCenter(withText: "房间不存在")
                        return
                    }
                }
                
                self.removeLoadingView()
                let vc: ARChatViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "AR_Chat") as! ARChatViewController
                (hosterId == localUserModel?.uid) ? (vc.isHoster = true) : (vc.isHoster = false)
                chatModel.channelUid = hosterId
                self.addChild(vc)
                self.view.addSubview(vc.view)
            })
        } else {
            XHToast.showCenter(withText: "请输入房间ID")
        }
    }
    
    func setChannelAttribute(channelId: String!) {
        let attribute0: ARtmChannelAttribute = ARtmChannelAttribute()
        attribute0.key = "host"
        attribute0.value = localUserModel.uid!
        
        let attribute1: ARtmChannelAttribute = ARtmChannelAttribute()
        attribute1.key = "roomName"
        attribute1.value = "一起聊天吧"
        
        let options: ARtmChannelAttributeOptions = ARtmChannelAttributeOptions()
        ARVoiceRtm.rtmKit?.setChannel(channelId, attributes: [attribute0,attribute1], options: options, completion: { (errorCode) in
            print("setChannel sucess")
        })
    }
    
    @IBAction func didClickBackButton(_ sender: Any) {
        if self.children.count == 0 {
            ARVoiceRtm.rtmKit?.logout(completion: { (errorCode) in
                print("logout sucess")
            })
            removeUserInformation()
            let vc: UIViewController! = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "AR_Home")
            UIApplication.shared.keyWindow?.rootViewController = vc
        } else {
            XHToast.showCenter(withText: "当前正在语聊房中")
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return status ? .lightContent : .default
    }
}

extension ARMainViewController: ARtmDelegate {
    //连接状态改变回调
    func rtmKit(_ kit: ARtmKit, connectionStateChanged state: ARtmConnectionState, reason: ARtmConnectionChangeReason) {
        
    }
}
