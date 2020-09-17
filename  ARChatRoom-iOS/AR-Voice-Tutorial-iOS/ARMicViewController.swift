//
//  ARMicViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/15.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtmKit

class ARMicCell: UITableViewCell {
    
    @IBOutlet weak var headImageView: UIImageView!
    @IBOutlet weak var rejuctButton: UIButton!
    @IBOutlet weak var acceptButton: UIButton!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var micLabel: UILabel!
    
    func updateMicCell(userModel: ARChatUserModel, role: Bool) {
        if !role {
            acceptButton.isHidden = true
            rejuctButton.isHidden = true
        }
        nameLabel.text = userModel.name
        headImageView.sd_setImage(with: NSURL(string: userModel.head!) as URL?, placeholderImage: UIImage(named: "icon_head"))
        micLabel.text = String(format: "申请%@号麦", userModel.applyMic!)
    }
}

class ARMicViewController: UIViewController,UIGestureRecognizerDelegate {

    @IBOutlet weak var micButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var noMicLabel: UILabel!
    let tap = UITapGestureRecognizer()
    var hoster: Bool!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        (chatModel.waitModelList.count == 0) ? (noMicLabel.isHidden = false): (noMicLabel.isHidden = true)
        tableView.tableFooterView = UIView()
        tableView.separatorStyle = .none
        if !hoster {
            micButton.setTitle("取消排麦", for: .normal)
        }
        NotificationCenter.default.addObserver(self, selector: #selector(micRefresh), name: NSNotification.Name(rawValue:"ARChatNotificationMicRefresh"), object: nil)
    }
    
    
    @IBAction func didClickMicButton(_ sender: Any) {
        if hoster {
            //快速排麦
            
            deleteChannel(keys: ["waritinglist"])
        } else {
            //取消排麦
            let arr = chatModel.waitList
            for object in arr {
                let dic: NSDictionary = object as! NSDictionary
                let uid: String? = dic.object(forKey: "userid") as? String
                if uid == localUserModel.uid {
                    arr.remove(object)
                    if arr.count != 0 {
                        let channelAttribute: ARtmChannelAttribute = ARtmChannelAttribute()
                        channelAttribute.key = "waritinglist"
                        channelAttribute.value = getJSONStringFromArray(array: arr)
                        addOrUpdateChannel(attribute: channelAttribute)
                    } else {
                        deleteChannel(keys: ["waritinglist"])
                    }
                }
            }
            self.dismiss(animated: true, completion: nil)
        }
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
    
    @objc func micRefresh(nofi: Notification) {
        let result: Bool = nofi.userInfo!["micLock"] as! Bool
        if result {
            (chatModel.waitModelList.count == 0) ? (noMicLabel.isHidden = false): (noMicLabel.isHidden = true)
            tableView.reloadData()
        } else {
            self.dismiss(animated: false, completion: nil)
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
        print(" MicVc deinit")
    }
}

extension ARMicViewController: UITableViewDelegate,UITableViewDataSource {
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: ARMicCell = tableView.dequeueReusableCell(withIdentifier: "ARMicCellID") as! ARMicCell
        cell.selectionStyle = .none
        cell.updateMicCell(userModel: chatModel.waitModelList[indexPath.row] as! ARChatUserModel, role: hoster!)
        cell.acceptButton.tag = indexPath.row
        cell.rejuctButton.tag = indexPath.row
        cell.acceptButton.addTarget(self, action: #selector(acceptMic), for: .touchUpInside)
        cell.rejuctButton.addTarget(self, action: #selector(rejectMic), for: .touchUpInside)
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return chatModel.waitModelList.count
    }
    
    @objc func acceptMic(sender: UIButton) {
        //同意上麦
        if sender.tag < chatModel.waitModelList.count {
            let userModel: ARChatUserModel = chatModel.waitModelList[sender.tag] as! ARChatUserModel
            let dic: NSDictionary! = ["cmd": "acceptLine","seat": userModel.applyMic as Any]
            let message: ARtmMessage = ARtmMessage.init(text: getJSONStringFromDictionary(dictionary: dic))
            let options: ARtmSendMessageOptions = ARtmSendMessageOptions()
            ARVoiceRtm.rtmKit?.send(message, toPeer: userModel.uid!, sendMessageOptions: options, completion: { (errorCode) in
                
            })
            removeMicListFromUid(micUid: userModel.uid!)
        }
    }
    
    @objc func rejectMic(sender: UIButton) {
        //拒绝上麦
        if sender.tag < chatModel.waitModelList.count {
            let userModel: ARChatUserModel = chatModel.waitModelList[sender.tag] as! ARChatUserModel
            let dic: NSDictionary! = ["cmd": "rejectLine","reason": "拒绝上麦"]
            let message: ARtmMessage = ARtmMessage.init(text: getJSONStringFromDictionary(dictionary: dic))
            let options: ARtmSendMessageOptions = ARtmSendMessageOptions()
            ARVoiceRtm.rtmKit?.send(message, toPeer: userModel.uid!, sendMessageOptions: options, completion: { (errorCode) in
                
            })
            removeMicListFromUid(micUid: userModel.uid!)
        }
    }
}
