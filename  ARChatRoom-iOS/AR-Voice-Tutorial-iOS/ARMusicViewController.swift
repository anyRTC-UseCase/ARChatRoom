//
//  ARMusicViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/9.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit
import ARtcKit
import ARtmKit

class ARMusicCell: UITableViewCell {
    
    @IBOutlet weak var markLabel: UILabel!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var playButton: UIButton!
    @IBOutlet weak var stopButton: UIButton!
    @IBOutlet weak var animationImageView: UIImageView!
    
    let list: NSMutableArray! = NSMutableArray()
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        for i in 0...2 {
             let animationImage: UIImage! = UIImage(named: String(format: "icon_volume%d", i))
             list.add(animationImage as Any)
         }
    }
    
    func startAnimation() {
        animationImageView.animationImages = list as? [UIImage]
        animationImageView.animationDuration = 0.5
        animationImageView.animationRepeatCount = 0
        animationImageView.startAnimating()
    }
}

class ARMusicViewController: UITableViewController {
    
    private var list = ["最美的光","无名之辈"]
    var musicName: String?
    var musicState: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let leftButton: UIButton = UIButton.init(type: .custom)
        leftButton.frame = CGRect.init(x: 0, y: 0, width: 100, height: 17)
        leftButton.setTitle("音乐列表", for: .normal)
        leftButton.titleLabel?.font = UIFont(name: "PingFang SC", size: 17)
        leftButton.setTitleColor(RGBA(r: 96, g: 96, b: 96, a: 1), for: .normal)
        leftButton.titleEdgeInsets = UIEdgeInsets.init(top: 0, left: 10, bottom: 0, right: 0);
        leftButton.setImage(UIImage(named: "icon_return"), for: .normal)
        leftButton.addTarget(self, action: #selector(didClickBackButton), for: .touchUpInside)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: leftButton)
        tableView.tableFooterView = UIView()
        
        if chatModel.musicDic != nil {
            musicName = chatModel.musicDic.object(forKey: "name") as? String
            musicState = chatModel.musicDic.object(forKey: "state") as? String
        }
    }
    
    @objc func didClickBackButton() {
        self.dismiss(animated: true, completion: nil)
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return list.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let musicCell: ARMusicCell = tableView.dequeueReusableCell(withIdentifier:"ARMusicCellID", for: indexPath) as! ARMusicCell
        musicCell.nameLabel.text = list[indexPath.row]
        musicCell.playButton.tag = indexPath.row
        musicCell.stopButton.tag = indexPath.row
        musicCell.playButton.addTarget(self, action: #selector(didClickPlayButton), for: .touchUpInside)
        musicCell.stopButton.addTarget(self, action: #selector(didClickStopButton), for: .touchUpInside)
        
        if musicCell.nameLabel.text == musicName {
            if musicState == "open" {
                musicCell.playButton.isSelected = true
                musicCell.stopButton.isHidden = false
                musicCell.markLabel.isHidden = false
                musicCell.animationImageView.isHidden = false
                musicCell.startAnimation()
            } else if (musicState == "pause") {
                musicCell.playButton.isSelected = false
                musicCell.stopButton.isHidden = false
                musicCell.markLabel.isHidden = false
                
            } else if (musicState == "close") {
                musicCell.playButton.isSelected = false
                musicCell.stopButton.isHidden = true
                musicCell.markLabel.isHidden = true
                musicCell.animationImageView.isHidden = true
            }
        } else {
            musicCell.playButton.isSelected = false
            musicCell.stopButton.isHidden = true
            musicCell.markLabel.isHidden = true
            musicCell.animationImageView.isHidden = true
        }
        musicCell.selectionStyle = .none
        return musicCell;
    }
    
    @objc func didClickPlayButton(sender: UIButton) {
        if sender.isSelected {
            rtcKit.pauseAudioMixing()
            musicState = "pause"
        } else {
            if musicState == "pause" {
                rtcKit.resumeAudioMixing()
            } else {
                rtcKit.stopAudioMixing()
                let filePath: String = Bundle.main.path(forResource: list[sender.tag], ofType: ["mp3","m4a"][sender.tag])!
                rtcKit.startAudioMixing(filePath, loopback: false, replace: false, cycle: 1)
                rtcKit.setEnableSpeakerphone(true)
            }
            musicName = list[sender.tag]
            musicState = "open"
        }
        self.tableView.reloadData()
        updateMusicChannelAttribute()
    }
    
    @objc func didClickStopButton(sender: UIButton) {
        rtcKit.stopAudioMixing()
        musicState = "close"
        self.tableView.reloadData()
        updateMusicChannelAttribute()
    }
    
    func updateMusicChannelAttribute() {
        let dict: NSDictionary = ["name": musicName!,"state": musicState!]
        addOrUpdateChannel(key: "music", value: getJSONStringFromDictionary(dictionary: dict))
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .default
    }
}
