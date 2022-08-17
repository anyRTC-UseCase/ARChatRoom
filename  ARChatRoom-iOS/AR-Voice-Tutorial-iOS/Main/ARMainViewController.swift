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

//状态栏字体颜色，默认default
var StatusBarTextColor: Bool = false

class ARMainViewController: ARBaseViewController {

    @IBOutlet weak var channelIdTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        //登录
        customLoadingView(text: "登录中",count: MAXFLOAT)
        localUserModel = getUserInformation()
        guard let account = localUserModel!.uid, account.count > 0 else {
            return
        }
        ARVoiceRtm.rtmKit?.login(byToken: nil, user: account, completion: { [weak self] (errorCode) in
            guard errorCode == .ok else {
                return
            }
            ARVoiceRtm.status = .online
            self?.setLocalUserAttributes()
            self?.removeLoadingView()
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
        if ARVoiceRtm.status == .online {
            if self.children.count != 0 {
               XHToast.showCenter(withText: "当前正在语聊房中")
               return
           }
           
           var channelId: String?
           (sender.tag == 50) ? (channelId = self.channelIdTextField.text) : (channelId = localUserModel.uid)
           if strlen(channelId!) != 0 {
               //获取频道属性
               StatusBarTextColor = true
               channelIdTextField.resignFirstResponder()
               customLoadingView(text: "连接中", count: MAXFLOAT)
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
                       chatModel.roomName = dic.object(forKey: "roomName") as? String ?? ""
                       chatModel.announcement = dic.object(forKey: "notice") as? String
                       let record: String? = dic.object(forKey: "record") as? String
                       chatModel.record = false
                       if (record == "1") {
                           chatModel.record = true
                           if hosterId == localUserModel.uid {
                               self.deleteChannel(keys: ["record"])
                               chatModel.record = false
                           }
                       }
                       let musicValue = dic.object(forKey: "music") as? String
                       if hosterId == localUserModel?.uid && musicValue != nil {
                           self.deleteChannel(keys: ["music"])
                       }
                       for index in 0...8 {
                           let key: String! = String(format: "seat%d", index)
                           var uid: String? = dic.object(forKey: key as Any) as? String
                           (uid == nil) ? (uid = "") : nil
                           chatModel.seatDic.setValue(uid, forKey: key)
                       }
                   } else {
                       if localUserModel.uid == channelId {
                           hosterId = localUserModel.uid
                           self.setChannelAttribute(channelId: localUserModel.uid)
                       } else {
                           StatusBarTextColor = false
                           self.setNeedsStatusBarAppearanceUpdate()
                           self.removeLoadingView()
                           XHToast.showCenter(withText: "房间不存在")
                           return
                       }
                   }
                   
                   self.removeLoadingView()
                   chatModel.channelUid = hosterId
                   
                   if chatModel.isLock != nil && hosterId != localUserModel.uid {
                       self.chatTextField.placeholder = "请输入4位数字密码"
                       self.chatTextField.becomeFirstResponder()
                       self.chatTextField.isSecureTextEntry = true
                       self.chatTextField.addTarget(self, action: #selector(self.chatTextFieldLimit), for: .editingChanged)
                       self.confirmButton.addTarget(self, action: #selector(self.didSendChatTextField), for: .touchUpInside)
                       return
                   }
                   
                   self.joinChatRoom()
               })
           } else {
               XHToast.showCenter(withText: "请输入房间ID")
           }
        }
    }
    
    func joinChatRoom() {
        let vc: ARChatViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "AR_Chat") as! ARChatViewController
        (chatModel.channelUid == localUserModel?.uid) ? (vc.isHoster = true) : (vc.isHoster = false)
        
        self.addChild(vc)
        self.view.addSubview(vc.view)
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
            UIAlertController.showAlert(in: self, withTitle: "提示", message: "确定退出当前账号？", cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["退出"]) { [weak self] (alertVc, action, index) in
                if index == 2 {
                    ARVoiceRtm.rtmKit?.logout(completion: { (errorCode) in
                        print("logout sucess")
                    })
                    self!.removeUserInformation()
                    
                    let cachePath = self!.creatRecordPath()
                    //删除录音文件
                    let manger = FileManager.default
                    do {
                        try manger.removeItem(atPath: String(format: "%@", cachePath))
                    } catch {
                        print("Error occurs.")
                    }
                
                    let vc: UIViewController! = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "AR_Home")
                    UIApplication.shared.keyWindow?.rootViewController = vc
                }
            }
        } else {
            XHToast.showCenter(withText: "当前正在语聊房中")
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return StatusBarTextColor ? .lightContent : .default
    }
    
    @objc func chatTextFieldLimit() {
        //限制4位数字
        if isBlank(text: chatTextField.text) {
            confirmButton.alpha = 0.3
        } else {
            confirmButton.alpha = 1.0
            //限制4位数字
           if chatTextField.text!.count > 4 {
               let str: String = chatTextField.text!
               chatTextField.text = String(str.prefix(4))
           }
        }
    }
    
    @objc func didSendChatTextField() {
        if !isBlank(text: chatTextField.text) {
            //设置密码
            if chatTextField.text?.count != 4 {
                XHToast.showCenter(withText: "请输入4位数字密码")
                return
            }
            
            if chatModel.isLock == chatTextField.text {
                joinChatRoom()
            } else {
                XHToast.showCenter(withText: "密码错误")
            }
            chatTextField.resignFirstResponder()
            chatTextField.text = ""
        }
    }
}
