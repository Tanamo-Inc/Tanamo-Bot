package com.tanamo.mybot.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType
import com.ibm.watson.developer_cloud.conversation.v1.Conversation
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice
import com.tanamo.mybot.R
import com.tanamo.mybot.adapter.Chato
import com.tanamo.mybot.model.Clicker
import com.tanamo.mybot.model.Kons.RECORD_REQUEST_CODE
import com.tanamo.mybot.model.Kons.REQUEST_RECORD_AUDIO_PERMISSION
import com.tanamo.mybot.model.Kons.TAGG
import com.tanamo.mybot.model.Kons.cPassword
import com.tanamo.mybot.model.Kons.cUsername
import com.tanamo.mybot.model.Kons.connect
import com.tanamo.mybot.model.Kons.initToast
import com.tanamo.mybot.model.Kons.sttPassword
import com.tanamo.mybot.model.Kons.sttUsername
import com.tanamo.mybot.model.Kons.ttsPassword
import com.tanamo.mybot.model.Kons.ttsUsername
import com.tanamo.mybot.model.Kons.workSpaceId
import com.tanamo.mybot.model.Messo
import com.tanamo.mybot.model.Toucher
import kotlinx.android.synthetic.main.chat_main.*
import kotlinx.android.synthetic.main.content_chat.*
import java.util.*

//todo : Add to my Messaging App.

class Chat : AppCompatActivity() {

    private lateinit var adapter: Chato
    private lateinit var lise: ArrayList<Messo>
    private var wContext: com.ibm.watson.developer_cloud.conversation.v1.model.Context? = null
    private lateinit var streamPlayer: StreamPlayer
    private var initialRequest: Boolean = false
    private var permissionToRecordAccepted = false
    private var listening = false
    private var speechService: SpeechToText? = null
    private var textToSpeech: TextToSpeech? = null
    private var capture: MicrophoneInputStream? = null
    private var mContext: Context? = null
    private var microphoneHelper: MicrophoneHelper? = null


