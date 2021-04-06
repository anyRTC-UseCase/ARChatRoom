//
//  ARFloatingView.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/13.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARFloatingView: WMDragView {
    
    var backView: UIView!
    var headImageView: UIImageView!
    var roomLabel: UILabel!
    var closeButton: UIButton!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.frame = CGRect.init(x: ARScreenWidth - 195, y: ARScreenHeight - 175, width: 195, height: 62)
        self.backgroundColor = UIColor.init(hexString: "#DDDDE0")
        
        let maskPath0 = UIBezierPath(roundedRect: bounds,byRoundingCorners: [.topLeft, .bottomLeft], cornerRadii:CGSize(width:31, height:31))
        let masklayer0 = CAShapeLayer()
        masklayer0.frame = bounds
        masklayer0.path = maskPath0.cgPath
        self.layer.mask = masklayer0
        setUpContent()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setUpContent() {
        backView = UIView.init(frame: CGRect.init(x: 6, y: 6, width: 183, height: 50))
        backView.backgroundColor = UIColor.white
        self.addSubview(backView)
        
        let maskPath1 = UIBezierPath(roundedRect: backView.bounds,byRoundingCorners: [.topLeft, .bottomLeft], cornerRadii:CGSize(width:25, height:25))
        let masklayer1 = CAShapeLayer()
        masklayer1.frame = bounds
        masklayer1.path = maskPath1.cgPath
        backView.layer.mask = masklayer1
        
        headImageView = UIImageView.init(image: UIImage(named: "icon_head"))
        backView.addSubview(headImageView)
        headImageView.snp.makeConstraints { (make) in
            make.left.equalTo(backView).offset(5)
            make.width.height.equalTo(60)
            make.centerY.equalToSuperview()
        }
        
        closeButton = UIButton.init(type: .custom)
        closeButton.setImage(UIImage.init(named: "icon_float_close"), for: .normal)
        backView.addSubview(closeButton)
        closeButton.snp.makeConstraints { (make) in
            make.right.equalToSuperview().offset(-15)
            make.width.height.equalTo(30)
            make.centerY.equalToSuperview()
        }
        
        roomLabel = UILabel.init()
        roomLabel.text = "一起聊天吧"
        roomLabel.textColor = UIColor.init(hexString: "#606060")
        roomLabel.font = UIFont(name: "PingFang SC", size: 10)
        backView.addSubview(roomLabel)
        roomLabel.snp.makeConstraints { (make) in
            make.left.equalTo(headImageView.snp_right).offset(6)
            make.right.equalTo(closeButton.snp_left).offset(-6)
            make.centerY.equalTo(backView.snp_centerY)
        }
    }
}
