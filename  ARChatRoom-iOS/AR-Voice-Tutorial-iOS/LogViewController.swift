//
//  LogViewController.swift
//  AR-iOS-Tutorial
//
//  Created by 余生丶 on 2020/4/27.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

enum ARContentType {
    /** 普通消息、系统消息、上麦消息、礼物消息 */
    case info, warning, mic, gift
}

class ARLogModel: NSObject {
    var contentType: ARContentType? = .info
    var userModel: ARChatUserModel?
    var content: String?
}

class LogCell: UITableViewCell {
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var colorView: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        colorView.layer.cornerRadius = 12.25
    }
    
    func update(logModel: ARLogModel) {
        colorView.backgroundColor = UIColor.init(hue: 0.0, saturation: 0.0, brightness: 0.0, alpha: 0.3)
        if logModel.contentType == .info {
            let clickText: String! = String(format: "%@：",logModel.userModel!.name!)
            let text = String(format: "%@%@", clickText,logModel.content!)
            let attStr = NSMutableAttributedString.init(string: text)
            attStr.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.init(hexString: "#74DEFA"), range: NSMakeRange(0, clickText.count))
            attStr.addAttribute(NSAttributedString.Key.font, value: UIFont(name: "PingFang SC", size: 14) as Any, range: NSMakeRange(0, text.count))
            contentLabel.yb_addAttributeTapAction(with: [logModel.userModel!.name!]) { (label, string, range, int) in
            }
            contentLabel.attributedText = attStr
        } else if (logModel.contentType == .warning) {
            contentLabel.text = logModel.content
            contentLabel.textColor = UIColor.init(hexString: "#FFEC99")
        }
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
        DispatchQueue.main.async {
            self.list.append(logModel)
            let index = IndexPath(row: self.list.count - 1, section: 0)
            self.tableView.insertRows(at: [index], with: .automatic)
            self.tableView.scrollToRow(at: index, at: .middle, animated: false)
        };
    }
}
