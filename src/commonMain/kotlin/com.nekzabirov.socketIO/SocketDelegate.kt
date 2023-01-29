package com.nekzabirov.socketIO

/**
 * SocketIO callback
 */
interface SocketDelegate {
    /**
     * Calls while socket client successful connected
     */
    fun onConnect()

    /**
     * Socket client disconnected without error
     */
    fun onDisconnect()

    /**
     * Socket client error
     */
    fun onError(error: Exception)

    /**
     * Receive message from socket
     */
    fun onMessage(body: String)
}