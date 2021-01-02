package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class ConfiguracionesDao {

    companion object {

        fun getEntero(conn: Connection, queGrupo: String, queValor: String): Int {
            val comm: Statement = conn.createStatement()
            var queEntero = 0

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT Entero FROM Configuraciones WHERE Grupo = '$queGrupo' AND Valor = '$queValor' AND Aplicacion = 0")
                        if (rs.next()) {
                            queEntero = rs.getInt("Entero")
                        }

                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return queEntero
        }


    }
}