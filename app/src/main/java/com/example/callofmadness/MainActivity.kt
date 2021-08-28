package com.example.callofmadness

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callofmadness.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.numberSelectSeekBar.setOnSeekBarChangeListener(NumberBarChangeListener())
        binding.callButton.setOnClickListener(CallButtonClickListener())

        var initNumber = 88128140;
        updateNumber(initNumber)
        binding.numberSelectSeekBar.progress = initNumber

        requestPermission();
    }

    inner class CallButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:010" + String.format("%08d", binding.numberSelectSeekBar.progress))));
        }
    }
    inner class NumberBarChangeListener : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {updateNumber(progress)}
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private fun updateNumber(progress: Int) {
        binding.nowSelectedNumberTextView.text = "010-"+String.format("%04d", progress/10000)+"-"+String.format("%04d", progress%10000);
    }


    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.CALL_PHONE)

    private fun requestPermission(){
        var permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSIONS_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    showRequestPermissionDialog();
                }else{
                }
            }
        }
    }

    private fun showRequestPermissionDialog(){
        var builder = AlertDialog.Builder(this)
        builder
            .setTitle("알림")
            .setMessage("권한이 없으면 사용할 수 없습니다. 권한을 설정해주세요.")
            .setCancelable(false)
            .setPositiveButton("예") {dialog, which -> startPermissionSettingActivity()}
            .setNegativeButton("아니오") {dialog, which -> endApplication()}
        builder.create().show()
    }

    private fun endApplication() {
        finish()
    }

    private fun startPermissionSettingActivity() {
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData( Uri.parse("package:" + getPackageName()))
        startActivity(intent);
    }

}