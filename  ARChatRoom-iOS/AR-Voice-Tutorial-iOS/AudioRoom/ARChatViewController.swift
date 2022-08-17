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

class ARChatViewController: ARBaseViewController {
    
    @IBOutlet weak var stackView0: UIStackView!
    @IBOutlet weak var stackView1: UIStackView!
    @IBOutlet weak var announcementButton: UIButton!
    @IBOutlet weak var onlineButton: UIButton!
    @IBOutlet weak var chatNameButton: UIButton!
    @IBOutlet weak var channelIdLabel: UILabel!
    @IBOutlet weak var signalButton: UIButton!
    @IBOutlet weak var chatButton: UIButton!
    @IBOutlet weak var musicButton: UIButton!
    @IBOutlet weak var musicLabel: UILabel!
    @IBOutlet weak var musicView: UIView!
    /** 音频开关 */
    @IBOutlet weak var audioButton: UIButton!
    /** 上下麦 -- 游客 */
    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var listButton: UIButton!
    @IBOutlet weak var moreButton: UIButton!
    /** 音效高度 */
    @IBOutlet weak var soundConstraint: NSLayoutConstraint!
    /** 录音指示器 */
    @IBOutlet weak var recordIndicator: UIView!
    
    fileprivate var memberList = NSMutableArray()
    var channelId: String!
    var isHoster: Bool!
    var cancelFromBan: Bool = false
    /** 游客正在申请上麦(非自由) */
    var applyMic: Bool = false
    /** 默认聊天（密码） */
    var passwordInput: Bool = false
    
    let soundList = ["哈哈哈","起哄","鼓掌","尴尬","乌鸦","哎呀我滴妈"]
    let colorList = ["#CE5850","#F29025","#8252F6","#6AB71C","#03A3C3","#0A6BBE"]
    let giftNameList = ["棒棒糖","星星","魔术帽","独角兽","王冠","宝箱","跑车","火箭"]
    let giftList = ["icon_lollipop_effect","icon_stars_effect","icon_hats_effect","icon_unicorn_effect","icon_crown_effect","icon_chest_effect","icon_car_effect","icon_rockets_effect"]
    
