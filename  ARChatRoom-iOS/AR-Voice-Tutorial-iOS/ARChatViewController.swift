//
//  ARChatViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/2.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtcKit
import IQKeyboardManager
import SDWebImage
import ARtmKit
import SnapKit

var rtcKit: ARtcEngineKit!

class ARChatViewController: UIViewController {
    
    @IBOutlet weak var stackView0: UIStackView!
    @IBOutlet weak var stackView1: UIStackView!
    @IBOutlet weak var announcementButton: UIButton!
    @IBOutlet weak var onlineButton: UIButton!
    @IBOutlet weak var chatNameButton: UIButton!
    @IBOutlet weak var channelIdLabel: UILabel!
    @IBOutlet weak var signalButton: UIButton!
    @IBOutlet weak var chatButton: UIButton!
    
    @IBOutlet weak var musicButton: UIButton!
    @IBOutlet weak var musicView: UIView!
    /** 音频开关 */
    @IBOutlet weak var audioButton: UIButton!
    /** 上下麦 -- 游客 */
    @IBOutlet weak var micButton: UIButton!
    /** 麦序 */
    @IBOutlet weak var listButton: UIButton!
    /** 更多 */
    @IBOutlet weak var moreButton: UIButton!
    /** 音效高度 */
    @IBOutlet weak var soundConstraint: NSLayoutConstraint!
    /** 频道成员 */
    fileprivate var memberList = NSMutableArray()
    var keyBoardView: UIView!
    var confirmButton: UIButton!
    
    var chatTextField: UITextField!
    var channelId: String!
    var isHoster: Bool!
    /** 游客正在申请上麦(非自由) */
    var applyMic: Bool = false
    
    let soundList = ["哈哈哈","起哄","鼓掌","尴尬","乌鸦","哎呀我滴妈"]
    let colorList = ["#CE5850","#F29025","#8252F6","#6AB71C","#03A3C3","#0A6BBE"]
    let giftList = ["icon_lollipop_effect","icon_stars_effect","icon_hats_effect","icon_unicorn_effect","icon_crown_effect","icon_chest_effect","icon_car_effect","icon_rockets_effect"]
    
    weak var logVC: LogViewController?
    var micArr = NSMutableArray()
    fileprivate var rtmChannel: ARtmChannel!

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        UIApplication.shared.isIdleTimerDisabled = true
        announcementButton.layer.borderColor = UIColor.red.cgColor
        onlineButton.layer.borderColor = UIColor.red.cgColor
        channelId = chatModel.channelId
        channelIdLabel.text = String(format: "ID:%@",channelId)
        chatNameButton.setTitle(chatModel.roomName, for: .normal)
        chatButton.contentHorizontalAlignment = .left
        musicView.layer.borderColor = UIColor.lightGray.cgColor
        self.view.addSubview(getInputAccessoryView())
        
        initializeEngine()
        initializeHost()
        
        for i in 1 ..< 9 {
            let micView: ARMicView = ARMicView.videoView()
            micView.tag = i
            micView.delegate = self
            micView.titleLabel.text = String(i) + "号麦位"
            micView.uid = chatModel.seatDic.object(forKey: String(format: "seat%d", i)) as? String
            (i < 5) ? (stackView0.addArrangedSubview(micView)) : (stackView1.addArrangedSubview(micView))
            micArr.add(micView)
        }
        
        let messageModel: ARLogModel = ARLogModel()
        messageModel.contentType = .warning
        messageModel.content = "系统：官方倡导绿色交友，并24小时对互动房间进行巡查，如果发现低俗、骂人、人身攻击等违规行为。官方将进行封房封号处理"
        self.logVC?.log(logModel: messageModel)
        
        NotificationCenter.default.addObserver(self,selector:#selector(keyboardChange(notify:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self,selector:#selector(keyboardChange(notify:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(soundEffect), name: NSNotification.Name(rawValue:"ARChatNotificationSound"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(sendoutGift), name: NSNotification.Name(rawValue:"ARChatNotificationGift"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(setupPassword), name: NSNotification.Name(rawValue:"ARChatNotificationPassWord"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(sendoutGiftFromUid), name: NSNotification.Name(rawValue:"ARChatNotificationGiftFromUid"), object: nil)
    }
    
