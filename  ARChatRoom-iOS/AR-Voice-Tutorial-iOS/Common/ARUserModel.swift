//
//  ARUserModel.swift
//  AR-Voice-Tutorial-iOS
//
//  Created by 余生丶 on 2020/9/2.
//  Copyright © 2020 AR. All rights reserved.
//

import UIKit

class ARUserModel: NSObject,NSCoding {
    
    //用户随机id
    var uid: String?
    //用户昵称
    var name: String?
    //用户随机头像
    var head: String?
    //true 女 false男
    var sex: Bool?
    //音乐声
    var musicVolume: Float = 40.0
    //人声
    var voices: Float = 60.0
    //耳返
    var earVolume: Float = 0.0
    
    required init?(coder: NSCoder) {
        super.init()
        uid = coder.decodeObject(forKey: "uid") as? String
        name = coder.decodeObject(forKey: "name") as? String
        head = coder.decodeObject(forKey: "head") as? String
        sex = coder.decodeObject(forKey: "sex") as? Bool
        musicVolume = coder.decodeFloat(forKey: "musicVolume")
        voices = coder.decodeFloat(forKey: "voices")
        earVolume = coder.decodeFloat(forKey: "earVolume")
    }

    func encode(with coder: NSCoder) {
        coder.encode(uid, forKey: "uid")
        coder.encode(name, forKey: "name")
        coder.encode(head, forKey: "head")
        coder.encode(sex, forKey: "sex")
        coder.encode(musicVolume, forKey: "musicVolume")
        coder.encode(voices, forKey: "voices")
        coder.encode(earVolume, forKey: "earVolume")
    }
    
    override init() {
        super.init()
    }
}

let filePath = NSHomeDirectory() + "/Documents/contacts.data"

extension NSObject {
    
    func saveUserInformation(model: ARUserModel) {
        let data = NSMutableData()
        let archive = NSKeyedArchiver(forWritingWith: data)
        archive.encode(model, forKey: "userModel")
        archive.finishEncoding()
        data.write(toFile: filePath, atomically: true)
    }
    
    func getUserInformation() -> ARUserModel? {
        let fileData = NSMutableData(contentsOfFile: filePath)
        if FileManager.default.fileExists(atPath: filePath) {
            let unarchiver = NSKeyedUnarchiver(forReadingWith: fileData! as Data)
            let userModel = unarchiver.decodeObject(forKey: "userModel") as? ARUserModel
            unarchiver.finishDecoding()
            return userModel
        }
        return nil
    }
    
    func removeUserInformation() {
        if FileManager.default.fileExists(atPath: filePath) {
            do {
                try FileManager.default.removeItem(atPath:filePath)
                } catch {
                print(error)
            }
        }
    }
}

class ARChatUserModel: NSObject {
    //用户随机id
    var uid: String?
    //用户昵称
    var name: String? = ""
    //用户随机头像
    var head: String? = ""
    //true 女 false男
    var sex: Bool? = false
    //申请的麦位 --- 麦序
    var applyMic: String? = "1"
}



