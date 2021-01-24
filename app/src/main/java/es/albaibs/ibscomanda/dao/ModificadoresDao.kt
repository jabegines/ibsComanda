package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaModificadores
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class ModificadoresDao {

    companion object {

        fun getModificadores(conn: Connection, queGrupo: Short): List<ListaModificadores> {
            val listaModif = emptyList<ListaModificadores>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT A.Modificador, A.EsArticulo, A.Dosis, A.IncrPrecio, B.DescripcionTicket AS Descr" +
                                " FROM HTModificadores A" +
                                " LEFT JOIN Articulos B ON A.Modificador = B.Articulo" +
                                " WHERE A.EsArticulo = 'T' AND  A.Grupo = $queGrupo" +
                                " UNION " +
                                " SELECT A.Modificador, A.EsArticulo, A.Dosis, A.IncrPrecio, B.Descripcion AS Descr" +
                                " FROM HTModificadores A" +
                                " LEFT JOIN HTComentarios B ON A.Modificador = B.Codigo" +
                                " WHERE A.EsArticulo = 'F' AND A.Grupo = $queGrupo")

                        while (rs.next()) {
                            val lista = ListaModificadores()
                            lista.modificador = rs.getString("Modificador")
                            lista.esArticulo = rs.getString("EsArticulo")
                            if (rs.getString("Dosis") != null) lista.dosis = rs.getString("Dosis")
                            else lista.dosis = ""
                            lista.incrPrecio = rs.getString("IncrPrecio")
                            lista.descripcion = rs.getString("Descr")
                            listaModif.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaModif
        }

    }
}