//
//  ARGiftViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/13.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARGiftViewController: UIViewController, UIGestureRecognizerDelegate {

    @IBOutlet weak var stackView0: UIStackView!
    @IBOutlet weak var stackView1: UIStackView!
    
    var giftList: NSMutableArray! = NSMutableArray()
    /** 礼物标识 */
    var index: NSInteger = 0
    let tap = UITapGestureRecognizer()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        for object in stackView0.subviews {
            giftList.add(object)
        }
        for object in stackView1.subviews {
            giftList.add(object)
        }
    }
    
    @IBAction func didClickGivingButton(_ sender: UIButton) {
        NotificationCenter.default.post(name: NSNotification.Name("ARChatNotificationGift"), object: self, userInfo: ["gift": String(index)])
        self.dismiss(animated: false, completion: nil)
    }
    
    @IBAction func didClickGiftButton(_ sender: UIButton) {
        for object in giftList {
            let button: UIButton! = object as? UIButton
            button.isSelected = false
        }
        sender.isSelected.toggle()
        index = sender.tag
    }
    
    @IBAction func didClickCloseButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
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