    private val recognizeOptions: RecognizeOptions
        get() = RecognizeOptions.Builder()
                .contentType(ContentType.OPUS.toString())
                .interimResults(true)
                .inactivityTimeout(2000)
                .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_main)

        init()

    }

    private fun init() {

        toolbar.title = ""

        setSupportActionBar(toolbar)

        config()

        lise = ArrayList()
        adapter = Chato(lise)
        microphoneHelper = MicrophoneHelper(this)

        val lay = LinearLayoutManager(this)
        lay.stackFromEnd = true
        recycler_view.layoutManager = lay
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = adapter
        this.message.setText("")
        this.initialRequest = true
        sendMessage()


        //Watson Text-to-Speech Service on Bluemix
        textToSpeech = TextToSpeech()
        textToSpeech!!.setUsernameAndPassword(ttsUsername, ttsPassword)


        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAGG, "Permission to record denied")
            makeRequest()
        }


        recycler_view.addOnItemTouchListener(Toucher(applicationContext, recycler_view, object : Clicker {
            override fun onClick(view: View, position: Int) {
                val thread = Thread(Runnable {
                    val audioMessage: Messo?
                    try {

                        audioMessage = lise[position]
                        streamPlayer = StreamPlayer()
                        if (!audioMessage.message.isEmpty())
                        //Change the Voice format and choose from the available choices
                            streamPlayer.playStream(textToSpeech!!.synthesize(audioMessage.message, Voice.EN_LISA).execute())
                        else
                            streamPlayer.playStream(textToSpeech!!.synthesize("No Text Specified", Voice.EN_LISA).execute())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                thread.start()
            }

            override fun onLongClick(view: View, position: Int) {
                recordMessage()

            }
        }))

        btn_send.setOnClickListener {
            if (connect(this@Chat)) {
                sendMessage()
            }
        }

        ///    btn_record.setOnClickListener { recordMessage() }


    }

    private fun config() {

        mContext = applicationContext
        cUsername = mContext!!.getString(R.string.conversation_username)
        cPassword = mContext!!.getString(R.string.conversation_password)
        workSpaceId = mContext!!.getString(R.string.workSpaceId)
        sttUsername = mContext!!.getString(R.string.STT_username)
        sttPassword = mContext!!.getString(R.string.STT_password)
        ttsUsername = mContext!!.getString(R.string.TTS_username)
        ttsPassword = mContext!!.getString(R.string.TTS_password)

    }

    // Speech-to-Text Record Audio permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAGG, "Permission has been denied by user")
                } else {
                    Log.i(TAGG, "Permission has been granted by user")
                }
                return
            }

            MicrophoneHelper.REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), MicrophoneHelper.REQUEST_PERMISSION)
    }

    // Sending a message to Watson Conversation Service
    private fun sendMessage() {
        val inputs = this.message!!.text.toString().trim { it <= ' ' }

        if (!this.initialRequest) {
            val messo = Messo()
            messo.message = inputs
            messo.id = "50"
            lise.add(messo)
        } else {
            val messo = Messo()
            messo.message = inputs
            messo.id = "60"
            this.initialRequest = false
            initToast(applicationContext, "Tap on the message for Voice")
        }

        this.message!!.setText("")
        adapter.notifyDataSetChanged()

        val thread = Thread(Runnable {

            try {
                val service = Conversation(Conversation.VERSION_DATE_2017_05_26)
                service.setUsernameAndPassword(cUsername, cPassword)
                val input = InputData.Builder(inputs).build()
                val options = MessageOptions.Builder(workSpaceId).input(input).context(wContext).build()
                val response = service.message(options).execute()

                //Passing Context of last conversation
                if (response!!.context != null) {

                    wContext = response.context

                }
                val outMessage = Messo()

                if (response.output != null && response.output.containsKey("text")) {

                    val responseList = response.output["text"] as ArrayList<*>
                    if (responseList.size > 0) {
                        outMessage.message = responseList[0] as String
                        outMessage.id = "2"
                    }
                    lise.add(outMessage)
                    val thread = Thread(Runnable {
                        val audioMessage: Messo?
                        try {

                            audioMessage = outMessage
                            streamPlayer = StreamPlayer()
                            if (!audioMessage.message.isEmpty())
                            //Change the Voice format and choose from the available choices
                                streamPlayer.playStream(textToSpeech!!.synthesize(audioMessage.message, Voice.EN_LISA).execute())
                            else
                                streamPlayer.playStream(textToSpeech!!.synthesize("No Text Specified", Voice.EN_LISA).execute())

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
                    thread.start()
                }

                runOnUiThread {
                    adapter.notifyDataSetChanged()
                    if (adapter.itemCount > 1) {
                        recycler_view!!.layoutManager.smoothScrollToPosition(recycler_view, null, adapter.itemCount - 1)

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()

    }

    //Record a message via Watson Speech to Text
    private fun recordMessage() {
        speechService = SpeechToText()
        speechService!!.setUsernameAndPassword(sttUsername, sttPassword)


        if (!listening) {
            capture = microphoneHelper!!.getInputStream(true)
            Thread(Runnable {
                try {
                    speechService!!.recognizeUsingWebSocket(capture!!, recognizeOptions, MicrophoneRecognizeDelegate())
                } catch (e: Exception) {
                    showError(e)
                }
            }).start()
            listening = true
            initToast(this@Chat, "Listening....Click to Stop")

        } else {
            try {
                microphoneHelper!!.closeInputStream()
                listening = false
                initToast(this@Chat, "Stopped Listening....Click to Start")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    //Watson Speech to Text Methods.
    private inner class MicrophoneRecognizeDelegate : RecognizeCallback {

        override fun onTranscription(speechResults: SpeechResults) {

            if (speechResults.results != null && !speechResults.results.isEmpty()) {
                val text = speechResults.results[0].alternatives[0].transcript
                showMicText(text)
            }
        }

        override fun onConnected() {

        }

        override fun onError(e: Exception) {
            showError(e)

        }

        override fun onDisconnected() {
            // enableMicButton()
        }

        override fun onInactivityTimeout(runtimeException: RuntimeException) {

        }

        override fun onListening() {

        }

        override fun onTranscriptionComplete() {

        }

    }

    private fun showMicText(text: String) {
        runOnUiThread { message.setText(text) }
    }


    private fun showError(e: Exception) {
        runOnUiThread {
            Toast.makeText(this@Chat, e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAGG, "onCreateOptionsMenu: ")
        menuInflater.inflate(R.menu.start, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAGG, "onOptionsItemSelected: ")
        val id = item.itemId

        if (id == R.id.record) {
            recordMessage()
            return true
        }


        return super.onOptionsItemSelected(item)
    }


}