    weak var logVC: LogViewController?
    var micArr = NSMutableArray()
    fileprivate var rtmChannel: ARtmChannel!
    /** 悬浮窗 */
    lazy var floatingView: ARFloatingView = {
        let suspensionView = ARFloatingView.init(frame: CGRect.zero)
        suspensionView.clickDragViewBlock = { [weak self] (dragView: WMDragView?) ->() in
            self?.view.isHidden = false
            StatusBarTextColor = true
            self?.setNeedsStatusBarAppearanceUpdate()
            self!.floatingView.removeFromSuperview()
        }
        suspensionView.roomLabel.text = chatModel.roomName
        suspensionView.closeButton.addTarget(self, action: #selector(self.endChatRoom), for: .touchUpInside)
        return suspensionView
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        initializeUI()
        initializeEngine()
        addObserveNotification()
    }
    
    func initializeEngine() {
        //实例化rtc实例对象
        ARVoiceRtm.updateRtmkit(delegate: self)
        rtcKit = ARtcEngineKit.sharedEngine(withAppId: AppID, delegate: self)
        rtcKit.setChannelProfile(.liveBroadcasting)
        if  isHoster {
            rtcKit.setClientRole(.broadcaster)
        }
        rtcKit.setAudioProfile(.musicHighQuality, scenario: .gameStreaming)
        //加入房间
        rtcKit.joinChannel(byToken: "", channelId: channelId!, uid: localUserModel.uid) {(channel, uid, elapsed) -> Void in
            print("joinChannel")
        }
        rtcKit.enable(inEarMonitoring: true)
        //启用说话者音量提示
        rtcKit.enableAudioVolumeIndication(500, smooth: 3, report_vad: true)
        
        //创建rtm频道
        ARVoiceRtm.rtmKit?.aRtmDelegate = self
        rtmChannel = ARVoiceRtm.rtmKit?.createChannel(withId: channelId, delegate: self)
        if (rtmChannel != nil) {
            rtmChannel.join { [weak self] (errorCode) in
                print("join channel")
                self?.getChannelMembers()
            }
        }
    }
    
    func getChannelMembers() {
        //获取频道成员
        rtmChannel.getMembersWithCompletion { [weak self] (members, errorCode) in
            if members?.count != 0 {
                for object in members! {
                    let member: ARtmMember = object
                    self?.getUserAllAttribute(uid: member.uid, exist: true)
                }
            }
        }
    }
    
    func getUserAllAttribute(uid: String!, exist: Bool) {
        //获取指定用户的全部属性
        ARVoiceRtm.rtmKit?.getUserAllAttributes(uid, completion:{[weak self] (attributes, uid, errorCode) in
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
            self?.memberList.add(chatUserModel)
            if (self?.isHoster ?? false) && uid == localUserModel.uid {
                let micView = self?.micArr.firstObject as! ARMicView
                micView.chatUserModel = chatUserModel
                micView.uid = localUserModel.uid
            }
            
            if !exist {
                let userModel: ARChatUserModel? = self?.getUserModelFromUid(uid: uid)
                self?.logVC?.log(logModel: ARLogModel.createMessageMode(type: .join, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: true, fromUid: uid, fromName: userModel?.name, toUid: "", toName: "", giftName: ""))
            }
            
            for object in self!.micArr {
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
    
    func initializeUI() {
        UIApplication.shared.isIdleTimerDisabled = true
        announcementButton.layer.borderColor = UIColor.red.cgColor
        onlineButton.layer.borderColor = UIColor.red.cgColor
        channelId = chatModel.channelId
        channelIdLabel.text = String(format: "ID:%@",channelId)
        chatNameButton.setTitle(chatModel.roomName, for: .normal)
        chatButton.contentHorizontalAlignment = .left
        chatNameButton.contentHorizontalAlignment = .left
        musicView.layer.borderColor = UIColor.lightGray.cgColor
        for i in 0 ..< 9 {
            let micView: ARMicView = ARMicView.videoView()
            micView.tag = i
            micView.delegate = self
            if i == 0 {
                micView.titleLabel.text = "主持麦"
                self.view.addSubview(micView)
                micView.snp.makeConstraints { (make) in
                    make.top.equalTo(onlineButton.snp.bottom).offset(5)
                    make.width.equalTo(64)
                    make.height.equalTo(82)
                    make.centerX.equalTo(self.view.snp.centerX)
                }
            } else {
                micView.titleLabel.text = String(i) + "号麦位"
                micView.uid = chatModel.seatDic.object(forKey: String(format: "seat%d", i)) as? String
                (i < 5) ? (stackView0.addArrangedSubview(micView)) : (stackView1.addArrangedSubview(micView))
            }
            micArr.add(micView)
        }
        
        if isHoster {
            //主持人上麦
            addOrUpdateChannel(key: "seat0", value: localUserModel!.uid!)
            chatModel.currentMic = 0
        }
        
        let messageModel: ARLogModel = ARLogModel()
        messageModel.contentType = .warn
        messageModel.content = "系统：官方倡导绿色交友，并24小时对互动房间进行巡查，如果发现低俗、骂人、人身攻击等违规行为。官方将进行封房封号处理"
        self.logVC?.log(logModel: messageModel)
        
        chatTextField.placeholder = "聊点什么吧"
        chatTextField.delegate = self
        chatTextField.addTarget(self, action: #selector(chatTextFieldLimit), for: .editingChanged)
        confirmButton.addTarget(self, action: #selector(didSendChatTextField), for: .touchUpInside)
    }
    
    func addObserveNotification() {
        //通知
        NotificationCenter.default.addObserver(self, selector: #selector(soundEffect), name: UIResponder.chatNotificationSound, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(sendoutGift), name: UIResponder.chatNotificationGift, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(setupPassword), name: UIResponder.chatNotificationPassWord, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(sendoutGiftFromUid), name: UIResponder.chatNotificationGiftFromUid, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(clickMessageFromUid), name: UIResponder.chatNotificationMessageUid, object: nil)
    }
    
    @objc func didSendChatTextField() {
        if !isBlank(text: chatTextField.text) {
            if passwordInput {
                //设置密码
                if chatTextField.text?.count != 4 {
                    XHToast.showCenter(withText: "请输入4位数字密码")
                    return
                }
                addOrUpdateChannel(key: "isLock", value: chatTextField.text!)
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
        let userModel: ARChatUserModel? = getUserModelFromUid(uid: uid)
        self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .info, text: content, micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: uid, fromName: userModel?.name, toUid: "", toName: "", giftName: ""))
    }
    
    func sendChannelMessage(value: String!) {
        let rtmMessage: ARtmMessage = ARtmMessage.init(text: value)
        let options: ARtmSendMessageOptions = ARtmSendMessageOptions()
        //发送频道消息
        rtmChannel.send(rtmMessage, sendMessageOptions: options) { (errorCode) in
            print("Send Channel Message")
        }
    }

    @objc func chatTextFieldLimit() {
        if isBlank(text: chatTextField.text) {
            confirmButton.alpha = 0.3
        } else {
            confirmButton.alpha = 1.0
            if passwordInput {
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
            StatusBarTextColor = false
            setNeedsStatusBarAppearanceUpdate()
            self.view.isHidden = true
            let delegate  = UIApplication.shared.delegate as! AppDelegate
            delegate.window?.addSubview(floatingView)
        } else if (sender.tag == 51) {
            //关闭
            UIAlertController.showAlert(in: self, withTitle: "确定退出房间？", message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { [weak self] (aletrVc, action, index) in
                (index == 2) ? (self?.endChatRoom()) : nil
            }
        } else if (sender.tag == 52) {
            //音乐 -- 主持人
            if isHoster {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let nav: UINavigationController = storyboard.instantiateViewController(withIdentifier: "ARChat_MusicID") as! UINavigationController
                //let vc: ARMusicViewController = nav.viewControllers[0] as! ARMusicViewController
                nav.modalPresentationStyle = .overFullScreen
                nav.modalPresentationCapturesStatusBarAppearance = true
                self.present(nav, animated: true, completion: nil)
            } else {
                promptBoxView(result: false, text: "只有主持人才可以")
            }
        } else if (sender.tag == 53) {
            //音频
            if chatModel.muteMicList != nil {
                if chatModel.muteMicList.contains(localUserModel.uid!) {
                    sender.isSelected = true
                    XHToast.showCenter(withText: "您已被主持人禁麦")
                    return
                }
            }
            rtcKit.muteLocalAudioStream(sender.isSelected)
            if sender.isSelected && chatModel.currentMic != 9 {
                let micView: ARMicView = micArr[chatModel.currentMic] as! ARMicView
                if micView.uid == localUserModel.uid {
                    micView.endAudioAnimation()
                }
            }
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
                if chatModel.isMicLock {
                    //申请上麦
                    applyLockMic(value: "1")
                } else {
                    //自动上麦
                    for (index,object) in micArr.enumerated() {
                        if object is ARMicView {
                            let micView: ARMicView = object as! ARMicView
                            if isBlank(text: micView.uid) && index != 0 {
                                chatModel.currentMic = index
                                addOrUpdateChannel(key: String(format: "seat%d", index), value: localUserModel!.uid!)
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
        }  else if (sender.tag == 58) {
            //聊天
            if chatButton.titleLabel?.text != "禁言中" {
                chatTextField.becomeFirstResponder()
            }
        }
    }
    
    //通过uid获取个人属性
    func getUserModelFromUid(uid: String?) -> ARChatUserModel? {
        for object in self.memberList {
          if object is ARChatUserModel {
              let userModel: ARChatUserModel = object as! ARChatUserModel
              if userModel.uid == uid {
                  return userModel
              }
          }
        }
        return nil
    }
    
    //非自由模式申请上麦
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
        addOrUpdateChannel(key: "waitinglist", value: getJSONStringFromArray(array: arr))
        micButton.isSelected = false
        micButton.isHidden = true
        listButton.isHidden = false
    }
    
    @objc func sendoutGift(nofi: Notification) {
        let result: String = nofi.userInfo!["gift"] as! String
        var uid: String? = nofi.userInfo!["uid"] as? String
        var name: String? =  nofi.userInfo!["name"] as? String
        (uid == nil) ? uid = "" : nil
        (name == nil) ? name = "" : nil
        let dic: NSDictionary! = ["cmd": "gift", "giftId": result, "userId": uid as Any]
        sendChannelMessage(value: getJSONStringFromDictionary(dictionary: dic))
        //礼物消息
        self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .gift, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: localUserModel.uid, fromName: localUserModel.name, toUid: uid, toName: name, giftName: giftNameList[Int(result)!]))
        
        if isBlank(text: uid) {
            effectOfGift(giftName: giftList[Int(result)!],text: String(format: "%@ 给麦上所有人送上了 %@",localUserModel.name!, giftNameList[Int(result)!]))
        } else {
            effectOfGift(giftName: giftList[Int(result)!],text: String(format: "%@ 给 %@ 送上了 %@",localUserModel.name!,name!, giftNameList[Int(result)!]))
        }
    }
    
    @objc func sendoutGiftFromUid(nofi: Notification)  {
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.5) {
            let giftUid: String? = nofi.userInfo!["giftUid"] as? String
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: ARGiftViewController = storyboard.instantiateViewController(withIdentifier: "ARChat_Gift") as! ARGiftViewController
            
            let userModel: ARChatUserModel? = self.getUserModelFromUid(uid: giftUid)
            if userModel != nil {
                vc.channelUserModel = userModel
            }

            vc.modalPresentationStyle = .overCurrentContext
            self.present(vc, animated: true, completion: nil)
        }
    }
    
    @objc func soundEffect(nofi: Notification) {
        let result: Bool = nofi.userInfo!["open"] as! Bool
        chatModel.sound = result
        result ? (soundConstraint.constant = 40.0) : (soundConstraint.constant = 0)
    }
    
    @objc func setupPassword(nofi: Notification) {
        let result: Bool = nofi.userInfo!["state"] as! Bool
        if result {
            passwordInput = true
            chatTextField.text = ""
            chatTextField.keyboardType = .numberPad
            chatTextField.isSecureTextEntry = true
            chatTextField.placeholder = "请输入4位数字密码"
            chatTextField.becomeFirstResponder()
        } else {
            //取消密码
            deleteChannel(keys: ["isLock"])
            XHToast.showCenter(withText: "取消密码成功")
        }
    }
    
    @objc func clickMessageFromUid(nofi: Notification) {
        let uid: String? = nofi.userInfo!["uid"] as? String
        
        let channelUserModel: ARChatUserModel? = getUserModelFromUid(uid: uid)
        if channelUserModel != nil {
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: ARInfoDataViewController = storyboard.instantiateViewController(withIdentifier: "ARChat_InfoData") as! ARInfoDataViewController
            isHoster ? (vc.state = ARInfoDataState.none) : ((vc.state = ARInfoDataState.audience))
            vc.modalPresentationStyle = .overCurrentContext
            isHoster ? (vc.state = .noMic) : (vc.state = .audience)
            vc.userModel = channelUserModel
            self.present(vc, animated: true, completion: nil)
        } else {
            promptBoxView(result: false, text: "对方已退出频道")
        }
    }
    
    @objc func endChatRoom() {
        //离开房间
        UIApplication.shared.isIdleTimerDisabled = false
        deleteChannel(keys: [String(format: "seat%d", chatModel.currentMic)])
        rtmChannel.leave(completion: nil)
        ARVoiceRtm.rtmKit?.destroyChannel(withId: channelId)
        //释放rtc资源
        rtcKit.stopAudioMixing()
        rtcKit.leaveChannel(nil)
        ARtcEngineKit.destroy()

        StatusBarTextColor = false
        self.setNeedsStatusBarAppearanceUpdate()
        floatingView.removeFromSuperview()
        self.willMove(toParent: nil)
        self.view.removeFromSuperview()
        self.removeFromParent()
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

extension ARChatViewController: ARtcEngineDelegate, ARtmChannelDelegate {
    
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
    
    func rtcEngineLocalAudioMixingDidFinish(_ engine: ARtcEngineKit) {
        //本地音乐文件播放已结束回调
        //deleteChannel(keys: ["music"])
    }
    
    func rtcEngine(_ engine: ARtcEngineKit, reportAudioVolumeIndicationOfSpeakers speakers: [ARtcAudioVolumeInfo], totalVolume: Int) {
        //提示频道内谁正在说话、说话者音量及本地用户是否在说话的回调
        for object in speakers {
            let volumeInfo: ARtcAudioVolumeInfo! = object
            if volumeInfo.volume > 0 {
                if (volumeInfo.uid == "0") {
                    if chatModel.currentMic != 9 {
                        let micView: ARMicView! = (micArr[chatModel.currentMic] as! ARMicView)
                        if !isBlank(text: micView.uid) {
                            micView.startAudioAnimation()
                        }
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
                if !isBlank(text: micView.uid) {
                    //麦位被占，检查空麦位
                    for i in 1...9 {
                        let videoView: ARMicView! = (micArr[i] as! ARMicView)
                        if isBlank(text: videoView.uid) {
                            addOrUpdateChannel(key: String(format: "seat%d", i), value: localUserModel!.uid!)
                            chatModel.currentMic = result
                        }
                    }
                } else {
                    addOrUpdateChannel(key: String(format: "seat%d", result), value: localUserModel!.uid!)
                    chatModel.currentMic = result
                }
            }
        } else if (value == "rejectLine") {
            XHToast.showCenter(withText: "主播拒绝了你的上麦请求")
        }
    }
    
    func channel(_ channel: ARtmChannel, memberJoined member: ARtmMember) {
        //远端用户加入频道回调
        getUserAllAttribute(uid: member.uid, exist: false)
    }
    
    func channel(_ channel: ARtmChannel, memberLeft member: ARtmMember) {
        //频道成员离开频道回调
        let userModel: ARChatUserModel? = getUserModelFromUid(uid: member.uid)
        self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .join, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: member.uid, fromName: userModel?.name, toUid: "", toName: "", giftName: ""))
        
        memberList.remove(getUserModelFromUid(uid: member.uid) as Any)
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
            //礼物消息
            let uid: String? = dic.object(forKey: "userId") as? String
            var toUid: String? = ""
            var toName: String = ""
            if !isBlank(text: uid) {
                let chatUserModel: ARChatUserModel? = getUserModelFromUid(uid: uid)
                toUid = uid
                if chatUserModel != nil {
                    toName = chatUserModel!.name!
                }
            }
            let fromUserModel: ARChatUserModel! = getUserModelFromUid(uid: member.uid)
            self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .gift, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: member.uid, fromName: fromUserModel.name, toUid: toUid, toName: toName, giftName: giftNameList[Int(result)!]))
            
            if isBlank(text: uid) {
                effectOfGift(giftName: giftList[Int(result)!],text: String(format: " %@ 给麦上所有人送上了 %@ ",fromUserModel.name!, giftNameList[Int(result)!]))
            } else {
                effectOfGift(giftName: giftList[Int(result)!],text: String(format: " %@ 给 %@ 送上了 %@ ",fromUserModel.name!,toName, giftNameList[Int(result)!]))
            }
        }
    }
    
    func channel(_ channel: ARtmChannel, attributeUpdate attributes: [ARtmChannelAttribute]) {
        //频道属性更新回调。返回所在频道的所有属性。
        if attributes.count != 0 {
            let dic: NSMutableDictionary = NSMutableDictionary()
            for attribute in attributes {
               dic.setValue(attribute.value, forKey: attribute.key)
            }
            
            //欢迎语
            let welcome: String? = dic.object(forKey: "welecomeTip") as? String
            if welcome != nil && welcome != chatModel.welcome {
                self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .welcom, text: "", micLocation: "", micState: false, record: false, password: false, welcom: welcome, join: false, fromUid: "", fromName: "", toUid: "", toName: "", giftName: ""))
            }
            chatModel.welcome = dic.object(forKey: "welecomeTip") as? String
            
            //公告
            let notice: String? = dic.object(forKey: "notice") as? String
            if notice != chatModel.announcement {
                self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .warn, text: "主持人 修改了房间公告", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: "", fromName: "", toUid: "", toName: "", giftName: ""))
            }
            chatModel.announcement = notice
            
            let host: String? = dic.object(forKey: "host") as? String
            if isHoster {
                if host != localUserModel.uid {
                    rtcKit.setClientRole(.audience)
                    moreButton.isHidden = true
                    micButton.isHidden = false
                    isHoster = false
                    chatModel.currentMic = 0
                    soundConstraint.constant = 0.0
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
                    
                    addOrUpdateChannel(key: "seat0", value: localUserModel!.uid!)
                    chatModel.currentMic = 0
                }
            }
            
            //非自由上麦
            if (dic.object(forKey: "isMicLock") as? String) == "1" {
                chatModel.isMicLock = true
                micLock(lock: true)
                isHoster || applyMic ? (listButton.isHidden = false) : (listButton.isHidden = true)
                
                let waitMic = dic.object(forKey: "waitinglist") as? String
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
                            
                            let chatUserModel: ARChatUserModel? = getUserModelFromUid(uid: userModel.uid)
                            if chatUserModel != nil {
                                userModel.name = chatUserModel!.name
                                userModel.head = chatUserModel!.head
                                userModel.sex = chatUserModel!.sex
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
                    deleteChannel(keys: ["waitinglist"])
                } else {
                    micButton.isHidden = false
                }
                chatModel.waitList.removeAllObjects()
                chatModel.waitModelList.removeAllObjects()
                listButton.setTitle("0", for: .normal)
            }
            
            if topViewController() is ARMicViewController {
                NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationMicRefresh"), object: self, userInfo: ["micLock": NSNumber.init(value: chatModel.isMicLock)])
            }
            
            let password: String? = dic.object(forKey: "isLock") as? String
            if chatModel.isLock == nil && password != nil {
                promptBoxView(result: true, text: "更新成功")
                self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .password, text: "", micLocation: "", micState: false, record: false, password: true, welcom: "", join: false, fromUid: "", fromName: "", toUid: "", toName: "", giftName: ""))
            } else {
                if chatModel.isLock != nil && password == nil {
                self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .password, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: "", fromName: "", toUid: "", toName: "", giftName: ""))
                }
            }
            
            //录音
            let record: String? = dic.object(forKey: "record") as? String
            if record == "1" {
                if chatModel.record == false {
                    let animation = CABasicAnimation.init(keyPath: "backgroundColor")
                    animation.duration = 1.0
                    animation.fromValue = UIColor.white.cgColor
                    animation.toValue = UIColor.red.cgColor
                    animation.repeatCount = MAXFLOAT
                    animation.isRemovedOnCompletion = false
                    recordIndicator.layer.add(animation, forKey: "CABasicAnimation")
                    //开启录音提示
                    let channelUserModel: ARChatUserModel? = getUserModelFromUid(uid: host)
                    self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .record, text: "", micLocation: "", micState: false, record: true, password: false, welcom: "", join: false, fromUid: channelUserModel?.uid, fromName: channelUserModel?.name, toUid: "", toName: "", giftName: ""))
                }
                self.recordIndicator.isHidden = false
                chatModel.record = true
            } else {
                if chatModel.record {
                    recordIndicator.layer.removeAnimation(forKey: "CABasicAnimation")
                    //关闭录音提示
                    let channelUserModel: ARChatUserModel? = getUserModelFromUid(uid: host)
                    self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .record, text: "", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: channelUserModel?.uid, fromName: channelUserModel?.name, toUid: "", toName: "", giftName: ""))
                }
                self.recordIndicator.isHidden = true
                chatModel.record = false
            }
            
            let roomName: String? = dic.object(forKey: "roomName") as? String
            if chatModel.roomName != roomName && !isBlank(text: chatModel.roomName){
                self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .warn, text: "主持人 修改了房间名称", micLocation: "", micState: false, record: false, password: false, welcom: "", join: false, fromUid: "", fromName: "", toUid: "", toName: "", giftName: ""))
                floatingView.roomLabel.text = roomName
            }
            chatModel.roomName = roomName ?? ""

            chatModel.isLock = password
            chatNameButton.setTitle(chatModel.roomName, for: .normal)
            if chatModel.isLock != nil {
                chatNameButton.setImage(UIImage(named: "icon_lock_room"), for: .normal)
            } else {
                chatNameButton.setImage(nil, for: .normal)
            }
            
            //音乐
            let musicValue = dic.object(forKey: "music") as? String
            
            if !isBlank(text: musicValue) {
                chatModel.musicDic = getDictionaryFromJSONString(jsonString: musicValue!) as? NSMutableDictionary
                let musicState: String? = chatModel.musicDic.object(forKey: "state") as? String
                musicLabel.text = chatModel.musicDic.object(forKey: "name") as? String
                musicButton.layer.removeAnimation(forKey: "CABasicAnimation")
                if musicState == "open" {
                    let animation = CABasicAnimation.init(keyPath: "transform.rotation.z")
                    animation.duration = 2.0
                    animation.fromValue = 0.0
                    animation.toValue = Double.pi * 2
                    animation.repeatCount = MAXFLOAT
                    animation.isRemovedOnCompletion = false
                    musicButton.layer.add(animation, forKey: "CABasicAnimation")
                } else {
                    musicLabel.text = ""
                }
            } else {
                if !isBlank(text: musicLabel.text) {
                    musicButton.layer.removeAnimation(forKey: "CABasicAnimation")
                    musicLabel.text = ""
                }
            }
            
            //禁麦
            let value0: String? = dic.object(forKey: "MuteMicList") as? String
            if value0 != nil {
                chatModel.muteMicList = getArrayFromJSONString(jsonString: value0!)
                if chatModel.muteMicList.contains(localUserModel.uid!) && chatModel.currentMic != 9 {
                    rtcKit.muteLocalAudioStream(true)
                    cancelFromBan = true
                    audioButton.isSelected = true
                } else if cancelFromBan {
                    rtcKit.muteLocalAudioStream(false)
                    audioButton.isSelected = false
                    cancelFromBan = false
                }
            }
            
            //禁言
            chatButton.contentHorizontalAlignment = .left
            chatButton.setTitle("聊点是什么吧", for: .normal)
            let value1: String? = dic.object(forKey: "MuteInputList") as? String
            if value1 != nil {
                chatModel.muteInputList = getArrayFromJSONString(jsonString: value1!)
                if chatModel.muteInputList.contains(localUserModel.uid!) {
                    chatButton.setTitle("禁言中", for: .normal)
                    chatButton.contentHorizontalAlignment = .center
                }
            }
            
            chatModel.currentMic = 9
            for index in 0...8 {
                let key: String! = String(format: "seat%d", index)
                var oldUid: String? = chatModel.seatDic.object(forKey: key as Any) as? String
                (oldUid == nil) ? (oldUid = "") : nil
                var uid: String? = dic.object(forKey: key as Any) as? String
                (uid == nil) ? (uid = "") : nil
                
                if isBlank(text: oldUid) && !isBlank(text: uid){
                    //上麦
                    let channelUserModel: ARChatUserModel? = getUserModelFromUid(uid: uid)
                    if channelUserModel != nil {
                        self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .mic, text: "", micLocation: String(index), micState: true, record: false, password: false, welcom: "", join: false, fromUid: uid, fromName: channelUserModel?.name, toUid: uid, toName: "", giftName: ""))
                    }
                } else {
                    if !isBlank(text: oldUid) && isBlank(text: uid) {
                        //下麦
                        let channelUserModel: ARChatUserModel? = getUserModelFromUid(uid: oldUid)
                        if channelUserModel != nil {
                        self.logVC?.log(logModel: ARLogModel.createMessageMode(type: .mic, text: "", micLocation: String(index), micState: false, record: false, password: false, welcom: "", join: false, fromUid: uid, fromName: channelUserModel?.name, toUid: uid, toName: "", giftName: ""))
                        }
                    }
                }
                
                chatModel.seatDic.setValue(uid, forKey: key)
                let micView: ARMicView = micArr[index] as! ARMicView
                micView.uid = uid
                if uid == localUserModel.uid {
                    chatModel.currentMic = index
                }
                
                //禁麦状态
                micView.banMic(ban: false)
                if uid != nil {
                    if chatModel.muteMicList != nil {
                        if chatModel.muteMicList.contains(uid as Any) {
                            micView.banMic(ban: true)
                        }
                    }
                }
                
                
                let chatUserModel: ARChatUserModel? = getUserModelFromUid(uid: uid)
                if chatUserModel != nil {
                    micView.chatUserModel = chatUserModel
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
       }
    }
    
    func channel(_ channel: ARtmChannel, memberCount count: Int32) {
        //频道成员人数更新回调。返回最新频道成员人数。
        onlineButton.setTitle(String(format: "在线:%d",count), for: .normal)
    }
}

