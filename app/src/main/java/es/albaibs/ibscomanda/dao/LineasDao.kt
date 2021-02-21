package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.DatosCocina
import es.albaibs.ibscomanda.varios.DatosLinea
import net.sourceforge.jtds.jdbc.DateTime
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch


class LineasDao {

    companion object {

        fun dimeMaxOrden(conn: Connection, fSala: Short, fMesa: Short): Int {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT MAX(Orden) MaxOrden FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa")
                return if (rs.next())
                    rs.getInt("MaxOrden") + 1
                else
                    0

            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }

        fun getUltimaLinea(conn: Connection, fSala: Short, fMesa: Short): Int {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT MAX(Linea) MaxLinea FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa")
                return if (rs.next())
                    rs.getInt("MaxLinea") + 1
                else
                    0

            } catch (e: Exception) {
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

        fun subirOrden(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Int, fLinea: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                var rs = comm.executeQuery("SELECT Orden FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa" +
                        " AND Fraccion = $fFraccion AND Linea = $fLinea")
                if (rs.next()) {
                    val queOrden = rs.getShort("Orden")

                    // Vemos si tenemos alguna línea con un orden más bajo
                    rs = comm.executeQuery("SELECT Max(Orden) MaxOrden FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa" +
                                " AND Fraccion = $fFraccion AND Orden < $queOrden")
                    if (rs.next()) {
                        val queMaxOrden = rs.getShort("MaxOrden")
                        comm.execute("UPDATE HTLineasCuentas SET Orden = -1" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")

                        comm.execute("UPDATE HTLineasCuentas SET Orden = $queMaxOrden+1" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Orden = $queMaxOrden")

                        comm.execute("UPDATE HTLineasCuentas SET Orden = $queMaxOrden" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")
                    }
                }
                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        fun bajarOrden(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Int, fLinea: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                var rs = comm.executeQuery("SELECT Orden FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa" +
                        " AND Fraccion = $fFraccion AND Linea = $fLinea")
                if (rs.next()) {
                    val queOrden = rs.getShort("Orden")

                    // Vemos si tenemos alguna línea con un orden más alto
                    rs = comm.executeQuery("SELECT MIN(Orden) MinOrden FROM HTLineasCuentas WHERE Sala= $fSala AND Mesa = $fMesa" +
                            " AND Fraccion = $fFraccion AND Orden > $queOrden")
                    if (rs.next()) {
                        val queMinOrden = rs.getShort("MinOrden")
                        comm.execute("UPDATE HTLineasCuentas SET Orden = -1" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")

                        comm.execute("UPDATE HTLineasCuentas SET Orden = $queMinOrden-1" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Orden = $queMinOrden")

                        comm.execute("UPDATE HTLineasCuentas SET Orden = $queMinOrden" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")
                    }
                }
                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        fun modifCantidadLinea(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Int, fLinea: Short, queCantidad: String): Boolean {
            val comm: Statement = conn.createStatement()
            return try {
                comm.execute("UPDATE HTLineasCuentas SET Cantidad = $queCantidad" +
                        " WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        fun borrarLinea(conn: Connection, fSala: Short, fMesa: Short, fFraccion: Int, fLinea: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                comm.execute("DELETE FROM HTLineasCuentas WHERE Sala = $fSala AND Mesa = $fMesa AND Fraccion = $fFraccion AND Linea = $fLinea")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        fun anyadirLinea(conn: Connection, registro: DatosLinea): Boolean {
            val comm: Statement = conn.createStatement()
            val tim = System.currentTimeMillis()
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val queHoraPedido = df.format(tim)


            return try {
                comm.execute("INSERT INTO HTLineasCuentas (Sala, Mesa, Fraccion, Linea, Orden, Articulo, Codigo, Descripcion, DescrTicket," +
                        " Cantidad, Piezas, Precio, TipoDeIva, Importe, Flag, Formato, Usuario, Flag2, HoraPedido)" +
                        " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + "," + registro.linea +
                        ", " + registro.orden + ", " + registro.articuloId + ", '" + registro.codigoArt + "', '" + registro.descripcion +
                        "', '" + registro.descrTicket + "', " + registro.cantidad + ", " + registro.piezas + ", " + registro.precio + ", " + registro.codigoDeIva +
                        ", " + registro.importe + "," + registro.flag + ", " + registro.formatoId + ", " + registro.usuario + ", " + registro.flag2 +
                        ", '" + queHoraPedido + "')")

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
                            "SELECT A.Articulo, A.Linea, A.Descripcion, A.Cantidad, A.Piezas, A.Flag, B.Situacion, C.Descripcion DescrSit, A.Orden" +
                                    " FROM HTLineasCuentas A, HTSituacionesArticulo B" +
                                    " LEFT JOIN HTSituaciones C ON C.Codigo = B.Situacion" +
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
                            datos.descrSituacion = rs.getString("DescrSit")
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