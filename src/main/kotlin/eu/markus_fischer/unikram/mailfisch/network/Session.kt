package eu.markus_fischer.unikram.mailfisch.network

import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket

class Session(var remote_address: String,
              var remote_port: Int)
{
    var socket : Socket? = null
    fun establish_connection(): Boolean {
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