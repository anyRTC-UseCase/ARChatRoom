//
//  LogViewController.swift
//  AR-iOS-Tutorial
//
//  Created by 余生丶 on 2020/4/27.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

enum ARContentType {
    /** 普通消息、系统、上麦消息、礼物消息、进出消息、录音、欢迎语 、密码*/
    case info, warn, mic, gift, join, record, welcom, password
}

class ARLogModel: NSObject {
    //消息类型
    var contentType: ARContentType? = .info
    //聊天内容
    var content: String? = ""
    //麦位
    var micLocation: String? = ""
    //上下麦
    var micState: Bool? = false
    //加入离开
    var join: Bool? = false
    //录音状态
    var record: Bool? = false
    //房间密码
    var password: Bool? = false
    //欢迎语
    var welcom: String? = ""
    //发送者 -- id
    var fromUid: String? = ""
    //发送者 -- 昵称
    var fromName: String? = ""
    //礼物接受者 -- id
    var toUid: String? = ""
    //礼物接收者 -- 昵称
    var toName: String? = ""
    //礼物名称
    var giftName: String? = ""
    
    //创建消息
    class func createMessageMode(type: ARContentType, text: String?,micLocation: String?, micState: Bool?, record: Bool?, password: Bool?, welcom: String?, join: Bool?, fromUid: String?, fromName: String?, toUid: String?, toName: String?, giftName: String?) -> ARLogModel! {
        let logModel: ARLogModel = ARLogModel()
        logModel.contentType = type
        logModel.content = text
        logModel.micLocation = micLocation
        logModel.micState = micState
        logModel.record = record
        logModel.password = password
        logModel.welcom = welcom
        logModel.join = join
        logModel.fromUid = fromUid
        logModel.fromName = fromName
        logModel.toUid = toUid
        logModel.toName = toName
        logModel.giftName = giftName
        return logModel
    }
}

