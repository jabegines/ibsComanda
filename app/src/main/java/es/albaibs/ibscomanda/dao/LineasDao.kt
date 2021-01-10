package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.DatosCocina
import es.albaibs.ibscomanda.varios.DatosLinea
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class LineasDao {

    companion object {

        fun getUltimaLinea(conn: Connection, fSala: Short, fMesa: Short): Int {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT MAX(Linea) MaxLinea FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa")
                return if (rs.next())
                    rs.getInt("MaxLinea") + 1
                else
                    0

            } catch (e:Exception) {
                e.printStackTrace()
                0
            }
        }


        fun sinLineas(conn: Connection, fSala: Short, fMesa: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Linea FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa")
                return !rs.next()

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun anyadirLinea(conn: Connection, registro: DatosLinea): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                comm.execute("INSERT INTO HTLineasCuentas (Sala, Mesa, Fraccion, Linea, Orden, Articulo, Codigo, Descripcion, DescrTicket," +
                        " Cantidad, Piezas, Precio, Importe, Usuario)" +
                        " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + "," + registro.linea +
                        ", " + registro.orden + ", " + registro.articuloId + ", " + registro.codigoArt + ", '" + registro.descripcion +
                        "', '" + registro.descrTicket + "', " + registro.cantidad + ", " + registro.piezas + ", " + registro.precio +
                        ", " + registro.importe + ", " + registro.usuario + ")")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun actualizarCantidad(conn: Connection, queSala: Short, queMesa: Short, queFraccion: Short, queLinea: Int, queCantidad: Int): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                comm.execute("UPDATE HTLineasCuentas SET Cantidad = Cantidad + " + queCantidad + " WHERE Sala = " + queSala + " AND Mesa = " + queMesa +
                        " AND Fraccion = " + queFraccion  + " AND Linea = " + queLinea)

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun dimeCantLinea(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Short, fLinea: Int): Double {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Cantidad FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")
                return if (rs.next())
                    rs.getDouble("Cantidad")
                else
                    0.0

            } catch (e:Exception) {
                e.printStackTrace()
                0.0
            }
        }

        fun consultaCocina(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Short): List<DatosCocina> {
            val comm: Statement = conn.createStatement()
            val listaCocina = emptyList<DatosCocina>().toMutableList()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery(
                            "SELECT A.Articulo, A.Linea, A.Descripcion, A.Cantidad, A.Piezas, A.Flag, B.Situacion, A.Orden" +
                                    " FROM HTLineasCuentas A, HTSituacionesArticulo B" +
                                    " WHERE A.Articulo = B.Articulo" +
                                    " AND A.Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion" +
                                    " AND (A.Flag & 8 = 0 OR A.Flag & 32 <> 0)" +
                                    " ORDER BY B.Situacion, A.Orden"
                        )

                        while (rs.next()) {
                            val datos = DatosCocina()
                            datos.articuloId = rs.getInt("Articulo")
                            datos.linea = rs.getInt("Linea")
                            datos.descripcion = rs.getString("Descripcion")
                            datos.descrTicket = rs.getString("Descripcion")
                            datos.cantidad = rs.getString("Cantidad")
                            datos.piezas = rs.getString("Piezas")
                            datos.flag = rs.getShort("Flag")
                            datos.situacion = rs.getShort("Situacion")
                            datos.orden = rs.getInt("Orden")

                            listaCocina.add(datos)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaCocina
        }

    }
}