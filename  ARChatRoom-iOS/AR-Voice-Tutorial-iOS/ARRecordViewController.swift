//
//  ARRecordViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/21.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARRecordCell: UITableViewCell {
    
    @IBOutlet weak var fileNameLabel: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var shareButton: UIButton!
    
    func updateRecordCell(fileName: String) {
        let arr: NSArray? = fileName.components(separatedBy: "_") as NSArray
        if arr?.count == 2 {
            fileNameLabel.text = arr![0] as? String
            let time: String? = arr![1] as? String
            timeLabel.text = String(time!.prefix(time!.count - 4))
        }
    }
}

class ARRecordViewController: UIViewController {
    
    @IBOutlet weak var recordTableView: UITableView!
    var recordArr: NSMutableArray = NSMutableArray()

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let leftButton: UIButton = UIButton.init(type: .custom)
        leftButton.frame = CGRect.init(x: 0, y: 0, width: 100, height: 17)
        leftButton.setTitle("录音管理", for: .normal)
        leftButton.titleLabel?.font = UIFont(name: "PingFang SC", size: 17)
        leftButton.setTitleColor(RGBA(r: 96, g: 96, b: 96, a: 1), for: .normal)
        leftButton.titleEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 0, right: 0);
        leftButton.setImage(UIImage(named: "icon_return"), for: .normal)
        leftButton.addTarget(self, action: #selector(didClickBackButton), for: .touchUpInside)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: leftButton)
        recordTableView.tableFooterView = UIView()
        
        let cachePath = creatRecordPath()
        let manger = FileManager.default
        do {
            let cintents1 = try manger.contentsOfDirectory(atPath: cachePath)
            recordArr.addObjects(from: cintents1)
            if recordArr.count > 0 {
                recordTableView.isHidden = false
            }
        } catch {
            print("Error occurs.")
        }
    }
    
    @objc func didClickShareButton(sender: UIButton) {
        let fileName: String! = (self.recordArr[sender.tag] as! String)
        let url: NSURL = NSURL.init(fileURLWithPath: String(format: "%@%@", creatRecordPath(),fileName))
        let activityVc = UIActivityViewController(activityItems: [url], applicationActivities: nil)
        self.present(activityVc, animated: true, completion: nil)
    }
    
    @objc func didClickBackButton() {
        self.dismiss(animated: true, completion: nil)
    }
}

extension ARRecordViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return recordArr.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: ARRecordCell = tableView.dequeueReusableCell(withIdentifier: "ARRecordCellID") as! ARRecordCell
        cell.updateRecordCell(fileName: recordArr[indexPath.row] as! String)
        cell.shareButton.tag = indexPath.row
        cell.shareButton.addTarget(self, action: #selector(didClickShareButton), for: .touchUpInside)
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        let rowAction: UITableViewRowAction = UITableViewRowAction.init(style: .destructive, title: "删除") { (action, indexPath) in
            let cachePath = self.creatRecordPath()
            let fileName: String? = self.recordArr[indexPath.row] as? String
            //删除文件
            let manger = FileManager.default
            do {
                try manger.removeItem(atPath: String(format: "%@%@", cachePath,fileName!))
            } catch {
                print("Error occurs.")
            }
            self.recordArr.removeObject(at: indexPath.row)
            tableView.reloadData()
            if self.recordArr.count == 0 {
                self.recordTableView.isHidden = true
            }
        }
        return [rowAction]
    }
}
