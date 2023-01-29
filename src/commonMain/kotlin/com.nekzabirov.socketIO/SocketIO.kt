package com.nekzabirov.socketIO

expect class SocketIO(delegate: SocketDelegate) {
    /**
     * @param endpoint host without schemes
     * @param port connection post. basic is 9001
     */
    fun connect(endpoint: String, port: Int)

    /**
     * Immodestly disconnect own client from scoot
     */
    fun disconnect()

    /**
     * @param name is event name
     * @param payloadJson is json body as text
     */
    fun sendEvent(name: String, payloadJson: String)
}