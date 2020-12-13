package es.albaibs.ibscomanda.dao

import java.sql.Connection
import java.sql.Statement


class SalasDao {

    companion object {

        fun existeSala(conn: Connection, queSala: Int): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Sala FROM HTSalas WHERE Sala = $queSala")

                rs.next()

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


        fun existeMesa(conn: Connection, queSala: Int, queMesa: Int): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Mesa FROM HTDistribucionMesas WHERE Sala = $queSala AND Mesa = $queMesa")

                rs.next()

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }
}