package es.albaibs.ibscomanda.Dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.Varios.ListaGruposVta
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
                        val rs = comm.executeQuery("SELECT Grupo, Descripcion FROM HTGruposVenta")

                        while (rs.next()) {
                            val lista = ListaGruposVta()
                            lista.grupoId = rs.getInt("GRUPO")
                            lista.descripcion = rs.getString("DESCRIPCION")
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