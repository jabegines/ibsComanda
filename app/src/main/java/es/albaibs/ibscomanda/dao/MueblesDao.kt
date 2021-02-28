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

        fun actualizarBloqueo(conn: Connection, fPuesto: Short, fSala: Short, fMesa: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Puesto FROM DatosAdicionalesDocVentas" +
                                    " WHERE CodigoAlmacen = $fSala AND Documento = $fMesa" +
                                    " AND Grupo = 'CUENTA' AND Valor = 'BLOQUEADA'")
                var quePuesto: Short = -1
                var cuentaBloqueada = false
                if (rs.next()) {
                    quePuesto = rs.getShort("Puesto")
                    cuentaBloqueada = (quePuesto != fPuesto)
                }

                return if (cuentaBloqueada) false
                else {
                    if (quePuesto != fPuesto) {
                        comm.execute("INSERT INTO DatosAdicionalesDocVentas (Documento, Puesto, CodigoAlmacen, Ejercicio, Movimiento, TipoDocumento, Linea, Grupo, Valor)" +
                                " VALUES ($fMesa, $fPuesto, $fSala, 0, 0, 0, 0, 'CUENTA', 'BLOQUEADA')")

                        true
                    } else true
                }

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun desbloquearCuenta(conn: Connection, fSala: Short, fMesa: Short) {
            val comm: Statement = conn.createStatement()

            try {
                comm.execute("DELETE FROM DatosAdicionalesDocVentas WHERE Documento = $fMesa AND CodigoAlmacen = $fSala" +
                            " AND Grupo = 'CUENTA' AND Valor = 'BLOQUEADA'")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}