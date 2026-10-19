package com.example.aiintellegens

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    // DEINE DEPLOYED RENDER-URL UND SEKRÄTER APP-SCHLÜSSEL
    private val SERVER_URL = "https://ai-intellegenss.onrender.com/generate"
    private val APP_SECRET_TOKEN = "MeinSuperGeheimerSchluessel123!" // Muss exakt mit Render übereinstimmen!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputPrompt = findViewById<EditText>(R.id.inputPrompt)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val responseText = findViewById<TextView>(R.id.responseText)

        sendButton.setOnClickListener {
            val prompt = inputPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                responseText.text = "Lade Antwort..."
                sendPromptToServer(prompt) { result ->
                    runOnUiThread {
                        responseText.text = result
                    }
                }
            } else {
                inputPrompt.error = "Bitte erst einen Text eingeben"
            }
        }
    }

    private fun sendPromptToServer(prompt: String, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(SERVER_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                
                // Der geheime Schlüssel wird im Header mitgeschickt
                conn.setRequestProperty("x-app-token", APP_SECRET_TOKEN)
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                jsonParam.put("prompt", prompt)

                val os = OutputStreamWriter(conn.outputStream, "UTF-8")
                os.write(jsonParam.toString())
                os.flush()
                os.close()

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseString)
                    val textResult = jsonResponse.optString("text", "Keine Antwort erhalten")
                    withContext(Dispatchers.Main) { callback(textResult) }
                } else {
                    val errorStream = conn.errorStream?.bufferedReader()?.use { it.readText() }
                    withContext(Dispatchers.Main) { 
                        callback("Fehler vom Server ($responseCode): ${errorStream ?: "Unbekannter Fehler"}") 
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback("Verbindungsfehler: ${e.localizedMessage}")
                }
            }
        }
    }
}
