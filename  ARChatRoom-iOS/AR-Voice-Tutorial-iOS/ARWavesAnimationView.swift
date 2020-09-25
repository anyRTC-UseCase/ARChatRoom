//
//  ARWavesAnimationView.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/23.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARWavesAnimationView: UIView {

    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    override func draw(_ rect: CGRect) {
        let animationLayer: CALayer = CALayer()
        for i in 0...3 {
            let scaleAnimation: CABasicAnimation = self.scaleAnimation()
            let borderColorAnimation: CAKeyframeAnimation = self.borderColorAnimation()
            let backgroundColorAnimation: CAKeyframeAnimation = self.backgroundColorAnimation()
            
            let animationArray = [scaleAnimation, borderColorAnimation, backgroundColorAnimation]
            let animationGroup: CAAnimationGroup = self.animationGroupAnimations(array: animationArray as NSArray, index: i)
            
            let pulsingLayer: CALayer = self.pulsingLayer(rect: rect, animation: animationGroup)
            animationLayer.addSublayer(pulsingLayer)
        }
        self.layer.addSublayer(animationLayer)
    }
    
    func animationGroupAnimations(array: NSArray, index: NSInteger) -> CAAnimationGroup {
        let defaultCurve: CAMediaTimingFunction = CAMediaTimingFunction.init(name: CAMediaTimingFunctionName.default)
        let animationGroup: CAAnimationGroup = CAAnimationGroup()
        animationGroup.fillMode = CAMediaTimingFillMode.backwards
        animationGroup.beginTime = CACurrentMediaTime() + Double(index)
        animationGroup.duration = 3
        animationGroup.repeatCount = HUGE
        animationGroup.timingFunction = defaultCurve
        animationGroup.animations = array as? [CAAnimation]
        animationGroup.isRemovedOnCompletion = false
        return animationGroup
    }
    
    func pulsingLayer(rect: CGRect, animation: CAAnimationGroup) -> CALayer {
        let pulsingLayer: CALayer = CALayer()
        pulsingLayer.frame = CGRect.init(x: 0, y: 0, width: rect.size.width, height: rect.size.height)
        pulsingLayer.backgroundColor = RGBA(r: 255, g: 216, b: 87, a: 0.5).cgColor
        pulsingLayer.borderWidth = 0.5
        pulsingLayer.cornerRadius = rect.size.height / 2;
        pulsingLayer.add(animation, forKey: "plulsing")
        return pulsingLayer
    }
    
    func scaleAnimation() -> CABasicAnimation {
        let scaleAnimation: CABasicAnimation = CABasicAnimation(keyPath: "transform.scale")
        scaleAnimation.fromValue = 1.0
        scaleAnimation.toValue = 1.40
        return scaleAnimation
    }
    
    func backgroundColorAnimation() -> CAKeyframeAnimation {
        let backgroundColorAnimation: CAKeyframeAnimation = CAKeyframeAnimation(keyPath: "backgroundColor")
        backgroundColorAnimation.values = [RGBA(r: 255, g: 216, b: 87, a: 0.5).cgColor,RGBA(r: 255, g: 231, b: 152, a: 0.5).cgColor,RGBA(r: 255, g: 241, b: 197, a: 0.5).cgColor,RGBA(r: 255, g: 241, b: 197, a: 0).cgColor]
        backgroundColorAnimation.keyTimes = [0.3,0.6,0.9,1]
        return backgroundColorAnimation
    }
    
    func borderColorAnimation() -> CAKeyframeAnimation {
        let borderColorAnimation: CAKeyframeAnimation = CAKeyframeAnimation(keyPath: "borderColor")
        borderColorAnimation.values = [RGBA(r: 255, g: 216, b: 87, a: 0.5).cgColor,RGBA(r: 255, g: 231, b: 152, a: 0.5).cgColor,RGBA(r: 255, g: 241, b: 197, a: 0.5).cgColor,RGBA(r: 255, g: 241, b: 197, a: 0).cgColor]
        borderColorAnimation.keyTimes = [0.3,0.6,0.9,1]
        return borderColorAnimation;
    }
    
    func blackBorderColorAnimation() -> CAKeyframeAnimation {
        let borderColorAnimation: CAKeyframeAnimation = CAKeyframeAnimation(keyPath: "borderColor")
        borderColorAnimation.values = [RGBA(r: 0, g: 0, b: 0, a: 0.4).cgColor,RGBA(r: 0, g: 0, b: 0, a: 0.4).cgColor,RGBA(r: 0, g: 0, b: 0, a: 0.1).cgColor,RGBA(r: 0, g: 0, b: 0, a: 0).cgColor]
        borderColorAnimation.keyTimes = [0.3,0.6,0.9,1]
        return borderColorAnimation
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