    func initializeEngine() {
        //实例化rtc实例对象
        rtcKit = ARtcEngineKit.sharedEngine(withAppId: AppID, delegate: self)
        rtcKit.setChannelProfile(.profileiveBroadcasting)
        if  isHoster {
            rtcKit.setClientRole(.broadcaster)
        }
        
        //加入房间
        rtcKit.joinChannel(byToken: "", channelId: channelId!, uid: localUserModel.uid) {(channel, uid, elapsed) -> Void in
            print("joinChannel")
        }
        //启用说话者音量提示
        rtcKit.enableAudioVolumeIndication(200, smooth: 3, report_vad: true)
        
        ARVoiceRtm.rtmKit?.aRtmDelegate = self
        //创建rtm频道
        rtmChannel = ARVoiceRtm.rtmKit?.createChannel(withId: channelId, delegate: self)
        rtmChannel.join { (errorCode) in
            print("join channel")
            self.getChannelMembers()
        }
    }
    
    func initializeHost() {
        let micView: ARMicView = ARMicView.videoView()
        micView.titleLabel.text = "主持麦"
        micView.delegate = self
        self.view.addSubview(micView)
        micView.snp.makeConstraints { (make) in
            make.top.equalTo(onlineButton.snp.bottom).offset(5)
            make.width.equalTo(64)
            make.height.equalTo(82)
            make.centerX.equalTo(self.view.snp.centerX)
        }
        micArr.insert(micView, at: 0)
        
        if isHoster {
            //主持人上麦
            let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
            channelAttribute.key = "seat0"
            channelAttribute.value = localUserModel!.uid!
            addOrUpdateChannel(attribute: channelAttribute)
            chatModel.currentMic = 0
        }
    }
    
    func getChannelMembers() {
        rtmChannel.getMembersWithCompletion { (members, errorCode) in
            if members?.count != 0 {
                for object in members! {
                    let member: ARtmMember = object
                    self.getUserAllAttribute(uid: member.uid)
                }
            }
        }
    }
    
    func getUserAllAttribute(uid: String!) {
        ARVoiceRtm.rtmKit?.getUserAllAttributes(uid, completion:{(attributes, uid, errorCode) in
            let chatUserModel = ARChatUserModel()
            chatUserModel.uid = uid
            for attribute in attributes! {
                if attribute.key == "sex" {
                    (attribute.value == "1") ? (chatUserModel.sex = true) : (chatUserModel.sex = false)
                }
               
                if attribute.key == "name" {
                    chatUserModel.name = attribute.value
                }
               
                if attribute.key == "head" {
                    chatUserModel.head = attribute.value
               }
            }
            self.memberList.add(chatUserModel)
            
            for (_,object) in self.micArr.enumerated() {
                if object is ARMicView {
                    let micView: ARMicView = object as! ARMicView
                    if micView.uid ==  chatUserModel.uid {
                        micView.chatUserModel = chatUserModel
                    }
                }
            }
            
            for object in chatModel.waitModelList {
                let model: ARChatUserModel = object as! ARChatUserModel
                model.name = chatUserModel.name
                model.head = chatUserModel.head
                model.sex = chatUserModel.sex
            }
        })
    }
    
