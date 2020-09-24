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
        leftButton.setTitleColor(RGBA(r: 96, g: 96, b: 96, a:1), for: .normal)
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
        infoTextView.resignFirstResponder()
        var value: String!
        value = getAttributeValue(text: infoTextView.text)
        if infoState == ARChatInfoState(rawValue: 0) {
            if value!.count >= 4 {
                if chatModel.roomName != value {
                    saveModify(key: "roomName", value: value)
                } else {
                    self.navigationController?.popViewController(animated: true)
                }
            } else {
                limitText(text: "内容保持在4~32个字以内")
            }
        } else if (infoState == ARChatInfoState(rawValue: 1)) {
            if chatModel.announcement != value {
                if !value.isEmpty {
                    saveModify(key: "notice", value: value)
                } else {
                    deleteChannel(keys: ["notice"])
                    self.navigationController?.popViewController(animated: true)
                }
            } else {
                self.navigationController?.popViewController(animated: true)
            }
        } else {
            if  (chatModel.welcome != value) {
                if !value.isEmpty {
                    saveModify(key: "welecomeTip", value: value)
                } else {
                    deleteChannel(keys: ["welecomeTip"])
                    self.navigationController?.popViewController(animated: true)
                }
            } else {
                self.navigationController?.popViewController(animated: true)
            }
        }
    }
    
    func saveModify(key: String,value: String) {
        UIAlertController.showAlert(in: self, withTitle: "确定修改？", message: nil, cancelButtonTitle: "取消", destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { [weak self] (alertVc, action, index) in
            if index == 2 {
                self?.addOrUpdateChannel(key: key, value: value)
            }
            
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.5) {
                self?.navigationController?.popViewController(animated: true)
            }
        }
    }
    
    func textViewDidChange(_ textView: UITextView) {
        (textView.text.count == 0) ? (placeholderLabel.isHidden = false) : (placeholderLabel.isHidden = true)
        if (infoState == ARChatInfoState(rawValue: 0)) {
            //4~32
            if textView.text.count > 32 {
                textView.text = String(textView.text.prefix(32))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
                limitText(text: "内容保持在4~32个字以内")
            }
        } else if (infoState == ARChatInfoState(rawValue: 1)) {
            //192
            if textView.text.count > 192 {
                textView.text = String(textView.text.prefix(192))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
                limitText(text: "内容保持在192个字以内")
            }
        } else if (infoState == ARChatInfoState(rawValue: 2)) {
            //16
            if textView.text.count > 16 {
                textView.text = String(textView.text.prefix(16))
                textView.undoManager?.removeAllActions()
                textView.becomeFirstResponder()
                limitText(text: "内容保持在16个字以内")
            }
        }
    }
    
    func limitText(text: String?) {
        UIAlertController.showAlert(in: self, withTitle: "提示", message: text, cancelButtonTitle: nil, destructiveButtonTitle: nil, otherButtonTitles: ["确定"]) { (vc, action, index) in
            
        }
    }
}
