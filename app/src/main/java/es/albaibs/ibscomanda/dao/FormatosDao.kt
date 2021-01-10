package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaFormatos
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class FormatosDao {

    companion object {

        fun getFormatosArticulo(conn: Connection, queArticuloId: Int): List<ListaFormatos> {
            val listaFormatos = emptyList<ListaFormatos>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT DISTINCT A.Formato, B.Descripcion FROM TarifasFormatos A" +
                                " LEFT JOIN Formatos B ON B.Codigo = A.Formato" +
                                " WHERE A.Articulo = $queArticuloId")

                        while (rs.next()) {
                            val lista = ListaFormatos()
                            lista.formatoId = rs.getShort("Formato")
                            lista.descripcion = rs.getString("Descripcion")
                            listaFormatos.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaFormatos
        }
    }
}