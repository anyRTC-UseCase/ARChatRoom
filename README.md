## 效果

![image](https://github.com/anyRTC-UseCase/ARChatRoom/blob/master/demo.gif)

## 项目概述

ARChatRoom 是anyRTC模仿语音开黑的示例项目，演示了如何通过 anyRTC云服务，并配合 anyRTC RTC SDK、anyRTC RTM SDK，快速实现语音社交的场景。
- 快速上麦。
- 场控能力：房间上麦、闭麦、抱麦、踢人等功能。
- 音乐播放：播放背景音乐、音效，烘托气氛.
- 踢人功能。
- 禁言，禁麦，送礼物等功能。
- IM消息发送

## 代码下载
Github代码下载慢，请移步至[码云](https://gitee.com/anyRTC/archat-room)下载

### 支持场景

ARChatRoom 示例项目提供上麦，拒绝等一些列逻辑。客户可拿来即用。语音开黑，语音连麦等场景

ARChatRoom 提供IM实时消息，送礼物等，适用于直播场景，狼人杀等益智类场景。

### 功能列表

#### RTM-SDK功能

- 创建RTM实时消息引擎：[initWithAppId](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#initwithappid)
- 登录：[loginByToken](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#loginbytoken)
- 添加或更新本地用户的属性：[addOrUpdateLocalUserAttributes](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#addorupdatelocaluserattributes)
- 获取指定用户的全部属性：[getUserAllAttributes](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#getuserallattributes)
- 创建一个RTM频道：[createChannelWithId](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#createchannelwithid)
- 删除某指定频道的指定属性：[deleteChannelAttributesByKeys](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#deletechannelattributesbykeys)
- 查询某指定频道的全部属性：[getChannelAllAttributes](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#getchannelallattributes)
- 向指定用户发送点对点消息或点对点的离线消息：[sendMessage](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_kit#sendmessage)
- 发送频道消息：[sendMessage](https://docs.anyrtc.io/rtm-ios/docs/ios_rtm/ios_rtm_channel#sendmessage)

#### RTC-SDK功能

- 创建RTC音视频引擎：[sharedEngineWithAppId](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#sharedengineWithappId)
- 设置音频编码配置：[setAudioProfile](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#setaudioprofile)
- 调节录音音量：[adjustRecordingSignalVolume](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#adjustrecordingsignalvolume)
- 启用说话者音量提示：[enableAudioVolumeIndication](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#enableaudiovolumeindication)
- 开关本地音频发送：[muteLocalAudioStream](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#mutelocalaudiostream)
- 开启耳返功能：[enableInEarMonitoring](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#enableinearmonitoring)
- 打开/关闭扬声器：[setEnableSpeakerphone](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#setenablespeakerphone)
- 设置耳返音量：[setInEarMonitoringVolume](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#setinearmonitoringvolume)
- 开始播放音乐文件：[startAudioMixing](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#startaudiomixing)
- 停止播放音乐文件：[stopAudioMixing](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#stopaudiomixing)
- 暂停播放音乐文件：[pauseAudioMixing](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#pauseaudiomixing)
- 恢复播放音乐文件：[resumeAudioMixing](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#resumeaudiomixing)
- 调节音乐文件的播放音量：[adjustAudioMixingVolume](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#adjustaudiomixingvolume)
- 播放指定音效文件：[playEffect](https://docs.anyrtc.io/rtc-ios/docs/ios/ios_rtc_kit#playeffect)

功能展示为iOS接口，其他平台接口请前往[文档中心](https://docs.anyrtc.io/)。

### 平台兼容

ARChatRoom 示例项目支持以下平台和版本：

- iOS 9 及以上。
- Android 4.4 及以上。

## 快速开始

### 前提条件

在编译及运行 ARChatRoom 示例项目之前，你需要完成以下准备工作。

#### 获取App ID
通过以下步骤获取anyRTC App ID：
  1. 在anyRTC[控制台](https://console.anyrtc.io/signup)创建一个账号。
  2. 登录anyRTC控制台，创建一个项目。
  3. 前往**项目管理**页面，获取该项目的 App ID。

### 运行示例项目

参考以下文档在对应的平台编译及运行示例项目：

- [Android 运行指南](https://github.com/anyRTC-UseCase/ARChatRoom/tree/master/ARChatRoom-Android)
- [iOS 运行指南](https://github.com/anyRTC-UseCase/ARChatRoom/tree/master/%20ARChatRoom-iOS)

## 常见问题

详见[常见问题](https://docs.anyrtc.io/platforms/docs/platforms/FAQ/faq)。

## **anyRTC创业扶持计划**

- 30万免费分钟数，助力初创企业快速发展。

>  anyRTC初创企业扶持计划，只要通过企业审核，联系客服加入anyRTC创业扶持计划，即可享受30万免费分钟数。获得分钟数可降低在实时音视频技术服务所产生的成本费用，零成本快速启动项目。

- 专属技术指导支持

> anyRTC为初创企业提供一对一专属客服，为客户提供专业、认真的服务，及时解答您的疑惑。并为客户提供专属技术指导，更快上手，轻松上线！

### 联系我们

联系电话：021-65650071

QQ咨询群：580477436

ARCall技术交流群：597181019

咨询邮箱：hi@dync.cc

技术问题：[开发者论坛](https://bbs.anyrtc.io)

获取更多帮助前往：[www.anyrtc.io](http://www.anyrtc.io)

