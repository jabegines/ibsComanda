package es.albaibs.ibscomanda.dao

import java.sql.Connection
import java.sql.Statement


class MueblesDao {

    companion object {

        fun guardarFlag(conn: Connection, queFlag: Int, fSala: Short, fMesa: Short): Boolean {
            val comm: Statement = conn.createStatement()
            return try {
                comm.execute("UPDATE HTDistribucionMesas SET Flag = $queFlag" +
                        " WHERE Sala = $fSala AND Mesa = $fMesa")

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}