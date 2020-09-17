//
//  ARChatInfoViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/9.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARChatInfoViewController: UITableViewController {
    
    let list = ["房间名称","公告","欢迎语"]

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        let leftButton: UIButton = UIButton.init(type: .custom)
        leftButton.frame = CGRect.init(x: 0, y: 0, width: 100, height: 17)
        leftButton.setTitle("房间信息", for: .normal)
        leftButton.titleLabel?.font = UIFont(name: "PingFang SC", size: 17)
        leftButton.setTitleColor(RGB(r: 96, g: 96, b: 96), for: .normal)
        leftButton.titleEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 0, right: 0);
        leftButton.setImage(UIImage(named: "icon_return"), for: .normal)
        leftButton.addTarget(self, action: #selector(didClickBackButton), for: .touchUpInside)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: leftButton)
        
        tableView.tableFooterView = UIView()
    }
    
    @objc func didClickBackButton() {
        self.dismiss(animated: true, completion: nil)
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 3
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellid = "reuseIdentifier"
        var cell = tableView.dequeueReusableCell(withIdentifier: cellid)
        // Configure the cell...
        if cell == nil {
            cell = UITableViewCell.init(style: .value1, reuseIdentifier: cellid)
        }
        
        if indexPath.row == 0 {
            cell?.detailTextLabel?.text = chatModel.roomName
            cell?.detailTextLabel?.font = UIFont(name: "PingFang SC", size: 15)
        }
        cell?.textLabel?.font = UIFont(name: "PingFang SC", size: 15)
        cell?.textLabel?.textColor = RGB(r: 51, g: 51, b: 51)
        cell?.textLabel?.text = list[indexPath.row]
        cell?.accessoryType = .disclosureIndicator
        cell?.selectionStyle = .none
        return cell!
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let vc: ARModifyViewController! = storyboard.instantiateViewController(withIdentifier: "ARChat_ModifyID") as? ARModifyViewController
        vc.infoState  = ARChatInfoState(rawValue: indexPath.row)
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.reloadData()
    }
}
