package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.ListaMesas
import es.albaibs.ibscomanda.varios.ListaSalas
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class SalasDao {

    companion object {

        fun getAllSalas(conn: Connection): MutableList<ListaSalas> {
            val listaSalas = emptyList<ListaSalas>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT Sala, Nombre FROM HTSalas" +
                                " ORDER BY Sala")

                        while (rs.next()) {
                            val lista = ListaSalas()
                            lista.salaId = rs.getShort("Sala")
                            lista.nombre = rs.getString("Nombre")
                            listaSalas.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaSalas
        }


        fun getMesasSala(conn: Connection, queSala: Short): MutableList<ListaMesas> {
            val listaMesas = emptyList<ListaMesas>().toMutableList()
            val comm: Statement = conn.createStatement()

            val latch = CountDownLatch(1)
            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    try {
                        val rs = comm.executeQuery("SELECT DISTINCT A.Mesa, B.Sala FROM HTDistribucionMesas A" +
                                " LEFT JOIN HTLineasCuentas B ON B.Sala = A.Sala AND B.Mesa = A.Mesa" +
                                " WHERE A.Sala = $queSala AND A.Tipo IN (1, 2, 3, 4, 100, 101, 102, 103, 104, 105, 106, 107)" +
                                " ORDER BY A.Mesa")

                        while (rs.next()) {
                            val lista = ListaMesas()
                            lista.mesaId = rs.getShort("Mesa")
                            lista.ocupada = rs.getString("Sala") != null
                            listaMesas.add(lista)
                        }
                        latch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            uiThread.start()
            latch.await()
            return listaMesas
        }


        fun getNombreSala(conn: Connection, queSala: Short): String {
            val comm: Statement = conn.createStatement()

            return try {
                val rs = comm.executeQuery("SELECT Nombre FROM HTSalas WHERE Sala = $queSala")
                if (rs.next()) rs.getString("Nombre")
                else ""

            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }


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