package com.hfad.playlistmaker.features.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.settings.domain.SetTheme

const val THEME_PREFERENCES = "theme_preferences"
const val THEME_KEY = "set_theme"

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initUI()

        //Обработка нажатия на кнопку "Поделиться приложением"
        val btnShare = findViewById<ImageView>(R.id.iv_share)
        btnShare.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link))
            sendIntent.type = getString(R.string.send_intent_type)
            startActivity(sendIntent)
        }
        //Обработка нажатия на кнопку "Написать в поддержку"
        val btnSupport = findViewById<ImageView>(R.id.iv_support)
        btnSupport.setOnClickListener {
            val subject = getString(R.string.mail_subject)
            val message = getString(R.string.mail_message)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse(getString(R.string.uri_parse))
            supportIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        //Обработка нажатия на кнопку "Написать в поддержку"
        val btnUserAgreement = findViewById<ImageView>(R.id.iv_user_agreement)
        btnUserAgreement.setOnClickListener {
            val url = getString(R.string.offer)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.setData(Uri.parse(url))
            startActivity(agreementIntent)
        }

        //Обработка нажатия на кнопку "Назад"
        val btnBack = findViewById<ImageView>(R.id.settings_arrow_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    private fun initUI() {
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val settingsTheme = SetTheme()
        themeSwitcher.isChecked = sharedPrefs.getBoolean(THEME_KEY, false)
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsTheme.switchTheme(isChecked)
            sharedPrefs.edit()
                .putBoolean(THEME_KEY, isChecked)
                .apply()
        }
    }

}

