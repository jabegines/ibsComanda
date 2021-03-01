package es.albaibs.ibscomanda.ventas

import android.os.HandlerThread
import es.albaibs.ibscomanda.DBConnection
import es.albaibs.ibscomanda.dao.SalasDao
import es.albaibs.ibscomanda.dao.UsuariosDao
import es.albaibs.ibscomanda.varios.DatosCocina
import es.albaibs.ibscomanda.varios.DatosConfImpresora
import java.net.Socket
import java.sql.Connection


class Impresion {


    companion object {

        val TXT_2HEIGHT = byteArrayOf(0x1b, 0x21, 0x10)                 // Double height text
        val TXT_2WIDTH = byteArrayOf(0x1b, 0x21, 0x20)                  // Double width text
        val TXT_BOLD_ON = byteArrayOf(0x1b, 0x45, 0x01)                 // Bold font ON
        //val PAPER_FULL_CUT = byteArrayOf(0x1d, 0x56, 0x00)              // Full cut paper
        //val CTL_LF = byteArrayOf(0x0a)                                  // Print and line feed
        //val BEEPER = byteArrayOf(0x1b, 0x42, 0x05, 0x09)                // Beeps 5 times for 9*50ms each time


        fun imprimir(lDatosCocina: List<DatosCocina>, datosImpresora: DatosConfImpresora, fSitActual: Short, fSala: Short, fMesa: Short, fUsuario: Short) {
            val connInf: Connection = DBConnection.connectionINF as Connection

            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    val socket = Socket(datosImpresora.ip, datosImpresora.puerto)
                    val outputStream = socket.getOutputStream()
                    //outputStream.write(TXT_2WIDTH)
                    var text1 = "Para: " + lDatosCocina[0].descrSituacion + "\n\n"
                    outputStream.write(text1.toByteArray())

                    //outputStream.write(TXT_BOLD_ON)
                    val queNombreUsuario = UsuariosDao.getNombreUsuario(connInf, fUsuario)
                    val queNombreSala = SalasDao.getNombreSala(connInf, fSala)
                    text1 = "$queNombreUsuario     $queNombreSala\n\n"
                    outputStream.write(text1.toByteArray())

                    //outputStream.write(TXT_2HEIGHT)
                    outputStream.write(TXT_BOLD_ON)
                    text1 = "Sala/Mesa: $fMesa/$fSala\n\n"
                    outputStream.write(text1.toByteArray())

                    text1 = "------------------------------" + "\n\n"
                    outputStream.write(text1.toByteArray())

                    outputStream.write(TXT_2HEIGHT)
                    outputStream.write(TXT_2WIDTH)
                    for (datoCocina in lDatosCocina) {
                        if (datoCocina.situacion == fSitActual) {
                            text1 = String.format("%.0f", datoCocina.cantidad) + " " + datoCocina.descripcion + "\n\n"
                            outputStream.write(text1.toByteArray())
                        }
                    }

                    text1 = "\n\n\n"
                    outputStream.write(text1.toByteArray())
                    outputStream.close()
                }
            }
            uiThread.start()
        }

    }

}