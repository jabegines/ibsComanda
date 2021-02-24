package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaArticulosGrupo
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class ArticulosDao {

    companion object {

        fun tieneModificadores(conn: Connection, queArticulo: Int): MutableList<Int> {
            val listaModif = emptyList<Int>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT Grupo FROM HTModificadoresArticulo" +
                                " WHERE Articulo = " + queArticulo)

                        while (rs.next()) {
                            listaModif.add(rs.getInt("Grupo"))
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



        fun getArticulosGrupo(conn: Connection, queGrupo: Int, fSala: Short, fMesa: Short): MutableList<ListaArticulosGrupo> {
            val listaArticulos = emptyList<ListaArticulosGrupo>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT A.Articulo, B.Codigo, B.Descripcion, B.DescripcionTicket, B.Flag1, B.Flag2," +
                                " C.Codigo CodIva, D.Texto FROM HTGruposArticulo A" +
                                " LEFT JOIN Articulos B ON B.Articulo = A.Articulo" +
                                " LEFT JOIN IvasyRecargos C ON C.Tipo = B.TipoDeIva" +
                                " LEFT JOIN HTArticulos D ON D.Articulo = A.Articulo" +
                                " WHERE A.Grupo = " + queGrupo +
                                " ORDER BY B.Descripcion")

                        while (rs.next()) {
                            val lista = ListaArticulosGrupo()
                            lista.articuloId = rs.getInt("Articulo")
                            lista.codigo = rs.getString("Codigo")
                            lista.descripcion = rs.getString("Descripcion")
                            lista.descrTicket = rs.getString("DescripcionTicket")
                            lista.codigoIva = rs.getShort("CodIva")
                            lista.flag1 = rs.getInt("Flag1")
                            lista.flag2 = rs.getInt("Flag2")
                            lista.texto = rs.getString("Texto")
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