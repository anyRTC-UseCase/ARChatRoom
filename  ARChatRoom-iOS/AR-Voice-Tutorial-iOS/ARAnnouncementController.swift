//
//  ARAnnouncementController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/10.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARAnnouncementController: UIViewController, UIGestureRecognizerDelegate {
    
    @IBOutlet weak var announcementTextView: UITextView!
    
    let tap = UITapGestureRecognizer()

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        tap.addTarget(self, action: #selector(didClickCloseButton))
        tap.delegate = self
        self.view.addGestureRecognizer(tap)
        
        let info: String? = chatModel.announcement
        if info?.count != 0 && info != nil {
            announcementTextView.isHidden = false
            announcementTextView.text = info
        }
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
}
