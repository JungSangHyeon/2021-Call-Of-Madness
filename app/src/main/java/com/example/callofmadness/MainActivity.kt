package com.example.callofmadness

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.callofmadness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        checkPermission()
    }
    private fun initView() {
        binding.numberSelectSeekBar.setOnSeekBarChangeListener(NumberBarChangeListener())
        binding.callButton.setOnClickListener {startCall()}
        updateNumber(binding.numberSelectSeekBar.progress)
    }

    private fun startCall() {
        val callUri = Uri.parse("tel:010" + String.format("%08d", binding.numberSelectSeekBar.progress))
        val callIntent = Intent(Intent.ACTION_CALL, callUri)
        startActivity(callIntent)
    }
    inner class NumberBarChangeListener : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {updateNumber(progress)}
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    @SuppressLint("SetTextI18n")
    private fun updateNumber(progress: Int) {
        binding.nowSelectedNumberTextView.text = "010-"+String.format("%04d", progress/10000)+"-"+String.format("%04d", progress%10000);
    }


    private fun checkPermission(){
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf( Manifest.permission.CALL_PHONE), 0 )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED){
            showRequestPermissionDialog()
        }
    }
    private fun showRequestPermissionDialog(){
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("알림")
            .setMessage("권한이 없으면 사용할 수 없습니다. 권한을 설정해주세요.")
            .setCancelable(false)
            .setPositiveButton("예") { _, _ -> startPermissionSettingActivity()}
            .setNegativeButton("아니오") { _, _ -> finish()}
        builder.create().show()
    }
    private fun startPermissionSettingActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.parse("package:$packageName"))
        startActivity(intent)
    }
}