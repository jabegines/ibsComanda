package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.DatosConfImpresora
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class SituacionesPuestoDao {

    companion object {

        fun getImpresorasSituacion(conn: Connection, quePuesto: Short, queSituacion: Short): MutableList<String> {
            val comm: Statement = conn.createStatement()
            val listaImpresoras = emptyList<String>().toMutableList()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT Impresora FROM HTSituacionesPuesto" +
                                " WHERE Almacen = 0 AND Puesto = $quePuesto AND CodConfiguracion = 1 AND Situacion = $queSituacion")

                        while (rs.next()) {
                            listaImpresoras.add(rs.getString("Impresora"))
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaImpresoras
        }

        fun getConfImpresora(conn: Connection, quePuesto: Short, queImpresora: String): DatosConfImpresora {
            val comm: Statement = conn.createStatement()
            val datosConf = DatosConfImpresora()

            return try {
                val rs = comm.executeQuery("SELECT Valor, Entero FROM ConfiguracionPuestos WHERE Almacen = 0 AND Puesto = $quePuesto")

                datosConf.ip = rs.getString("Valor")
                datosConf.puerto = rs.getInt("Entero")
                return datosConf

            } catch (e: Exception) {
                e.printStackTrace()
                return datosConf
            }
        }
    }
}