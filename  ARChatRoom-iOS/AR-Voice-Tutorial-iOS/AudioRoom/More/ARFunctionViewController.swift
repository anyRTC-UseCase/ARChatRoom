//
//  ARFunctionViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/10.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit
import ARtcKit

class ARFunctionViewController: ARBaseViewController, UIGestureRecognizerDelegate{

    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var soundButton: UIButton!
    @IBOutlet weak var passwordButton: UIButton!
    @IBOutlet weak var recordButton: UIButton!
    
    let tap = UITapGestureRecognizer()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        micButton.isSelected = chatModel.isMicLock
        soundButton.isSelected = chatModel.sound
        recordButton.isSelected = chatModel.record
        
        if chatModel.isLock?.count != 0 && chatModel.isLock != nil {
            passwordButton.isSelected = true
        }
    }
    
    @IBAction func didClickCloseButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    
    @IBAction func clickFunctionButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        if sender.tag == 50 {
            //房间信息
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: UIViewController! = storyboard.instantiateViewController(withIdentifier: "ARChat_InfoID")
            vc.modalPresentationStyle = .fullScreen
            self.present(vc, animated: true, completion: nil)
        } else if (sender.tag == 51) {
            //自由上麦
            var value: String?
            sender.isSelected ? (value = "1") : (value = "0")
            addOrUpdateChannel(key: "isMicLock", value: value)
            self.dismiss(animated: false, completion: nil)
        } else if (sender.tag == 52) {
            //设置密码
             NotificationCenter.default.post(name: UIResponder.chatNotificationPassWord, object: self, userInfo: ["state": NSNumber.init(value: sender.isSelected)])
            self.dismiss(animated: false, completion: nil)
        } else if (sender.tag == 53) {
            //音量
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: UIViewController! = storyboard.instantiateViewController(withIdentifier: "ARChat_VolumeID")
            vc.modalPresentationStyle = .overCurrentContext
            self.present(vc, animated: true, completion: nil)
        } else if (sender.tag == 54) {
            sender.isSelected.toggle()
            //录音
            if !sender.isSelected {
                UIAlertController.showActionSheet(in: self, withTitle: nil, message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["开始录音", "录音管理"], popoverPresentationControllerBlock: { (vc)
                    in
                }) { (alertVc, action, index) in
                    if index == 2 {
                        //开始录制
                        UIAlertController.showActionSheet(in: self, withTitle: nil, message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["高保真","有损压缩"], popoverPresentationControllerBlock: { (popVc) in
                             
                        }) { (alertVc, action, index) in
                            if index != 0 {
                                self.chatTextField.placeholder = "请输入录音名称"
                                self.chatTextField.becomeFirstResponder()
                                self.confirmButton.tag = index
                                self.chatTextField.addTarget(self, action: #selector(self.chatTextFieldLimit), for: .editingChanged)
                                self.confirmButton.addTarget(self, action: #selector(self.didSendChatTextField), for: .touchUpInside)
                            }
                        }
                    } else if index == 3 {
                        //录音管理
                        let storyboard = UIStoryboard(name: "Main", bundle: nil)
                        let vc: UIViewController! = storyboard.instantiateViewController(withIdentifier: "ARChat_Record")
                        vc.modalPresentationStyle = .fullScreen
                        self.present(vc, animated: true, completion: nil)
                    }
                }
            } else {
                //结束
                UIAlertController.showAlert(in: self, withTitle: "确定结束录制？", message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { (alertVc, action, index) in
                    if index == 2 {
                        //结束录制
                        rtcKit.stopAudioRecording()
                        self.addOrUpdateChannel(key: "record", value: "0")
                        self.dismiss(animated: false, completion: nil)
                    }
                }
            }
        } else if (sender.tag == 55) {
            //音效
            NotificationCenter.default.post(name: UIResponder.chatNotificationSound, object: self, userInfo: ["open":NSNumber.init(value: sender.isSelected)])
            self.dismiss(animated: false, completion: nil)
        }
    }
    
    @objc func chatTextFieldLimit() {
        if chatTextField.text?.count == 0 {
            confirmButton.alpha = 0.3
        } else {
            confirmButton.alpha = 1.0
           if chatTextField.text!.count > 10 {
               let str: String = chatTextField.text!
               chatTextField.text = String(str.prefix(10))
            }
        }
    }
    
    @objc func didSendChatTextField() {
        let recordName: String? = chatTextField.text
        if recordName?.count != 0 {
            //设置录音名称
            let quality: ARAudioRecordingQuality?
            (self.confirmButton.tag == 2) ? (quality = .low ): (quality = .high)
            //录制日期
            let date = Date()
            let timeFormatter = DateFormatter()
            timeFormatter.dateFormat = "yyy.MM.dd HH:mm:ss"
            let nowTime = timeFormatter.string(from: date) as String
            //录制路径
            let filePath = String(format: "%@/%@_%@.wav",self.creatRecordPath(), recordName!, nowTime)
            rtcKit.startAudioRecording(filePath, sampleRate: 32000, quality: quality!)
            //开始录制
            addOrUpdateChannel(key: "record", value: "1")
            chatTextField.resignFirstResponder()
            chatTextField.text = ""
            self.dismiss(animated: false, completion: nil)
        }
    }
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if(touch.view == self.view) {
            self.dismiss(animated: true, completion: nil)
            return true
        } else {
            return false
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
}
