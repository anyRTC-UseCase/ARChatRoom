//
//  ARFansViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/10.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARFansCell: UITableViewCell {
    
    @IBOutlet weak var headImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var sexImageView: UIImageView!
    
    func updateFansCell(userModel: ARChatUserModel) {
        nameLabel?.text = userModel.name
        headImageView.sd_setImage(with: NSURL(string: userModel.head!) as URL?, placeholderImage: UIImage(named: "icon_head"))
        var imageName = ""
        userModel.sex! ? (imageName = "icon_female") : (imageName = "icon_male")
        sexImageView.image = UIImage(named: imageName)
    }
}

class ARFansViewController: UIViewController,UIGestureRecognizerDelegate {

    @IBOutlet weak var fansTableView: UITableView!
    let tap = UITapGestureRecognizer()
    var memberList: NSMutableArray!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        fansTableView.tableFooterView = UIView()
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
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
}

extension ARFansViewController: UITableViewDelegate,UITableViewDataSource {
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: ARFansCell = tableView.dequeueReusableCell(withIdentifier: "ARFansCellID") as! ARFansCell
        let userModel: ARChatUserModel = memberList![indexPath.row] as! ARChatUserModel
        cell.updateFansCell(userModel: userModel)
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return memberList.count
    }
}
