//
//  ARMicView.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/3.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

enum ARMicStatus {
    //默认、麦上有人、麦位被锁、麦位有人&&被锁
    case micDefault, micExist, micLock, micExistLock
}

protocol ARMicDelegate {
    func didClickMicView(index: NSInteger, status: ARMicStatus, userModel: ARChatUserModel?);
}

class ARWavesView: UIView {

    override func draw(_ rect: CGRect) {
        let radius: CGFloat = 30
        let startAngle: CGFloat = 0
        
        let center = self.center
        let endAngle: CGFloat = 2 * CGFloat(Double.pi)
        
        let path = UIBezierPath(arcCenter: center, radius: radius, startAngle: startAngle, endAngle: endAngle, clockwise: true)
        let layer = CAShapeLayer()
        layer.path = path.cgPath
        layer.strokeColor = UIColor.orange.cgColor
        layer.fillColor = UIColor.clear.cgColor
        
        self.layer.addSublayer(layer)
    }
}

class ARMicView: UIView {
    
    @IBOutlet weak var micImageView: UIImageView!
    @IBOutlet weak var lockImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var sexImageView: UIImageView!
    @IBOutlet weak var padding: NSLayoutConstraint!
    
    var micStatus: ARMicStatus? = .micDefault
    fileprivate let tap = UITapGestureRecognizer()
    var delegate: ARMicDelegate?
    
    var uid: String? {
        didSet
        {
            if uid?.count != 0 && uid != nil {
                lockImageView.isHidden = true
                sexImageView.isHidden = false
                padding.constant = -6
            } else {
                micImageView.image = UIImage(named:"icon_add")
                sexImageView.isHidden = true
                padding.constant = 0
                if self.tag > 0 {
                    self.titleLabel.text = String(format:"%d号麦位",self.tag)
                } else {
                    self.titleLabel.text = "主持麦"
                }
                if ARMicStatus.micLock == micStatus && self.tag != 0 {
                    lockImageView.isHidden = false
                }
            }
        }
    }
    
    var chatUserModel: ARChatUserModel! {
        didSet
        {
            self.micImageView.sd_setImage(with: NSURL(string: chatUserModel.head!) as URL?, placeholderImage: UIImage(named: "icon_add"))
            
            var imageName = ""
            chatUserModel.sex! ? (imageName = "icon_female") : (imageName = "icon_male")
            self.titleLabel.text = chatUserModel.name
            self.sexImageView.image = UIImage(named: imageName)
        }
    }
    
    class func videoView() -> ARMicView {
        
        return Bundle.main.loadNibNamed("ARMicView", owner: nil, options: nil)![0] as! ARMicView
    }
    
    override func awakeFromNib() {
        tap.addTarget(self, action: #selector(didClickMicView))
        self.addGestureRecognizer(tap)
    }
    
    @objc func didClickMicView() {
        if (self.delegate != nil) {
            
            if micStatus != .micLock {
                if uid != nil && uid?.count != 0 {
                    micStatus = .micExist
                } else {
                    micStatus = .micDefault
                }
            } else {
                if self.tag == 0 {
                    (uid != nil && uid?.count != 0) ? (micStatus = .micExist) :(micStatus = .micDefault)
                } else {
                    lockImageView.isHidden ? (micStatus = .micExistLock) :(micStatus = .micLock)
                }
            }
            
            self.delegate?.didClickMicView(index: self.tag, status: micStatus!, userModel: chatUserModel)
        }
    }
    
    var timer: Timer!

    //音频检测
    public func startAudioAnimation() {
        if timer == nil {
            timer = Timer.scheduledTimer(timeInterval: 0.35, target: self, selector: #selector(wavesRippleAnimation), userInfo: nil, repeats: true)
        }
    }
    
    //水波动画
    @objc func wavesRippleAnimation() {
        let wavesView = ARWavesView(frame:micImageView.bounds)
        wavesView.backgroundColor = UIColor.clear
        micImageView.addSubview(wavesView)
        
        UIView.animate(withDuration: 1, animations: {
            wavesView.transform = wavesView.transform.scaledBy(x: 3, y: 3)
            wavesView.alpha = 0
        }) { (true) in
            wavesView.removeFromSuperview()
            if self.timer != nil {
                self.timer.invalidate()
                self.timer = nil
            }
        }
    }
}
