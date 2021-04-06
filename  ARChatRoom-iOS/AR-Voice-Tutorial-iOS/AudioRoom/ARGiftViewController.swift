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
    @IBOutlet weak var giftFromUidView: UIView!
    @IBOutlet weak var headImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    
    var channelUserModel: ARChatUserModel?
    
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
        let dic: NSMutableDictionary = ["gift": String(index)]
        if channelUserModel != nil {
            dic.setValue(channelUserModel?.uid, forKey: "uid")
            dic.setValue(channelUserModel?.name, forKey: "name")
        }
        NotificationCenter.default.post(name: UIResponder.chatNotificationGift, object: self, userInfo: dic as? [AnyHashable : Any])
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
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if channelUserModel != nil {
            giftFromUidView.isHidden = false
            headImageView.sd_setImage(with: NSURL(string: channelUserModel!.head!) as URL?, placeholderImage: UIImage(named: "icon_add"))
            
            var imageName = ""
            channelUserModel!.sex! ? (imageName = "icon_female") : (imageName = "icon_male")
            nameLabel.attributedText = getAttributedString(text: channelUserModel!.name!, image: UIImage(named: imageName)!, index: channelUserModel!.name!.count)
        }
    }
}
