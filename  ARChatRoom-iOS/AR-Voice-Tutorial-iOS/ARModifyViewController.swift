//
//  ARModifyViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/9.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

enum ARChatInfoState: NSInteger {
    case name,announcement,welcome
}

class ARModifyViewController: UIViewController, UITextViewDelegate {
    
    @IBOutlet weak var infoTextView: UITextView!
    @IBOutlet weak var placeholderLabel: UILabel!
    @IBOutlet weak var limitLabel: UILabel!
    @IBOutlet weak var textViewHeight: NSLayoutConstraint!
    
    let list = ["房间名称","公告","欢迎语"]
    let placeholderList = ["请输入房间名称","请输入公告内容","请输入欢迎语"]
    let limitList = ["4~32","192","16"]
    var infoState: ARChatInfoState?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        placeholderLabel.text = placeholderList[infoState!.rawValue]
        limitLabel.text = limitList[infoState!.rawValue]
        (infoState == ARChatInfoState(rawValue: 1)) ? (textViewHeight.constant = 203) : (textViewHeight.constant = 64)
        
        let leftButton: UIButton = UIButton.init(type: .custom)
        leftButton.frame = CGRect.init(x: 0, y: 0, width: 100, height: 17)
        leftButton.setTitle(list[infoState!.rawValue], for: .normal)
        leftButton.titleLabel?.font = UIFont(name: "PingFang SC", size: 17)
        leftButton.setTitleColor(RGB(r: 96, g: 96, b: 96), for: .normal)
        leftButton.titleEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 0, right: 0);
        leftButton.setImage(UIImage(named: "icon_return"), for: .normal)
        leftButton.addTarget(self, action: #selector(didClickBackButton), for: .touchUpInside)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: leftButton)
        initializeChatInfo()
    }
    
    func initializeChatInfo() {
        if infoState == ARChatInfoState(rawValue: 0) {
            infoTextView.text = chatModel.roomName
        } else if (infoState == ARChatInfoState(rawValue: 1)) {
            infoTextView.text = chatModel.announcement
        } else {
            infoTextView.text = chatModel.welcome
        }
        
        if infoTextView.text.count != 0 {
            placeholderLabel.isHidden = true
        }
    }
    
    @objc func didClickBackButton() {
        let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
        var value: String!
        (infoTextView.text.count == 0) ? (value = "") : (value = infoTextView.text)
        if infoState == ARChatInfoState(rawValue: 0) {
            if value!.count >= 4 {
                channelAttribute.key = "roomName"
                channelAttribute.value = value
                chatModel.roomName = value
                addOrUpdateChannel(attribute: channelAttribute)
            } else {
                print("invalid")
            }
        } else if (infoState == ARChatInfoState(rawValue: 1)) {
            channelAttribute.key = "notice"
            channelAttribute.value = value
            addOrUpdateChannel(attribute: channelAttribute)
        } else {
            channelAttribute.key = "welecomeTip"
            channelAttribute.value = value
            addOrUpdateChannel(attribute: channelAttribute)
        }
        self.navigationController?.popViewController(animated: true)
    }
    
    func textViewDidChange(_ textView: UITextView) {
        (textView.text.count == 0) ? (placeholderLabel.isHidden = false) : (placeholderLabel.isHidden = true)
        if (infoState == ARChatInfoState(rawValue: 0)) {
            //4~32
            if textView.text.count > 32 {
                textView.text = String(textView.text.prefix(32))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
            }
        } else if (infoState == ARChatInfoState(rawValue: 1)) {
            //192
            if textView.text.count > 192 {
                textView.text = String(textView.text.prefix(192))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
            }
        } else if (infoState == ARChatInfoState(rawValue: 2)) {
            //16
            if textView.text.count > 16 {
                textView.text = String(textView.text.prefix(16))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
            }
        }
    }
}
