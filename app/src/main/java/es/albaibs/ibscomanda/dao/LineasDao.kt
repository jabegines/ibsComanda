package es.albaibs.ibscomanda.dao

import es.albaibs.ibscomanda.varios.DatosLinea
import java.sql.Connection
import java.sql.Statement


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
                comm.execute("INSERT INTO HTLineasCuentas (Sala, Mesa, Fraccion, Linea, Orden, Articulo, Cantidad, Usuario)" +
                        " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + "," + registro.linea +
                        ", " + registro.orden + ", " + registro.articuloId + ", " + registro.cantidad + ", " + registro.usuario +
                        ")")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}