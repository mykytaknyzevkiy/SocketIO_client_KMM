package com.nekzabirov.socketIO

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class SocketConnection {
    companion object {
        private const val ENDPOINT = "pace-push.syncwise360.com"
        private const val PORT = 9001
    }

    @Test
    fun textFlow() = runBlocking(Dispatchers.Default) {
        val message = socketIOFlow(
            endpoint = ENDPOINT,
            port = PORT
        ).first()

        assertEquals(message, "connectionState: true")

        val delegate = object : SocketDelegate {
            override fun onConnect() {
                TODO("Not yet implemented")
            }

            override fun onDisconnect() {
                TODO("Not yet implemented")
            }

            override fun onError(error: Exception) {
                TODO("Not yet implemented")
            }

            override fun onMessage(body: String) {
                TODO("Not yet implemented")
            }
        }

        val endpoint = "localhost"
        val port = 9001

        val socketIO = SocketIO(delegate = delegate)

        /**
         * Connect
         * @param endpoint host without schemes
         * @param port connection post. basic is 9001
         */
        socketIO.connect(endpoint = endpoint, port = port)

        socketIO.sendEvent(name = "test", "{" +
                    "'name': 0" +
                "}")
    }

}