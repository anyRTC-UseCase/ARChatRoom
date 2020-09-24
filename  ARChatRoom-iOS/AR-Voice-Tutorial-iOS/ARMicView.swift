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

protocol ARMicDelegate: NSObjectProtocol {
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
    @IBOutlet weak var banMicImageView: UIImageView!
    
    var micStatus: ARMicStatus? = .micDefault
    fileprivate let tap = UITapGestureRecognizer()
    weak var delegate: ARMicDelegate?
    
    var wavesView: ARWavesAnimationView?
    
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
                
                if wavesView != nil {
                    wavesView!.removeFromSuperview()
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
    
    func banMic(ban: Bool) {
        banMicImageView.isHidden = !ban
    }

    //音频检测
    public func startAudioAnimation() {
        if wavesView == nil {
            wavesView = ARWavesAnimationView.init(frame: CGRect.init(x: 0, y: 0, width: 64, height: 64))
            wavesView!.center = micImageView.center
            self.insertSubview(wavesView!, at: 0)
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 3) {
                if self.wavesView != nil {
                    self.wavesView!.removeFromSuperview()
                    self.wavesView = nil
                }
            }
        }
    }
}
