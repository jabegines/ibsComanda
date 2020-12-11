package es.albaibs.ibscomanda.Dao

import es.albaibs.ibscomanda.Varios.DatosCabecera
import java.sql.Connection
import java.sql.Statement


class CuentasDao {

    companion object {

        fun nuevaCabecera(conn: Connection, registro: DatosCabecera): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                comm.execute("INSERT INTO HTCabeceraCuentas (Sala, Mesa, Fraccion, Tarifa, Usuario)" +
                            " VALUES (" + registro.sala + ", " + registro.mesa + ", " + registro.fraccion + ", 1, 0)")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}