//
//  ARBaseViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/20.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARBaseViewController: UIViewController {
    
    var keyBoardView: UIView!
    var chatTextField: UITextField!
    var confirmButton: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.view.addSubview(getInputAccessoryView())
        NotificationCenter.default.addObserver(self,selector:#selector(keyboardChange(notify:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self,selector:#selector(keyboardChange(notify:)), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    func getInputAccessoryView() -> UIView {
        keyBoardView = UIView.init(frame: CGRect.init(x: 0, y: ARScreenHeight, width: ARScreenWidth, height: 53))
        keyBoardView.backgroundColor = UIColor.init(red: 247/255, green: 247/255, blue: 247/255, alpha: 1)
        
        chatTextField = UITextField.init(frame: CGRect.init(x: 10, y: 8, width: ARScreenWidth - 93, height: 38));
        chatTextField.font = UIFont.systemFont(ofSize: 14)
        chatTextField.layer.masksToBounds = true
        chatTextField.layer.cornerRadius = 5
        chatTextField.returnKeyType = .send
        chatTextField.backgroundColor = UIColor.white
        
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
        keyBoardView.addSubview(confirmButton)
        return keyBoardView
    }
    
    @objc func keyboardChange(notify:NSNotification){
        if chatTextField.isFirstResponder {
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
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
