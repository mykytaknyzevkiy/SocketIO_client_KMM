package com.nekzabirov.socketIO

import co.touchlab.kermit.Logger
import platform.Foundation.*
import platform.darwin.NSObject
import socket_IO.SocketIO
import socket_IO.SocketIODelegateProtocol
import socket_IO.SocketIOPacket

actual class SocketIO actual constructor(private val delegate: SocketDelegate) {
    companion object {
        private const val TAG = "SocketIO"
    }

    private var socketIO: SocketIO? = null

    private val socketDelegate = object : SocketIODelegateProtocol, NSObject() {
        override fun socketIODidConnect(socket: SocketIO?) {
            delegate.onConnect()
        }

        override fun socketIO(socket: SocketIO?, didReceiveMessage: SocketIOPacket?) {
            if (didReceiveMessage == null) {
                Logger.e(TAG, message = {
                    "Received message packet is null"
                })
                return
            }

            didReceiveMessage.packetData?.let {
                NSJSONSerialization.JSONObjectWithData(it, NSJSONReadingMutableContainers, error = null)
            }?.let {
                NSJSONSerialization.dataWithJSONObject(
                    it,
                    NSJSONWritingPrettyPrinted,
                    null
                )
            }?.let {
                NSString.create(data = it, NSUTF8StringEncoding)
            }?.let {
                delegate.onMessage(it.substringFromIndex(0))
            }
        }

        override fun socketIO(socket: SocketIO?, onError: NSError?) {
            delegate.onError(Exception(onError?.localizedDescription ?: "Unknown"))
        }
    }

    actual fun connect(endpoint: String, port: Int) {
        Logger.d(TAG, message = {
            "connect $endpoint:$port"
        })

        socketIO = null

        socketIO = SocketIO(socketDelegate).apply {
            useSecure = true
        }

        socketIO?.connectToHost(
            endpoint,
            port.toLong()
        )
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

        val data = NSString.create(string = payloadJson).dataUsingEncoding(
            NSUTF8StringEncoding
        )

        if (data == null) {
            Logger.e(TAG, message = {
                "Cannot convert json message to NSData"
            })
            return
        }

        val nsDir = NSJSONSerialization.JSONObjectWithData(data, NSJSONReadingMutableContainers, error = null)

        if (nsDir == null) {
            Logger.e(TAG, message = {
                "Cannot convert data to NSDIR via NSJSONSerialization"
            })
            return
        }

        socketIO?.sendEvent(
            name,
            nsDir
        )
    }
}