package com.movtery.zalithlauncher.game.localserver

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Klien RCON (protokol Source RCON) minimal untuk mengirim perintah ke server lokal */
object RconClient {
    private const val TYPE_AUTH = 3
    private const val TYPE_COMMAND = 2

    suspend fun exec(port: Int, password: String, command: String): String = withContext(Dispatchers.IO) {
        Socket().use { socket ->
            socket.connect(InetSocketAddress("127.0.0.1", port), 3000)
            socket.soTimeout = 5000
            val out = socket.getOutputStream()
            val input = DataInputStream(socket.getInputStream())

            fun send(id: Int, type: Int, body: String) {
                val b = body.toByteArray(Charsets.UTF_8)
                val buf = ByteBuffer.allocate(14 + b.size).order(ByteOrder.LITTLE_ENDIAN)
                buf.putInt(10 + b.size).putInt(id).putInt(type).put(b).put(0).put(0)
                out.write(buf.array())
                out.flush()
            }

            fun read(): Pair<Int, String> {
                val lenBytes = ByteArray(4)
                input.readFully(lenBytes)
                val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.LITTLE_ENDIAN).int
                val data = ByteArray(len)
                input.readFully(data)
                val id = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).int
                val payload = if (len > 10) String(data, 8, len - 10, Charsets.UTF_8) else ""
                return id to payload
            }

            send(1, TYPE_AUTH, password)
            val (authId, _) = read()
            if (authId == -1) error("RCON auth failed")
            send(2, TYPE_COMMAND, command)
            read().second
        }
    }
}
