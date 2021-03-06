package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaGruposVta
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class GruposVtaDao {

    companion object {

        fun getAllGruposVta(conn: Connection): MutableList<ListaGruposVta> {
            val listaGrupos = emptyList<ListaGruposVta>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT Grupo, Descripcion, Texto FROM HTGruposVenta" +
                                                " ORDER BY Descripcion")

                        while (rs.next()) {
                            val lista = ListaGruposVta()
                            lista.grupoId = rs.getInt("Grupo")
                            lista.descripcion = rs.getString("Descripcion")
                            lista.texto = rs.getString("Texto")
                            listaGrupos.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaGrupos
        }
    }

}