class LogCell: UITableViewCell {
    @IBOutlet weak var contentLabel: TTTAttributedLabel!
    @IBOutlet weak var colorView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        colorView.layer.cornerRadius = 12.25
    }
    
    func update(logModel: ARLogModel) {
        colorView.backgroundColor = UIColor.init(hue: 0.0, saturation: 0.0, brightness: 0.0, alpha: 0.3)
        contentLabel.attributedText = nil
        if logModel.contentType == .info {
            //聊天消息
            let clickText: String! = logModel.fromName
            let text: String! = String(format: "%@：%@", clickText,logModel.content!)
            let fromLink = contentLabel.addLink(toPhoneNumber: clickText as String?, with: NSRange.init(location: 0, length: clickText!.count))
            fromLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                self?.didClickUid(uid: logModel.fromUid)
            })
            
            let contentAttributedString = NSMutableAttributedString.init(string: text)
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: RGBA(r: 124, g: 227, b: 255, a: 1), range: NSMakeRange(0, clickText!.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.white, range: NSMakeRange(clickText!.count, text!.count - clickText!.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text!.count))
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .warn) {
            //系统警告
            let attr = [
                NSAttributedString.Key.font: UIFont(name: "PingFang SC", size: 14),
                NSAttributedString.Key.foregroundColor: UIColor.init(hexString: "#FFEC99")
             ]
            let contentAttributedString = NSAttributedString.init(string: logModel.content!, attributes: attr as [NSAttributedString.Key : Any])
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .mic) {
            //上麦信息
            let clickText: String! = String(format: "%@",logModel.fromName!)
            var text: String! = ""
            var state = ""
            logModel.micState! ? (state = "上了") : (state = "下了")
            if logModel.micLocation == "0" {
                text = String(format: "%@ %@主持麦", clickText, state)
            } else {
                text = String(format: "%@ %@%@号麦", clickText, state, logModel.micLocation!)
            }
            
            let fromLink = contentLabel.addLink(toPhoneNumber: clickText as String?, with: NSRange.init(location: 0, length: clickText!.count))
            
            let contentAttributedString = NSMutableAttributedString.init(string: text)
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: RGBA(r: 124, g: 227, b: 255, a: 1), range: NSMakeRange(0, clickText!.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.orange, range: NSMakeRange(clickText!.count, text!.count - clickText!.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text!.count))
            contentLabel.attributedText = contentAttributedString
            
            fromLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                 self?.didClickUid(uid: logModel.fromUid)
             })
        } else if (logModel.contentType == .gift) {
            //礼物
            var clickText: String! = ""
            (logModel.toUid!.count != 0) ? (clickText = logModel.toName!) : (clickText = "麦上全体人员")
            let text: String! = String(format: "%@ 赠送 %@ 1个%@",logModel.fromName!, clickText, logModel.giftName!)
            
            let fromLink = contentLabel.addLink(toPhoneNumber: logModel.fromName! as String?, with: NSRange.init(location: 0, length: logModel.fromName!.count))
            fromLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                self?.didClickUid(uid: logModel.fromUid)
            })
            
            if logModel.toUid!.count != 0 {
                let toLink = contentLabel.addLink(toPhoneNumber: clickText, with: NSRange.init(location: logModel.fromName!.count + 4, length: clickText.count))
                toLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                    self?.didClickUid(uid: logModel.toUid)
                })
            }
            
            let contentAttributedString = NSMutableAttributedString.init(string: text)
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#7CE3FF"), range: NSMakeRange(0, logModel.fromName!.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#1DC158"), range: NSMakeRange(logModel.fromName!.count, 3))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#FFFFFF"), range: NSMakeRange(logModel.fromName!.count + 4, clickText.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#FC4070"), range: NSMakeRange(text.count - logModel.giftName!.count - 2, logModel.giftName!.count + 2))
            contentAttributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text!.count))
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .join) {
            //进出房间
            colorView.backgroundColor = UIColor.init(hexString: "#40A3FB")
            var state: String?
            logModel.join! ? (state = "进入了房间") : (state = "离开了房间")
            let fromLink = contentLabel.addLink(toPhoneNumber: logModel.fromName!, with: NSRange.init(location: 0, length: logModel.fromName!.count))
            fromLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                self?.didClickUid(uid: logModel.fromUid)
            })
            
            let attr = [
                NSAttributedString.Key.font: UIFont(name: "PingFang SC", size: 14),
                NSAttributedString.Key.foregroundColor: UIColor.init(hexString: "#FFFFFF")
             ]
            let contentAttributedString = NSAttributedString.init(string: String(format: "%@ %@",logModel.fromName!,state!), attributes: attr as [NSAttributedString.Key : Any])
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .record) {
            //录音
            var state: String?
            logModel.record! ? (state = "房主开启了录音") : (state = "房主结束了录音")
            var clickText: String! = ""
            (logModel.fromName == nil) ? (clickText = "") : (clickText = logModel.fromName)
            let text: String! = String(format: "%@ %@",clickText, state!)
            
            let fromLink = contentLabel.addLink(toPhoneNumber: logModel.fromName!, with: NSRange.init(location: 0, length: logModel.fromName!.count))
            fromLink?.linkTapBlock = TTTAttributedLabelLinkBlock?.init({ [weak self] (TTTAttributedLabel, TTTAttributedLabelLink) in
                self?.didClickUid(uid: logModel.fromUid)
            })
            
            let contentAttributedString = NSMutableAttributedString.init(string: text)
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#7CE3FF"), range: NSMakeRange(0, clickText.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#C689FF"), range: NSMakeRange(clickText.count, text.count - clickText.count))
            contentAttributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text!.count))
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .welcom) {
            //欢迎语
            let attr = [
                NSAttributedString.Key.font: UIFont(name: "PingFang SC", size: 14),
                NSAttributedString.Key.foregroundColor: UIColor.init(hexString: "#C689FF")
             ]
            let contentAttributedString = NSAttributedString.init(string: String(format: "欢迎语：%@", logModel.welcom!), attributes: attr as [NSAttributedString.Key : Any])
            contentLabel.attributedText = contentAttributedString
        } else if (logModel.contentType == .password) {
            //密码
            var state: String?
            logModel.password! ? (state = "主持人为房间设置了密码") : (state = "主持人解除了房间密码")
            let text: String! = String(format: "系统: %@",state!)
            let contentAttributedString = NSMutableAttributedString.init(string: text)
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#FFEC99"), range: NSMakeRange(0, 3))
            contentAttributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#C689FF"), range: NSMakeRange(3, text.count - 3))
            contentAttributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text!.count))
            contentLabel.attributedText = contentAttributedString
        }
    }
    
    func didClickUid(uid: String?) {
        NotificationCenter.default.post(name: UIResponder.chatNotificationMessageUid, object: self, userInfo: ["uid": uid as Any])
    }
}

class LogViewController: UITableViewController {
    private lazy var list = [ARLogModel]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 44
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return list.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "LogCell", for: indexPath) as! LogCell
        cell.update(logModel: list[indexPath.row])
        return cell
    }
}

extension LogViewController {
    func log(logModel: ARLogModel) {
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.25) {
            self.list.append(logModel)
            let index = IndexPath(row: self.list.count - 1, section: 0)
            self.tableView.insertRows(at: [index], with: .automatic)
            self.tableView.scrollToRow(at: index, at: .middle, animated: false)
        }
    }
}
