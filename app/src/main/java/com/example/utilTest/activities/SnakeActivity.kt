package com.example.utilTest.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.game.Direction
import com.example.game.OnEatListener
import com.example.game.Over
import com.example.utilTest.R
import com.example.utilTest.utils.DialogManager
import com.example.utilTest.views.CustomDialog1
import kotlinx.android.synthetic.main.activity_snake.*

class SnakeActivity : AppCompatActivity(), OnEatListener,Over {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake)
        initView()
    }

    private fun initView() {
        game.onEatListener = this
        game.over = this
    }

    fun move(view:View){
        when(view.id){
            R.id.btn_up -> game.setMove(Direction.UP)
            R.id.btn_left -> game.setMove(Direction.LEFT)
            R.id.btn_right -> game.setMove(Direction.RIGHT)
            R.id.btn_down -> game.setMove(Direction.DOWN)
        }
    }

    fun start(view: View) {
        game.start()
        rl.visibility = View.GONE
        cl.visibility = View.VISIBLE
    }

    override fun onEatListener(eatCount: Int) {
        runOnUiThread {
            tv_sorce.text = eatCount.toString()
        }
    }

    override fun gameover() {
        runOnUiThread {
            var dialog = DialogManager.getInstance().create(this,
                DialogManager.CUSTOMDIALOG1
            ) as CustomDialog1
            dialog.setTitle("游戏结束")
            dialog.setMessage("分数${tv_sorce.text}")
            dialog.setYesOnclickListener("重新开始") {
                game.resetGame()
                dialog.dismiss()
                game.reStart()
            }
            dialog.setNoOnclickListener("退出"){
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }
    }
}