    func getInputAccessoryView() -> UIView {
        keyBoardView = UIView.init(frame: CGRect.init(x: 0, y: ARScreenHeight, width: ARScreenWidth, height: 53))
        keyBoardView.backgroundColor = UIColor.init(red: 247/255, green: 247/255, blue: 247/255, alpha: 1)
        
        chatTextField = UITextField.init(frame: CGRect.init(x: 10, y: 8, width: ARScreenWidth - 93, height: 38));
        chatTextField.placeholder = "聊点什么吧"
        chatTextField.font = UIFont.systemFont(ofSize: 14)
        chatTextField.layer.masksToBounds = true
        chatTextField.layer.cornerRadius = 5
        chatTextField.returnKeyType = .send
        chatTextField.backgroundColor = UIColor.white
        chatTextField.delegate = self
        chatTextField.addTarget(self, action: #selector(chatTextFieldLimit), for: .editingChanged)
        keyBoardView.addSubview(chatTextField)
        
        confirmButton = UIButton.init(type: .custom)
        confirmButton.frame = CGRect.init(x: ARScreenWidth - 73, y: 8, width: 63, height: 38)
        confirmButton.setTitleColor(UIColor.white, for: .normal)
        confirmButton.backgroundColor = UIColor.init(red: 64/255, green: 163/255, blue: 251/255, alpha: 1)
        confirmButton.layer.masksToBounds = true
        confirmButton.titleLabel?.font = UIFont.systemFont(ofSize: 12)
        confirmButton.layer.cornerRadius = 5
        confirmButton.alpha = 0.3
        confirmButton.setTitle("确定", for:.normal)
        confirmButton.addTarget(self, action: #selector(didSendChatTextField), for: .touchUpInside)
        keyBoardView.addSubview(confirmButton)
        return keyBoardView
    }
    
    @objc func keyboardChange(notify:NSNotification){
        //时间
        let duration : Double = notify.userInfo![UIResponder.keyboardAnimationDurationUserInfoKey] as! Double
        if notify.name == UIResponder.keyboardWillShowNotification {
            //键盘高度
            let keyboardY : CGFloat = (notify.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as! NSValue).cgRectValue.size.height
            let high = UIScreen.main.bounds.size.height - keyboardY - 53
            
            UIView.animate(withDuration: duration) {
                self.keyBoardView.frame = CGRect(x: 0, y: high, width: ARScreenWidth, height: 53)
                self.view.layoutIfNeeded()
            }
        } else if notify.name == UIResponder.keyboardWillHideNotification {
            
            UIView.animate(withDuration: duration, animations: {
                self.keyBoardView.frame = CGRect(x: 0, y: ARScreenHeight, width: ARScreenWidth, height: 53)
                self.view.layoutIfNeeded()
            })
        }
    }
    
    @objc func didSendChatTextField() {
        
        if chatTextField.text?.count != 0 {
            if PasswordInput {
                //设置密码
                let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                channelAttribute.key = "isLock"
                channelAttribute.value = chatTextField.text!
                addOrUpdateChannel(attribute: channelAttribute)
            } else {
                addChannelMessageLog(uid: localUserModel.uid, content: chatTextField.text)
                let dic: NSDictionary! = ["cmd": "msg","content": chatTextField.text as Any]
                sendChannelMessage(value: getJSONStringFromDictionary(dictionary: dic))
            }
            chatTextField.resignFirstResponder()
            chatTextField.text = ""
        }
    }
    
    func addChannelMessageLog(uid:String!, content: String!) {
        let messageModel: ARLogModel = ARLogModel()
        messageModel.contentType = .info
        messageModel.content = content
        for object in memberList {
            let userModel: ARChatUserModel? = object as? ARChatUserModel
            if userModel?.uid == uid {
                messageModel.userModel = userModel
            }
        }
        self.logVC?.log(logModel: messageModel)
    }
    
    func sendChannelMessage(value: String!) {
        let rtmMessage: ARtmMessage = ARtmMessage.init(text: value)
        let options: ARtmSendMessageOptions = ARtmSendMessageOptions()
        //发送频道消息
        rtmChannel.send(rtmMessage, sendMessageOptions: options) { (errorCode) in
            
        }
    }

    @objc func chatTextFieldLimit() {
        if chatTextField.text?.count == 0 {
            confirmButton.alpha = 0.3
        } else {
            confirmButton.alpha = 1.0
            if PasswordInput {
                //限制4位数字
                if chatTextField.text!.count > 4 {
                    let str: String = chatTextField.text!
                    chatTextField.text = String(str.prefix(4))
                }
            }
        }
    }
    
    @IBAction func didClickChatButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        if sender.tag == 50 {
            //最小化
//            gloabWindow?.isHidden = true
            self.view.isHidden = true
            let delegate  = UIApplication.shared.delegate as! AppDelegate
            let floatingView = ARFloatingView.init(frame: CGRect.zero)
            floatingView.clickDragViewBlock = { (dragView: WMDragView?) ->() in
                self.view.isHidden = false
                floatingView.removeFromSuperview()
            }
            delegate.window?.addSubview(floatingView)
        } else if (sender.tag == 51) {
            //关闭
            UIApplication.shared.isIdleTimerDisabled = false
            deleteChannel(keys: [String(format: "seat%d", chatModel.currentMic)])
            rtcKit.stopAudioMixing()
            rtcKit.leaveChannel(nil)
            ARtcEngineKit.destroy()
            
            rtmChannel.leave(completion: nil)
            ARVoiceRtm.rtmKit?.destroyChannel(withId: channelId)
            
            //dismissGloabWindow()
            self.willMove(toParent: nil)
            self.view.removeFromSuperview()
            self.removeFromParent()
        } else if (sender.tag == 52) {
            //音乐 -- 主持人
            if isHoster {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let nav: UINavigationController = storyboard.instantiateViewController(withIdentifier: "ARChat_MusicID") as! UINavigationController
                //let vc: ARMusicViewController = nav.viewControllers[0] as! ARMusicViewController
                nav.modalPresentationStyle = .fullScreen
                self.present(nav, animated: true, completion: nil)
            }
        } else if (sender.tag == 53) {
            //音频
            rtcKit.muteLocalAudioStream(sender.isSelected)
        } else if (sender.tag == 54) {
            //上下麦 -- 游客
            if !sender.isSelected {
                //下麦
                if (chatModel.currentMic > 0) && (chatModel.currentMic < 9) {
                    rtcKit.setClientRole(.audience)
                    rtcKit.muteLocalAudioStream(false)
                    
                    deleteChannel(keys: [String(format: "seat%d", chatModel.currentMic)])
                    let micView: ARMicView = micArr[chatModel.currentMic] as! ARMicView
                    micView.uid = ""
                    chatModel.currentMic = 9
                    audioButton.isHidden = true
                    audioButton.isSelected = false
                }
            } else {
                if chatModel.isMicLock! {
                    //申请上麦
                    applyLockMic(value: "1")
                } else {
                    //自动上麦
                    for (index,object) in micArr.enumerated() {
                        if object is ARMicView {
                            let micView: ARMicView = object as! ARMicView
                            if micView.uid?.count == 0 {
                                chatModel.currentMic = index
                                let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                                channelAttribute.key = String(format: "seat%d", index)
                                channelAttribute.value = localUserModel!.uid!
                                addOrUpdateChannel(attribute: channelAttribute)
                                rtcKit.setClientRole(.broadcaster)
                                self.audioButton.isHidden = false
                                self.micButton.isSelected = true
                                
                                micView.uid = localUserModel.uid!
                                break
                            }
                        }
                    }
                }
            }
        } else if (sender.tag == 55) {
            // 麦序 -- 主持人、游客
            
        } else if (sender.tag == 56) {
            //更多 -- 主持人

        } else if (sender.tag == 57) {
            //礼物
            
        } else if (sender.tag == 58) {
            //聊天
            chatTextField.becomeFirstResponder()
        }
    }
    
    func applyLockMic(value: String!) {
        applyMic = true
        let arr = chatModel.waitList
        if arr.count != 0 {
            for object in chatModel.waitList {
                let dic: NSDictionary! = (object as! NSDictionary)
                let uid: String? = dic.object(forKey: "userid") as? String
                if uid == localUserModel.uid {
                    arr.remove(dic as Any)
                }
            }
        }
        
        let dic = ["userid": localUserModel.uid, "seat": value]
        arr.add(dic)
        let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
        channelAttribute.key = "waritinglist"
        channelAttribute.value = getJSONStringFromArray(array: arr)
        addOrUpdateChannel(attribute: channelAttribute)
        micButton.isSelected = false
        micButton.isHidden = true
        listButton.isHidden = false
    }
    
    @objc func sendoutGift(nofi: Notification) {
        let result: String = nofi.userInfo!["gift"] as! String
        effectOfGift(giftName: giftList[Int(result)!])
        
        let dic: NSDictionary! = ["cmd": "gift", "giftId": result, "userId": ""]
        sendChannelMessage(value: getJSONStringFromDictionary(dictionary: dic))
    }
    
    @objc func sendoutGiftFromUid(nofi: Notification)  {
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.5) {
            let uid: String? = nofi.userInfo!["gift"] as? String
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: ARGiftViewController = storyboard.instantiateViewController(withIdentifier: "ARChat_Gift") as! ARGiftViewController
            vc.modalPresentationStyle = .overCurrentContext
            self.present(vc, animated: true, completion: nil)
        }
    }
    
