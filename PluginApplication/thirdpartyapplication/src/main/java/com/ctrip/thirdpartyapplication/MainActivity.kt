package com.ctrip.thirdpartyapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * 这里不能直接使用findViewById，因为该方法需要一个上下文，
         * 而thirdPartyApp是插件，是没有被安装的，是没有上下文的
         * 所以，需要重新findViewById，让宿主app来实现
         */
        var img = findViewById<ImageView>(R.id.img)
        img.setOnClickListener({
            Toast.makeText(that, "点击啦", Toast.LENGTH_SHORT).show()
        })
    }
}
