package org.ar.audioganme.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.ar.audioganme.R
import org.ar.audioganme.adapter.OnlineMemberAdapter
import org.ar.audioganme.manager.ChatRoomManager
import org.ar.audioganme.model.MemberListBean
import org.ar.rtm.ErrorInfo
import org.ar.rtm.ResultCallback
import org.ar.rtm.RtmAttribute
import org.ar.rtm.RtmChannelMember
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MemberDialog : DialogFragment(){

    private val mainScope = MainScope()
    private lateinit var rvList:RecyclerView
    private lateinit var tvOk:TextView
    private lateinit var memberAdapter:OnlineMemberAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_members,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvList = view.findViewById(R.id.rv_list)
        tvOk = view.findViewById(R.id.btn_ok)
        tvOk.setOnClickListener{
            dismiss()
        }
        memberAdapter = OnlineMemberAdapter()
        rvList.adapter= memberAdapter
        mainScope.launch {
            val listMember = ArrayList<MemberListBean>()
            val member=getMembers()
            member.forEach{
                val userAttrList = getUserAttr(it.userId)
                val map = HashMap<String,String>()
                userAttrList.forEach{ it ->
                    map[it.key] = it.value
                }
                listMember.add(MemberListBean(map["uid"].toString(),map["head"].toString(),map["name"].toString(),map["sex"]!!.toInt()))
            }
            memberAdapter.addData(listMember)
        }

    }

    private suspend fun getMembers() = suspendCoroutine<List<RtmChannelMember>> {
        continuation ->
        ChatRoomManager.instance(activity).channel.getMembers(object : ResultCallback<List<RtmChannelMember>> {
            override fun onSuccess(var1: List<RtmChannelMember>?) {
                continuation.resume(var1 as ArrayList)
            }

            override fun onFailure(var1: ErrorInfo?) {
            }

        })
    }

    private suspend fun getUserAttr(userId:String) = suspendCoroutine<List<RtmAttribute>> {
        continuation ->
        ChatRoomManager.instance(activity).rtmManager.getmRtmClient().getUserAttributes(userId,object :ResultCallback<List<RtmAttribute>>{
            override fun onSuccess(var1: List<RtmAttribute>?) {
                continuation.resume(var1 as ArrayList)
            }

            override fun onFailure(var1: ErrorInfo?) {
            }

        })

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mainScope.cancel()
    }

}