extension ARChatViewController: UITextFieldDelegate, ARMicDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if !isBlank(text: textField.text) {
            textField.resignFirstResponder()
            if !passwordInput {
                addChannelMessageLog(uid: localUserModel.uid, content: chatTextField.text)
                let dic: NSDictionary! = ["cmd": "msg","content": chatTextField.text as Any]
                sendChannelMessage(value: getJSONStringFromDictionary(dictionary: dic))
            }
            textField.text = ""
        }
        return true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        passwordInput = false
        chatTextField.placeholder = "聊点什么吧"
        chatTextField.keyboardType = .default
        chatTextField.isSecureTextEntry = false
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
                    if !isBlank(text: micView.uid) {
                        micView.lockImageView.isHidden = true
                    }
                } else {
                    if !isBlank(text: micView.uid) {
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
                    addOrUpdateChannel(key: String(format: "seat%d", index), value: localUserModel!.uid!)
                    
                    let micView: ARMicView = micArr[index] as! ARMicView
                    micView.uid = localUserModel.uid!
                } else {
                    promptBoxView(result: false, text: "游客不能上主持人麦位")
                }
            } else {
               promptBoxView(result: false, text: "主持人不能更改麦位")
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
                promptBoxView(result: false, text: "主持人不能更改麦位")
            }
        }
    }
}

extension ARChatViewController:UICollectionViewDataSource,UICollectionViewDelegate, UICollectionViewDelegateFlowLayout{
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return soundList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        
        let str = soundList[indexPath.row]
        let dic = [NSAttributedString.Key.font: UIFont(name: "PingFang SC", size: 14)]
        let size = CGSize(width: CGFloat(MAXFLOAT), height: 40)
        let width = str.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: dic as [NSAttributedString.Key : Any], context: nil).size.width
        return CGSize(width: width + 50, height: 40)
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
        soundLabel.text = soundName
    }
}
