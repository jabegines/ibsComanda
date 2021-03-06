package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.*
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class ModificadoresDao {

    companion object {

        fun anyadirModificadores(conn: Connection, registro: DatosLinea, lModificadores: MutableList<ListaModificadores>): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                val lineaDeMenu = 0

                for (listModif in lModificadores) {
                    val esArticulo = listModif.esArticulo == "T"
                    val articuloId = if (esArticulo) listModif.modificador else "0"
                    val comentarioId = if (esArticulo) "0" else listModif.modificador
                    val cantidad = "1.0"
                    val dosis = if (listModif.dosis == "") "0.0" else listModif.dosis
                    var flag = 0
                    if (listModif.esArticuloDeMenu == "T") flag = FLAGLINEAHOSTELERIA_ES_ARTICULO_DE_MENU + FLAGLINEAHOSTELERIA_IMPRESA
                    if (!esArticulo) flag = FLAGLINEAHOSTELERIA_ES_MODIFICADOR + FLAGLINEAHOSTELERIA_IMPRESA
                    val mitad = 0

                    val cadena = "INSERT INTO HTLineasModif (Sala, Mesa, Fraccion, Linea, NumeroModif, LineaDeMenu, Articulo, Codigo, Comentario," +
                            " Descripcion, Cantidad, Dosis, IncrPrecio, EsArticulo, Flag, GrupoModif, Mitad)" +
                            " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + ", " + registro.linea +
                            ", " + listModif.numeroModif + ", " + lineaDeMenu + ", " + articuloId + ", '" + listModif.codigo + "', " + comentarioId +
                            ", '" + listModif.descripcion + "', " + cantidad + ", " + dosis +
                            ", " + listModif.incrPrecio + ", '" + listModif.esArticulo + "', " + flag + ", " + listModif.grupoModif +
                            ", " + mitad + ")"

                    comm.execute(cadena)
                }
                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

        }

        fun getModificadores(conn: Connection, queGrupo: Short): List<ListaModificadores> {
            val listaModif = emptyList<ListaModificadores>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT A.Modificador, A.EsArticulo, A.Dosis, A.IncrPrecio, B.Codigo, B.DescripcionTicket AS Descr" +
                                " FROM HTModificadores A" +
                                " LEFT JOIN Articulos B ON A.Modificador = B.Articulo" +
                                " WHERE A.EsArticulo = 'T' AND  A.Grupo = $queGrupo" +
                                " UNION " +
                                " SELECT A.Modificador, A.EsArticulo, A.Dosis, A.IncrPrecio, CAST(B.Codigo AS VARCHAR) AS Codigo, B.Descripcion AS Descr" +
                                " FROM HTModificadores A" +
                                " LEFT JOIN HTComentarios B ON A.Modificador = B.Codigo" +
                                " WHERE A.EsArticulo = 'F' AND A.Grupo = $queGrupo")

                        while (rs.next()) {
                            val lista = ListaModificadores()
                            lista.grupoModif = queGrupo
                            lista.modificador = rs.getString("Modificador")
                            lista.esArticulo = rs.getString("EsArticulo")
                            if (rs.getString("Dosis") != null) lista.dosis = rs.getString("Dosis")
                            else lista.dosis = ""
                            lista.incrPrecio = rs.getString("IncrPrecio")
                            lista.codigo = rs.getString("Codigo")
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