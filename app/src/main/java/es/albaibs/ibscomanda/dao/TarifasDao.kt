package es.albaibs.ibscomanda.dao

import android.os.HandlerThread
import java.sql.Connection
import java.sql.Statement
import java.util.concurrent.CountDownLatch


class TarifasDao {

    companion object {

        fun getPrecio(conn: Connection, queTarifa: Int, queFormatoId: Short, queArticulo: Int): String {
            val comm: Statement = conn.createStatement()

            return try {
                if (queFormatoId > 0) {
                    val rs = comm.executeQuery("SELECT PrecioBase FROM TarifasFormatos WHERE Articulo = $queArticulo AND Tarifa = $queTarifa AND Formato = $queFormatoId")
                    return if (rs.next())
                        rs.getString("PrecioBase")
                    else
                        "0.0"
                } else {
                    val rs = comm.executeQuery("SELECT PrecioBase FROM Tarifas WHERE Articulo = $queArticulo AND Tarifa = $queTarifa")
                    return if (rs.next())
                        rs.getString("PrecioBase")
                    else
                        "0.0"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                "0.0"
            }
        }


    }
}