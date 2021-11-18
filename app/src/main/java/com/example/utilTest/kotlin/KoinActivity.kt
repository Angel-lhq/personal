package com.example.utilTest.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.utilTest.R
import com.example.utilTest.beans.Person
import com.example.utilTest.interface_.TestInterface
import com.example.utilTest.interface_.TestInterface.a
import com.example.utilTest.utils.DialogManager
import com.example.utilTest.utils.DialogManager.CUSTOMDIALOG1
import com.example.utilTest.utils.Log
import com.example.utilTest.views.CustomDialog1
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.experimental.property.inject
import kotlin.math.log

class KoinActivity : AppCompatActivity(),TestInterface {
    val person1 : Person by inject()
    val person2 by inject<Person>()
    val ktUtil : KtUtil by inject()

    var textView:TextView? = null
    var button:Button? = null
    var imageView:ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_koin)
        val person3 = get<Person>()

        textView = findViewById(R.id.tv_text)
        button = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)

        textView?.setOnClickListener {
            TODO("Not yet implemented")
        }

        person1.city = "北京"
        person2.city = "河北"
        person3.city = "深圳"

        Log.i("KoinActivity","person1.city = " + person1.city + ",person2.city = " + person2.city + ",person3.city = " + person3.city)

        textView?.text = "person1.city = " + person1.city + ",person2.city = " + person2.city + ",person3.city = " + person3.city

        ktUtil.main()
        println(ktUtil.isEquals2)
        Log.i("utiltest","${ktUtil.isEquals2}")
        Log.i("utiltest","person1.city = ${person1.city} ,person2.city = ${person2.city} ,person3.city = ${person3.city}")

        button?.setOnClickListener{
            if (imageView?.isSelected!!){
                imageView!!.setImageResource(R.drawable.ic_baseline_add_circle_24)
                imageView?.isSelected = false
            }else{
                imageView!!.setImageResource(R.drawable.ic_baseline_add_circle_outline_24)
                imageView?.isSelected = true
            }
        }
        button!!.setOnLongClickListener{
            var dialog = DialogManager.getInstance().create(this,CUSTOMDIALOG1) as CustomDialog1
            dialog.setTitle("ssssssss")
            dialog.setCancelable(true)
            dialog.setOnCancelListener{

            }
            dialog.setNoOnclickListener("取消"){
                dialog.dismiss()
                Log.d(TAG, "cancel$it")
            }
            dialog.setYesOnclickListener("确定") { Log.d(TAG, "ssssss$it") }
            dialog.show()
            false
        }

    }

    override fun setA(value: Int) {

    }

    abstract class AbstractClass(){
        abstract fun abstractMethod()
    }

    data class DataClass(val name: String,val address: String){

    }

    companion object {
        private const val TAG = "KoinActivity"
    }


    interface inter{
        var sss:String
        val ss:String
        fun sssss(): Unit {
            ss.length
            sss.length

        }
    }

}

private fun Any.inject(ss: String): Boolean {
    TODO("Not yet implemented")
}
