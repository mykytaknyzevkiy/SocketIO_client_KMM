package com.nekzabirov.socketIO

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * SocketClient with flows as callback
 * @param endpoint host without schemes
 * @param port connection post. basic is 9001
 */
class SocketIOFlow(
    private val endpoint: String,
    private val port: Int
) : SocketDelegate {
    /**
     * state flow of connection states.
     * true is successful connected
     * false is client isn't connected or successful disconnected
     */
    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean>
        get() = _connectionState

    /**
     * Received message from socket as json text
     */
    private val _onMessage = MutableSharedFlow<String>()
    val onMessage: Flow<String>
        get() = _onMessage
            .onStart {
                socketIO.connect(endpoint, port)
            }
            .onCompletion {
                socketIO.disconnect()
            }

    /**
     * error callback
     */
    private val _onError = MutableSharedFlow<Exception>()
    val onError: Flow<Exception>
        get() = _onError

    private val socketIO = SocketIO(this)

    override fun onConnect() {
        _connectionState.tryEmit(true)
    }

    override fun onDisconnect() {
        _connectionState.tryEmit(false)
    }

    override fun onError(error: Exception) {
        _onError.tryEmit(error)
    }

    override fun onMessage(body: String) {
        _onMessage.tryEmit(body)
    }

    /**
     * @param name is event name
     * @param payloadJson is json body as text
     */
    fun sendEvent(name: String, payload: String) {
        socketIO.sendEvent(name, payload)
    }
}

/**
 * SocketClient as flow only received message without send event
 * @param endpoint host without schemes
 * @param port connection post. basic is 9001
 */
fun socketIOFlow(endpoint: String, port: Int) = channelFlow {
    val socketIO = SocketIO(object : SocketDelegate {
        override fun onConnect() {
            this@channelFlow.trySend("connectionState: true")
        }

        override fun onDisconnect() {
            this@channelFlow.trySend("connectionState: false")
        }

        override fun onError(error: Exception) {
            this@channelFlow.trySend("error: ${error.message}")
        }

        override fun onMessage(body: String) {
            this@channelFlow.trySend(body)
        }

    })

    socketIO.connect(endpoint, port)

    awaitClose()

    socketIO.disconnect()
}