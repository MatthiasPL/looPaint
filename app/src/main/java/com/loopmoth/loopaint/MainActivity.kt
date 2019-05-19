package com.loopmoth.loopaint

import android.content.pm.PackageManager
import android.graphics.*
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import kotlinx.android.synthetic.main.activity_main.*
import top.defaults.colorpicker.ColorPickerPopup
import java.nio.file.Files.exists
import android.os.Environment.getExternalStorageDirectory
//import sun.swing.SwingUtilities2.drawRect
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.lang.Exception
import java.sql.Timestamp


class MainActivity : AppCompatActivity() {

    private lateinit var paintView: PaintView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        paintView = findViewById(R.id.paintView)

        val icon = ImageView(this) // Create an icon
        icon.setImageResource( R.drawable.ic_menu )
        icon.maxWidth = 30
        icon.maxHeight = 30

        val itemBuilder = SubActionButton.Builder(this)
        val itemIcon = ImageView(this)
        itemIcon.setImageResource( R.drawable.ic_prev  )
        itemIcon.maxHeight=20
        itemIcon.maxWidth=20
        val button1 = itemBuilder.setContentView(itemIcon).build()

        val itemBuilder2 = SubActionButton.Builder(this)
        val itemIcon2 = ImageView(this)
        itemIcon2.setImageResource( R.drawable.ic_next  )
        val button2 = itemBuilder2.setContentView(itemIcon2).build()

        val itemBuilder3 = SubActionButton.Builder(this)
        val itemIcon3 = ImageView(this)
        itemIcon3.setImageResource( R.drawable.ic_color_wheel  )
        val button3 = itemBuilder3.setContentView(itemIcon3).build()

        val itemBuilder4 = SubActionButton.Builder(this)
        val itemIcon4 = ImageView(this)
        itemIcon4.setImageResource( R.drawable.ic_artistic_brush  )
        val button4 = itemBuilder4.setContentView(itemIcon4).build()

        val itemBuilder5 = SubActionButton.Builder(this)
        val itemIcon5 = ImageView(this)
        itemIcon5.setImageResource( R.drawable.ic_mountain_range_on_black_background )
        val button5 = itemBuilder5.setContentView(itemIcon5).build()

        val itemBuilder6 = SubActionButton.Builder(this)
        val itemIcon6 = ImageView(this)
        itemIcon6.setImageResource( R.drawable.ic_save )
        val button6 = itemBuilder6.setContentView(itemIcon6).build()

        val itemBuilder8 = SubActionButton.Builder(this)
        val itemIcon8 = ImageView(this)
        itemIcon8.setImageResource( R.drawable.ic_eraser )
        val button8 = itemBuilder8.setContentView(itemIcon8).build()

        val itemBuilder7 = SubActionButton.Builder(this)
        val itemIcon7 = ImageView(this)
        itemIcon7.setImageResource( R.drawable.ic_clear_button )
        val button7 = itemBuilder7.setContentView(itemIcon7).build()

        val actionMenu = FloatingActionMenu.Builder(this)
            .addSubActionView(button1)
            .addSubActionView(button2)
            .addSubActionView(button3)
            .addSubActionView(button5)
            .addSubActionView(button4)
            .addSubActionView(button8)
            .addSubActionView(button6)
            .addSubActionView(button7)
            .setStartAngle(90)
            .setEndAngle(-90)
            .attachTo(actionButton)
            .build()

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView.init(metrics)

        button1.setOnClickListener{
            paintView.removeLastStroke()
        }

        button2.setOnClickListener {
            paintView.reviveLastStroke()
        }

        button3.setOnClickListener {
            //paintView.currentColor
            ColorPickerPopup.Builder(this)
                .initialColor(paintView.currentColor) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("OK")
                .cancelTitle("Anuluj")
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(paintView, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        paintView.currentColor = color
                        //paintView.setBackgroundColor(color)
                    }

                    fun onColor(color: Int, fromUser: Boolean) {

                    }
                })
        }

        button4.setOnClickListener {
            if (slider.visibility == View.VISIBLE) {
                YoYo.with(Techniques.FadeOut)
                    .duration(700)
                    .onEnd { slider.visibility = View.INVISIBLE }
                    .playOn(findViewById(R.id.slider))

            } else {
                YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .onStart { slider.visibility = View.VISIBLE }
                    .playOn(findViewById(R.id.slider))
            }
        }

        button5.setOnClickListener {
            ColorPickerPopup.Builder(this)
                .initialColor(paintView.bgColor) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(false) // Enable alpha slider or not
                .okTitle("OK")
                .cancelTitle("Anuluj")
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(paintView, object : ColorPickerPopup.ColorPickerObserver() {
                    override fun onColorPicked(color: Int) {
                        paintView.bgColor = color
                        //paintView.changeBackground()
                        paintView.setBackgroundColor(color)
                        //paintView.setBackgroundColor(color)
                    }

                    fun onColor(color: Int, fromUser: Boolean) {

                    }
                })
        }

        button6.setOnClickListener {
            paintView.saveAsImage(Timestamp(System.currentTimeMillis()).toString(), this@MainActivity)
            Toast.makeText(this@MainActivity, "Saving as " + Timestamp(System.currentTimeMillis()).toString(), Toast.LENGTH_LONG).show()
        }

        button7.setOnClickListener {
            paintView.clear()
            actionMenu.close(true)
            slider.visibility = View.INVISIBLE
        }

        button8.setOnClickListener {
            paintView.currentColor = paintView.bgColor
        }

        slider.setOnPositionChangeListener { view, fromUser, oldPos, newPos, oldValue, newValue ->
            paintView.strokeWidth=slider.value
            //actionMenu.close(true)
        }

        paintView.setOnClickListener {
            //Toast.makeText(this@MainActivity, "test", Toast.LENGTH_SHORT).show()
            actionMenu.close(true)
            slider.visibility = View.INVISIBLE
        }
    }

}
