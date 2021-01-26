//
//  ViewController.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/1.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var headImageView: UIImageView!
    @IBOutlet weak var maleButton: UIButton!
    @IBOutlet weak var femaleButton: UIButton!
    @IBOutlet weak var nameTextField: UITextField!
    //头像
    var url: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        switchHead(sex: false)
    }
    
    @IBAction func didClickGenderButton(_ sender: UIButton!) {
        if sender == maleButton {
            if !sender.isSelected {
                sender.isSelected = true
                femaleButton.isSelected = false
                switchHead(sex: false)
            }
        } else {
            if !sender.isSelected {
                switchHead(sex: true)
                sender.isSelected = true
                maleButton.isSelected = false
            }
        }
    }
    
    func switchHead(sex: Bool) {
        let random: NSInteger = NSInteger(arc4random() % 10 + 1)
        if sex {
            url = String(format: "https://teameeting.oss-cn-shanghai.aliyuncs.com/play/woman/head%d.jpeg", random)
        } else {
            url = String(format: "https://teameeting.oss-cn-shanghai.aliyuncs.com/play/man/head%d.jpeg", random)
        }
        headImageView.sd_setImage(with: NSURL(string: url!) as URL?, placeholderImage: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination is ARMainViewController {
            let mainVc: ARMainViewController = segue.destination as! ARMainViewController
            mainVc.modalPresentationCapturesStatusBarAppearance = true
        }
    }
    
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        if !isBlank(text: nameTextField.text) {
            var userModel: ARUserModel? = getUserInformation()
            if userModel == nil {
                userModel = ARUserModel()
                userModel?.uid = String(format: "%d", Int(arc4random_uniform(899999) + 100000))
                userModel?.head = url
            }
            userModel?.sex = femaleButton.isSelected
            userModel?.name = nameTextField.text
            saveUserInformation(model: userModel!)
            return true
        } else {
            XHToast.showCenter(withText: "请输入昵称")
        }
        return false
    }
    
    override func viewWillAppear(_ animated: Bool) {
        headImageView.layer.cornerRadius = ARScreenWidth * 0.3/2
        headImageView.layer.borderColor = UIColor.lightGray.cgColor
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
}

