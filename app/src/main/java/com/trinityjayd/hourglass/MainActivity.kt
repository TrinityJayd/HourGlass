package com.trinityjayd.hourglass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.trinityjayd.hourglass.ui.theme.HourGlassTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the activity's layout using the XML layout file
        setContentView(R.layout.activity_main);

        //get login button
        val loginButton = findViewById<Button>(R.id.loginButton)
        //set on click listener
        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        //get register button
        val registerButton = findViewById<Button>(R.id.signupButton)
        //set on click listener
        registerButton.setOnClickListener {
            //go to register activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
