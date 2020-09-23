package org.ar.audioganme.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import org.ar.audioganme.R
import org.ar.audioganme.manager.ChatRoomManager
import org.ar.audioganme.weight.XRadioGroup


class GiftDialog :DialogFragment() {

    private lateinit var btnSend : Button
    private lateinit var xRadioGroup: XRadioGroup
    private var giftId:Int = 0
    private var sendUserId:String =""
    private lateinit var listener:onGiftSendListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
        return inflater.inflate(R.layout.dialog_gift, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSend = view.findViewById(R.id.btn_send)
        xRadioGroup = view.findViewById(R.id.rg_group)
        xRadioGroup.check(0)
        xRadioGroup.setOnCheckedChangeListener { _, checkedId ->

            when(checkedId){
                R.id.rb_a->giftId=0
                R.id.rb_b->giftId=1
                R.id.rb_c->giftId=2
                R.id.rb_d->giftId=3
                R.id.rb_e->giftId=4
                R.id.rb_f->giftId=5
                R.id.rb_g->giftId=6
                R.id.rb_h->giftId=7
            }

        }
        btnSend.setOnClickListener {
            ChatRoomManager.instance(activity).sendGift(sendUserId,giftId)
            if (listener!=null){
                listener.send(sendUserId,giftId)
            }
            dismiss()
        }
    }



    override fun onStart() {
        super.onStart()
        val win = dialog!!.window
        win!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        val params = win!!.attributes
        params.gravity = Gravity.BOTTOM
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        win!!.attributes = params
    }

    fun showDialog(userId:String,manager: FragmentManager,event:onGiftSendListener ){
        sendUserId = userId
        listener=event
        show(manager,"")
    }

    interface onGiftSendListener{
        fun send(userId:String,giftId:Int)
    }

}