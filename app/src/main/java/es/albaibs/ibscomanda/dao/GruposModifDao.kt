package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaGruposModif
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class GruposModifDao {

    companion object {

        fun getGruposModif(conn: Connection, queArticuloId: Int): List<ListaGruposModif> {
            val listaGrupos = emptyList<ListaGruposModif>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT A.Grupo, B.Descripcion FROM HTModificadoresArticulo A, HTGruposModificadores B" +
                                " WHERE A.Articulo = $queArticuloId AND A.Grupo = B.Grupo")

                        while (rs.next()) {
                            val lista = ListaGruposModif()
                            lista.grupoId = rs.getShort("Grupo")
                            lista.descripcion = rs.getString("Descripcion")
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