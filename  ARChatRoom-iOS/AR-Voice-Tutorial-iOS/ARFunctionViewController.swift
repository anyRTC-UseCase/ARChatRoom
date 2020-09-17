//
//  ARFunctionViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/10.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

protocol ARFunctionDelegate {
    func clickOperatio(sender: UIButton);
}

class ARFunctionViewController: UIViewController, UIGestureRecognizerDelegate{

    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var soundButton: UIButton!
    @IBOutlet weak var passwordButton: UIButton!
    
    let tap = UITapGestureRecognizer()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        micButton.isSelected = chatModel.isMicLock!
        soundButton.isSelected = chatModel.sound
        
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
            let attribute: ARtmChannelAttribute = ARtmChannelAttribute()
            attribute.key = "isMicLock"
            sender.isSelected ? (attribute.value = "1") : (attribute.value = "0")
            addOrUpdateChannel(attribute: attribute)
            self.dismiss(animated: false, completion: nil)
        } else if (sender.tag == 52) {
            //设置密码
             NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationPassWord"), object: self, userInfo: ["state": NSNumber.init(value: sender.isSelected)])
            self.dismiss(animated: false, completion: nil)
        } else if (sender.tag == 53) {
            //音量
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let vc: UIViewController! = storyboard.instantiateViewController(withIdentifier: "ARChat_VolumeID")
            vc.modalPresentationStyle = .overCurrentContext
            self.present(vc, animated: true, completion: nil)
        } else if (sender.tag == 54) {
            //录音
            XHToast.showCenter(withText: "敬请期待")
        } else if (sender.tag == 55) {
            //音效
            NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationSound"), object: self, userInfo: ["open":NSNumber.init(value: sender.isSelected)])
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
}
