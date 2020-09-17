//
//  ARInfoDataViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/12.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

enum ARInfoDataState {
    /** 默认、未上麦、观众 */
    case none, noMic, audience
}

class ARInfoDataViewController: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var headImageView: UIImageView!
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var nameLabel: UILabel!
    /** 下麦 */
    @IBOutlet weak var wheatButton: UIButton!
    /** 禁麦 */
    @IBOutlet weak var micButton: UIButton!
    /** 禁言 */
    @IBOutlet weak var inputButton: UIButton!
    /** 转交主持 */
    @IBOutlet weak var transferButton: UIButton!
    /** 礼物 */
    @IBOutlet weak var giftButton: UIButton!
    
    let tap = UITapGestureRecognizer()
    var userModel: ARChatUserModel!
    /** 麦位 */
    var index: NSInteger = 0
    var state: ARInfoDataState?

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        
        if userModel != nil {
            headImageView.sd_setImage(with: NSURL(string: userModel!.head!) as URL?, placeholderImage: UIImage(named: "icon_add"))
            var imageName = ""
            userModel.sex! ? (imageName = "icon_female") : (imageName = "icon_male")
            nameLabel.attributedText = getAttributedString(text: userModel.name!, image: UIImage(named: imageName)!, index: userModel.name!.count)
            
            //禁麦
            if chatModel.muteMicList != nil {
                for object in chatModel.muteMicList {
                    let uid: String = object as! String
                    if uid == userModel.uid {
                        micButton.isSelected = true
                        break
                    }
                }
            }
            
            //禁言
            if chatModel.muteInputList != nil {
                for object in chatModel.muteInputList {
                    let uid: String = object as! String
                    if uid == userModel.uid {
                        inputButton.isSelected = true
                        break
                    }
                }
            }
        }
    }
    
    @IBAction func didClickInfoButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        if sender.tag == 50 {
            //下麦
            deleteChannel(keys: [String(format: "seat%d", index)])
        } else if (sender.tag == 51) {
            //禁麦
            let arr: NSMutableArray = NSMutableArray()
            if (chatModel!.muteMicList != nil) {
                [arr.addObjects(from: chatModel!.muteMicList as! [Any])]
            }
            sender.isSelected ? (arr.add(userModel.uid as Any)): (arr.remove(userModel.uid as Any))
            let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
            channelAttribute.key = "MuteMicList"
            channelAttribute.value = getJSONStringFromArray(array: arr)
            addOrUpdateChannel(attribute: channelAttribute)
        } else if (sender.tag == 52) {
            //禁言
            let arr: NSMutableArray = NSMutableArray()
            if (chatModel!.muteInputList != nil) {
                [arr.addObjects(from: chatModel!.muteInputList as! [Any])]
            }
            sender.isSelected ? (arr.add(userModel.uid as Any)): (arr.remove(userModel.uid as Any))
            let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
            channelAttribute.key = "MuteInputList"
            channelAttribute.value = getJSONStringFromArray(array: arr)
            addOrUpdateChannel(attribute: channelAttribute)
        } else if (sender.tag == 53) {
            //请出
            
        } else if (sender.tag == 54) {
            //转交主持
            let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
            channelAttribute.key = "host"
            channelAttribute.value = userModel.uid!
            addOrUpdateChannel(attribute: channelAttribute)
        } else if (sender.tag == 55) {
            //送礼
            NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationGiftFromUid"), object: self, userInfo: ["gift": userModel.uid as Any])
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func didClickCloseButton() {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if (userModel != nil) {
            if userModel.uid == localUserModel.uid {
                stackView.removeFromSuperview()
            }
        }
        
        if state == .noMic {
            wheatButton.isHidden = true
            micButton.isHidden = true
            transferButton.isHidden = true
        } else if state == .audience {
            for object in stackView.subviews {
                let button: UIButton = object as! UIButton
                if object != giftButton {
                    button.isHidden = true
                }
            }
        }
    }
}
