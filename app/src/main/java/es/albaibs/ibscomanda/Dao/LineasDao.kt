package es.albaibs.ibscomanda.Dao

import es.albaibs.ibscomanda.Varios.DatosLinea
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


        fun anyadirLinea(conn: Connection, registro: DatosLinea): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                comm.execute("INSERT INTO HTLineasCuentas (Sala, Mesa, Fraccion, Linea" +
                            " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + "," + registro.linea + ")")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}