package es.albaibs.ibscomanda.dao

import java.sql.Connection
import java.sql.Statement


class UsuariosDao {

    companion object {

        fun getNombreUsuario(conn: Connection, queUsuario: Short): String {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Nombre FROM Usuarios WHERE Codigo = $queUsuario")
                return if (rs.next()) rs.getString("Nombre")
                else ""

            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

    }

}