package eu.markus_fischer.unikram.mailfisch.network

import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

open class Session(var remote_address: String,
              var remote_port: Int)
{
    var socket : Socket? = null
    open fun establish_connection(): Boolean {
        try {
            socket = Socket(remote_address, remote_port)
        } catch (e : Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun close_connection() = socket?.close()
}

class SSLSession(remote_address: String,
                 remote_port: Int) : Session(remote_address, remote_port) {

    override fun establish_connection() : Boolean {
        try {
            socket = SSLSocketFactory.getDefault().createSocket(remote_address, remote_port)
            (socket as SSLSocket).startHandshake()
        } catch (e : Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}