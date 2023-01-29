package com.nekzabirov.socketIO

import co.touchlab.kermit.Logger
import io.socket.IOAcknowledge
import io.socket.IOCallback
import io.socket.SocketIO
import io.socket.SocketIOException
import org.json.JSONObject
import javax.net.ssl.SSLContext

actual class SocketIO actual constructor(private val delegate: SocketDelegate) {
    companion object {
        private const val TAG = "SocketIO"
    }

    private val ioCallback = object : IOCallback {
        override fun onDisconnect() {
            delegate.onDisconnect()
        }

        override fun onConnect() {
            delegate.onConnect()
        }

        override fun onMessage(p0: String?, p1: IOAcknowledge?) {
            delegate.onMessage(p0 ?: return)
        }

        override fun onMessage(p0: JSONObject?, p1: IOAcknowledge?) {
            delegate.onMessage(p0?.toString() ?: return)
        }

        override fun on(p0: String?, p1: IOAcknowledge?, vararg p2: Any?) {}

        override fun onError(p0: SocketIOException?) {
            delegate.onError(p0 ?: Exception("Unknown error"))
        }
    }

    private var socketIO: SocketIO? = null

    actual fun connect(endpoint: String, port: Int) {
        Logger.d(TAG, message = {
            "connect $endpoint:$port"
        })

        SocketIO.setDefaultSSLSocketFactory(SSLContext.getDefault())

        socketIO = try {
            SocketIO("https://$endpoint:$port")
        } catch (e: Exception) {
            delegate.onError(e)
            return
        }

        socketIO?.connect(ioCallback)
    }

    actual fun disconnect() {
        Logger.d(TAG, message = {
            "disconnect"
        })

        socketIO?.disconnect()
    }

    actual fun sendEvent(name: String, payloadJson: String) {
        Logger.d(TAG, message = {
            "sendEvent $name $payloadJson"
        })

        socketIO?.emit(name, JSONObject(payloadJson))
    }
}