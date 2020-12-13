package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaArticulosGrupo
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class ArticulosDao {

    companion object {

        fun getArticulosGrupo(conn: Connection, queGrupo: Int): MutableList<ListaArticulosGrupo> {
            val listaArticulos = emptyList<ListaArticulosGrupo>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT A.Articulo, B.Codigo, B.Descripcion, B.DescripcionTicket FROM HTGruposArticulo A" +
                                " LEFT JOIN Articulos B ON B.Articulo = A.Articulo" +
                                " WHERE A.Grupo = " + queGrupo +
                                " ORDER BY B.Descripcion")

                        while (rs.next()) {
                            val lista = ListaArticulosGrupo()
                            lista.articuloId = rs.getInt("ARTICULO")
                            lista.codigo = rs.getString("CODIGO")
                            lista.descripcion = rs.getString("DESCRIPCION")
                            lista.descrTicket = rs.getString("DESCRIPCIONTICKET")
                            listaArticulos.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaArticulos
        }
    }
}