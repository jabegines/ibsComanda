package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.DatosCabecera
import es.albaibs.ibscomanda.varios.ListaLineasCuenta
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class CuentasDao {

    companion object {

        fun getLineasCuenta(conn: Connection, fSala: Short, fMesa: Short): MutableList<ListaLineasCuenta> {
            val listaCuenta = emptyList<ListaLineasCuenta>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery(
                            "SELECT Linea, Orden, Cantidad, DescrTicket, Importe FROM HTLineasCuentas" +
                                    " WHERE Sala = $fSala AND Mesa = $fMesa"
                        )

                        while (rs.next()) {
                            val lista = ListaLineasCuenta()
                            lista.linea = rs.getShort("Linea")
                            lista.orden = rs.getInt("Orden")
                            lista.cantidad = rs.getString("Cantidad")
                            if (rs.getString("DescrTicket") != null)
                                lista.descripcion = rs.getString("DescrTicket")
                            lista.importe = rs.getString("Importe")
                            listaCuenta.add(lista)
                        }
                        latch.countDown()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaCuenta
        }

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

        fun borrarCuenta(conn: Connection, fSala: Short, fMesa: Short): Boolean {
            val comm: Statement = conn.createStatement()

            return try {
                return comm.execute("DELETE FROM HTCabeceraCuentas WHERE Sala = $fSala AND Mesa = $fMesa")

            } catch (e: Exception) {
                false
            }
        }
    }
}