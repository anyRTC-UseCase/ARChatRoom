//
//  ARVolumeViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/9.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARVolumeViewController: UIViewController,UIGestureRecognizerDelegate{
    
    @IBOutlet weak var musicSlider: UISlider!
    @IBOutlet weak var voicesSlider: UISlider!
    @IBOutlet weak var earSlider: UISlider!
    
    let tap = UITapGestureRecognizer()

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        
        musicSlider.value = localUserModel.musicVolume/100.0
        voicesSlider.value = localUserModel.voices/100.0
        earSlider.value = localUserModel.earVolume/100.0
    }
    
    @IBAction func didClickVolumeSlider(_ sender: UISlider) {
        if sender.tag == 50 {
            //音乐声
            rtcKit.adjustAudioMixingVolume(Int(sender.value * 100))
        } else if (sender.tag == 51) {
            //人声
            rtcKit.adjustRecordingSignalVolume(Int(sender.value * 100))
        } else if (sender.tag == 52) {
            //耳返
            rtcKit.setInEarMonitoringVolume(Int(sender.value * 100))
        }
    }
    
    @IBAction func didClickCloseButton(_ sender: Any) {
        saveVolume()
        self.dismiss(animated: true, completion: nil)
    }
    
    func saveVolume() {
        //保存设置
        localUserModel.musicVolume = musicSlider.value * 100
        localUserModel.voices = voicesSlider.value * 100
        localUserModel.earVolume = earSlider.value * 100
        saveUserInformation(model: localUserModel)
    }
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if(touch.view == self.view) {
            saveVolume()
            self.dismiss(animated: true, completion: nil)
            return true
        } else {
            return false
        }
    }
}