    @objc func soundEffect(nofi: Notification) {
        let result: Bool = nofi.userInfo!["open"] as! Bool
        chatModel.sound = result
        if result {
            soundConstraint.constant = 40.0
        } else {
            soundConstraint.constant = 0.0
        }
    }
    
    @objc func setupPassword(nofi: Notification) {
        let result: Bool = nofi.userInfo!["state"] as! Bool
        if result {
            PasswordInput = true
            chatTextField.text = ""
            chatTextField.keyboardType = .numberPad
            chatTextField.placeholder = "请输入4位数字密码"
            chatTextField.becomeFirstResponder()
        } else {
            //取消密码
            deleteChannel(keys: ["isLock"])
            XHToast.showCenter(withText: "取消密码成功")
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }
        
        if identifier == "EmbedLogViewController",
            let vc = segue.destination as? LogViewController {
            self.logVC = vc
        } else if (identifier == "ARFansViewController") {
            let vc: ARFansViewController = segue.destination as! ARFansViewController
            vc.memberList = memberList
        } else if (identifier == "ARMicViewController") {
            let vc: ARMicViewController = segue.destination as! ARMicViewController
            vc.hoster = isHoster
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        IQKeyboardManager.shared().isEnabled = false
        listButton.isHidden = true
        self.setNeedsStatusBarAppearanceUpdate()
        if isHoster {
            //主持人
            micButton.isHidden = true
        } else {
            //观众
            audioButton.isHidden = true
            moreButton.isHidden = true
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        IQKeyboardManager.shared().isEnabled = true
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
        print(" ChatVc deinit")
    }
}

extension ARChatViewController: ARtcEngineDelegate, ARtmChannelDelegate, ARtmDelegate {
    
    func rtcEngine(_ engine: ARtcEngineKit, reportRtcStats stats: ARChannelStats) {
        //当前通话统计回调。 该回调在通话或直播中每两秒触发一次
        let rtt: NSInteger = stats.gatewayRtt
        signalButton.setTitle(String(format: "%dms", rtt), for: .normal)
        
        let signal:String!
        if rtt < 30 {
            signal = "icon_signal_0"
        } else if (rtt < 40) {
            signal = "icon_signal_1"
        } else if (rtt < 60) {
            signal = "icon_signal_2"
        } else if (rtt < 100) {
            signal = "icon_signal_3"
        } else {
            signal = "icon_signal_4"
        }
        signalButton.setImage(UIImage(named:signal), for: .normal)
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, reportAudioVolumeIndicationOfSpeakers speakers: [ARtcAudioVolumeInfo], totalVolume: Int) {
        //提示频道内谁正在说话、说话者音量及本地用户是否在说话的回调
        for object in speakers {
            let volumeInfo: ARtcAudioVolumeInfo! = object
            if (volumeInfo.uid == "0" && totalVolume > 0) {
                let micView: ARMicView! = (micArr[0] as! ARMicView)
                if micView.uid?.count != 0 {
                    micView.startAudioAnimation()
                }
            } else {
                for videoView in micArr {
                    let micView: ARMicView! = videoView as? ARMicView
                    if micView.uid == volumeInfo.uid {
                        micView.startAudioAnimation()
                    }
                }
            }
        }
    }
    
    func rtmKit(_ kit: ARtmKit, connectionStateChanged state: ARtmConnectionState, reason: ARtmConnectionChangeReason) {
        //rtm连接状态改变
    }
    
    func rtmKit(_ kit: ARtmKit, messageReceived message: ARtmMessage, fromPeer peerId: String) {
        //收到点对点消息回调
        let dic = getDictionaryFromJSONString(jsonString: message.text)
        let value: String? = dic.object(forKey: "cmd") as? String
        if value == "acceptLine" {
            deleteChannel(keys: [String(format: "seat%d", chatModel.currentMic)])
            let seat: String! = dic.object(forKey: "seat") as? String
            let result: Int! = Int(seat)
            if result > 0 && result < 9 {
                let micView: ARMicView! = (micArr[result] as! ARMicView)
                if micView.uid?.count != 0 && micView.uid != nil {
                    //麦位被占，检查空麦位
                    for i in 1...9 {
                        let videoView: ARMicView! = (micArr[i] as! ARMicView)
                        if videoView.uid!.count == 0 {
                            let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                            channelAttribute.key = String(format: "seat%d", i)
                            channelAttribute.value = localUserModel!.uid!
                            addOrUpdateChannel(attribute: channelAttribute)
                            chatModel.currentMic = result
                        }
                    }
                } else {
                    let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                    channelAttribute.key = String(format: "seat%d", result)
                    channelAttribute.value = localUserModel!.uid!
                    addOrUpdateChannel(attribute: channelAttribute)
                    chatModel.currentMic = result
                }
            }
        } else if (value == "rejectLine") {
            XHToast.showCenter(withText: "主播拒绝了你的上麦请求")
        }
    }
    
    func channel(_ channel: ARtmChannel, memberJoined member: ARtmMember) {
        //远端用户加入频道回调
        getUserAllAttribute(uid: member.uid)
    }
    
    func channel(_ channel: ARtmChannel, memberLeft member: ARtmMember) {
        //频道成员离开频道回调
        for object in memberList {
            if object is ARChatUserModel {
                let userModel: ARChatUserModel = object as! ARChatUserModel
                if userModel.uid == member.uid {
                    memberList.remove(userModel)
                }
            }
        }
        
        for (_,object) in self.micArr.enumerated() {
            if object is ARMicView {
                let micView: ARMicView = object as! ARMicView
                if micView.uid ==  member.uid {
                    deleteChannel(keys: [String(format: "seat%d", micView.tag)])
                }
            }
        }
        removeMicListFromUid(micUid: member.uid)
    }
    
    func channel(_ channel: ARtmChannel, messageReceived message: ARtmMessage, from member: ARtmMember) {
        //收到频道消息回调
        let dic = getDictionaryFromJSONString(jsonString: message.text)
        let value: String? = dic.object(forKey: "cmd") as? String
        if value == "msg" {
            let content: String? = dic.object(forKey: "content") as? String
            addChannelMessageLog(uid: member.uid, content: content)
        } else if (value == "gift") {
            let result: String = dic.object(forKey: "giftId") as! String
            effectOfGift(giftName: giftList[Int(result)!])
        }
    }
    
    func channel(_ channel: ARtmChannel, attributeUpdate attributes: [ARtmChannelAttribute]) {
        //频道属性更新回调。返回所在频道的所有属性。
        if attributes.count != 0 {
            let dic: NSMutableDictionary = NSMutableDictionary()
            for attribute in attributes {
               dic.setValue(attribute.value, forKey: attribute.key)
            }
            
            let host: String? = dic.object(forKey: "host") as? String
            if isHoster {
                if host != localUserModel.uid {
                    rtcKit.setClientRole(.audience)
                    moreButton.isHidden = true
                    micButton.isHidden = false
                    isHoster = false
                    chatModel.currentMic = 0
                }
            } else {
                if host == localUserModel.uid {
                    if chatModel.currentMic != 9 {
                        deleteChannel(keys: [String(format: "seat%d",chatModel.currentMic)])
                    }
                    rtcKit.setClientRole(.broadcaster)
                    moreButton.isHidden = false
                    micButton.isHidden = true
                    isHoster = true
                    
                    let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                    channelAttribute.key = "seat0"
                    channelAttribute.value = localUserModel!.uid!
                    addOrUpdateChannel(attribute: channelAttribute)
                    chatModel.currentMic = 0
                }
            }
            
            chatModel.isLock = dic.object(forKey: "isLock") as? String
            
            if (dic.object(forKey: "isMicLock") as? String) == "1" {
                chatModel.isMicLock = true
                micLock(lock: true)
                isHoster || applyMic ? (listButton.isHidden = false) : (listButton.isHidden = true)
                
                let waitMic = dic.object(forKey: "waritinglist") as? String
                if waitMic?.count != 0 && waitMic != nil {
                    let arr = NSMutableArray()
                    arr.addObjects(from: getArrayFromJSONString(jsonString: waitMic!) as! [Any])
                    chatModel.waitList = arr
                    
                    let modelArr = NSMutableArray()
                    var waitMic: Bool = false
                    for object in arr {
                        if object is NSDictionary {
                            let dic = object as! NSDictionary
                            let userModel = ARChatUserModel()
                            userModel.uid = dic.object(forKey: "userid") as? String
                            userModel.applyMic = (dic.object(forKey: "seat") as? String)!
                            
                            if userModel.uid == localUserModel.uid {
                                waitMic = true
                            }
                            
                            for model in memberList {
                                if model is ARChatUserModel {
                                    let chatUserModel: ARChatUserModel = model as! ARChatUserModel
                                    if chatUserModel.uid == userModel.uid {
                                        userModel.name = chatUserModel.name
                                        userModel.head = chatUserModel.head
                                        userModel.sex = chatUserModel.sex
                                        break
                                    }
                                }
                            }
                            modelArr.add(userModel)
                        }
                    }
                    
                    if !isHoster && !waitMic {
                        //非自由麦
                        applyMic = false
                        micButton.isHidden = false
                        micButton.isSelected = false
                        listButton.isHidden = true
                    }
                    chatModel.waitModelList = modelArr
                    listButton.setTitle(String(format: "%d",modelArr.count), for: .normal)
                } else {
                    micButton.isSelected = false
                    listButton.setTitle("0", for: .normal)
                    chatModel.waitList.removeAllObjects()
                    chatModel.waitModelList.removeAllObjects()
                    if !isHoster {
                        applyMic = false
                        micButton.isHidden = false
                        micButton.isSelected = false
                        listButton.isHidden = true
                    }
                }
            } else {
                applyMic = false
                listButton.isHidden = true
                chatModel.isMicLock = false
                micLock(lock: false)
                listButton.isHidden = true
                if isHoster {
                    //清空麦序
                    deleteChannel(keys: ["waritinglist"])
                } else {
                    micButton.isHidden = false
                }
                chatModel.waitList.removeAllObjects()
                chatModel.waitModelList.removeAllObjects()
                listButton.setTitle("0", for: .normal)
            }
            
            if topViewController() is ARMicViewController {
                //刷新麦序
                NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationMicRefresh"), object: self, userInfo: ["micLock": NSNumber.init(value: chatModel.isMicLock!)])
            }
            
            chatModel.roomName = dic.object(forKey: "roomName") as? String
            chatNameButton.setTitle(chatModel.roomName, for: .normal)
            
            chatModel.announcement = dic.object(forKey: "notice") as? String
            chatModel.welcome = dic.object(forKey: "welecomeTip") as? String
            
            let musicValue = dic.object(forKey: "music") as? String
            if musicValue?.count != 0 && musicValue != nil {
                chatModel.musicDic = getDictionaryFromJSONString(jsonString: musicValue!) as? NSMutableDictionary
                let musicState: String? = chatModel.musicDic.object(forKey: "state") as? String
                musicButton.layer.removeAnimation(forKey: "CABasicAnimation")
                if musicState == "open" {
                    let animation = CABasicAnimation.init(keyPath: "transform.rotation.z")
                    animation.duration = 2.0
                    animation.fromValue = 0.0
                    animation.toValue = Double.pi * 2
                    animation.repeatCount = MAXFLOAT
                    animation.isRemovedOnCompletion = false
                    musicButton.layer.add(animation, forKey: "CABasicAnimation")
                }
            }
            
            chatModel.currentMic = 9
            for index in 0...8 {
                let key: String! = String(format: "seat%d", index)
                var uid: String? = dic.object(forKey: key as Any) as? String
                (uid == nil) ? (uid = "") : nil
                chatModel.seatDic.setValue(uid, forKey: key)
                
                let micView: ARMicView = micArr[index] as! ARMicView
                micView.uid = uid
                if uid == localUserModel.uid {
                    chatModel.currentMic = index
                }
                
                for object in memberList {
                    if object is ARChatUserModel {
                        let chatUserModel: ARChatUserModel = object as! ARChatUserModel
                        if micView.uid == chatUserModel.uid {
                            micView.chatUserModel = chatUserModel
                            break
                        }
                    }
                }
            }
            
            if chatModel.currentMic == 9 {
                rtcKit.setClientRole(.audience)
                micButton.isSelected = false
                audioButton.isHidden = true
            } else {
                rtcKit.setClientRole(.broadcaster)
                self.audioButton.isHidden = false
                self.micButton.isSelected = true
            }
            
            let value0: String? = dic.object(forKey: "MuteMicList") as? String
            if value0 != nil {
                chatModel.muteMicList = getArrayFromJSONString(jsonString: value0!)
                if chatModel.muteMicList.contains(localUserModel.uid!) && chatModel.currentMic != 9 {
                    rtcKit.muteLocalAudioStream(true)
                    audioButton.isSelected = true
                    audioButton.isUserInteractionEnabled = false
                } else {
                    audioButton.isUserInteractionEnabled = true
                }
            }
            
            let value1: String? = dic.object(forKey: "MuteInputList") as? String
            if value1 != nil {
                chatModel.muteInputList = getArrayFromJSONString(jsonString: value1!)
                if chatModel.muteInputList.contains(localUserModel.uid!) {
                    chatButton.isUserInteractionEnabled = false
                } else {
                    chatButton.isUserInteractionEnabled = true
                }
            }
       }
    }
    
    func channel(_ channel: ARtmChannel, memberCount count: Int32) {
        //频道成员人数更新回调。返回最新频道成员人数。
        onlineButton.setTitle(String(format: "在线:%d",count), for: .normal)
    }
}

extension ARChatViewController: UITextFieldDelegate, ARMicDelegate{
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField.text?.count != 0 {
            textField.resignFirstResponder()
            if !PasswordInput {
                addChannelMessageLog(uid: localUserModel.uid, content: chatTextField.text)
                let dic: NSDictionary! = ["cmd": "msg","content": chatTextField.text as Any]
                sendChannelMessage(value: getJSONStringFromDictionary(dictionary: dic))
            }
            textField.text = ""
        }
        return true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        PasswordInput = false
        chatTextField.placeholder = "聊点什么吧"
        chatTextField.keyboardType = .default
    }
    
    
    func micLock(lock: Bool) {
        //上麦模式
        micArr.enumerateObjects { (object, index, stop) in
            if object is ARMicView {
                let micView: ARMicView = object as! ARMicView
                if micView.tag != 0 {
                    micView.lockImageView.isHidden = !lock
                }
                
                if lock {
                    micView.micStatus = .micLock
                    if micView.uid?.count != 0 && micView.uid != nil {
                        micView.lockImageView.isHidden = true
                    }
                } else {
                    if micView.uid?.count != 0 && micView.uid != nil {
                        micView.micStatus = .micExist
                    } else {
                        micView.micStatus = .micDefault
                    }
                }
            }
        }
    }
    
    func didClickMicView(index: NSInteger, status: ARMicStatus, userModel: ARChatUserModel?) {
        if status == .micDefault {
            //自由模式
            if !isHoster {
                if index != 0 {
                    if (chatModel.currentMic > 0) && (chatModel.currentMic < 9) {
                        deleteChannel(keys: [String(format: "seat%d", chatModel.currentMic)])
                        let micView: ARMicView = micArr[chatModel.currentMic] as! ARMicView
                        micView.uid = ""
                    }
                    
                    chatModel.currentMic = index
                    let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                    channelAttribute.key = String(format: "seat%d", index)
                    channelAttribute.value = localUserModel!.uid!
                    addOrUpdateChannel(attribute: channelAttribute)
                    
                    let micView: ARMicView = micArr[index] as! ARMicView
                    micView.uid = localUserModel.uid!
                } else {
                    XHToast.showCenter(withText: "游客不能上主持人麦位")
                }
            } else {
               XHToast.showCenter(withText: "主持人不能切换麦位")
            }
        } else if (status == .micExist || status == .micExistLock) {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: ARInfoDataViewController = storyboard.instantiateViewController(withIdentifier: "ARChat_InfoData") as! ARInfoDataViewController
            isHoster ? (vc.state = ARInfoDataState.none) : ((vc.state = ARInfoDataState.audience))
            vc.modalPresentationStyle = .overCurrentContext
            vc.userModel = userModel
            vc.index = index
            self.present(vc, animated: true, completion: nil)
        } else if (status == .micLock) {
            //非自由上麦模式
            if !isHoster {
                UIAlertController.showAlert(in: self, withTitle: String(format: "是否申请上%d号麦",index), message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { (controller, action, buttonIndex) in
                    if buttonIndex == 2 {
                        self.applyLockMic(value: String(index))
                    }
                }
            } else {
                XHToast.showCenter(withText: "主持人不能切换麦位")
            }
        }
    }
}

extension ARChatViewController:UICollectionViewDataSource,UICollectionViewDelegate, UICollectionViewDelegateFlowLayout{
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return soundList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        
        return CGSize.init(width: 84, height: 40)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let collectionViewCell: ARChatSoundCell! = (collectionView.dequeueReusableCell(withReuseIdentifier: "ARChat_SoundCellID", for: indexPath) as! ARChatSoundCell)
        collectionViewCell.updateSoundCell(soundName: soundList[indexPath.row])
        collectionViewCell.backgroundColor = UIColor.init(hexString:colorList[indexPath.row])
        return collectionViewCell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let list = ["chipmunk","qihong","guzhang","awkward","wuya","wodema"]
        let filePath: String = Bundle.main.path(forResource: list[indexPath.row], ofType:"wav")!
        rtcKit.stopAllEffects()
        rtcKit.playEffect(666, filePath: filePath, loopCount: 0, pitch: 1.0, pan: 0, gain: 100, publish: true)
    }
}

class ARChatSoundCell: UICollectionViewCell {
    
    @IBOutlet weak var soundLabel: UILabel!
    
    func updateSoundCell(soundName: String!) {
        soundLabel.attributedText = getAttributedString(text: soundName, image: UIImage(named: "icon_volatility")!, index: soundName.count)
    }
    
}
