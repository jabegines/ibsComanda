package es.albaibs.ibscomanda.ventas

import android.os.HandlerThread
import es.albaibs.ibscomanda.varios.DatosCocina
import es.albaibs.ibscomanda.varios.DatosConfImpresora
import java.net.Socket


class Impresion {


    companion object {

        val TXT_2HEIGHT = byteArrayOf(0x1b, 0x21, 0x10)                 // Double height text
        val TXT_2WIDTH = byteArrayOf(0x1b, 0x21, 0x20)                  // Double width text
        val TXT_BOLD_ON = byteArrayOf(0x1b, 0x45, 0x01)                 // Bold font ON
        val PAPER_FULL_CUT = byteArrayOf(0x1d, 0x56, 0x00)              // Full cut paper
        val CTL_LF = byteArrayOf(0x0a)                                  // Print and line feed
        val BEEPER = byteArrayOf(0x1b, 0x42, 0x05, 0x09)                // Beeps 5 times for 9*50ms each time


        fun imprimir(datoCocina: DatosCocina, datosImpresora: DatosConfImpresora) {

            val uiThread = object : HandlerThread("UIHandler") {
                override fun run() {
                    val socket = Socket(datosImpresora.ip, datosImpresora.puerto)
                    val outputStream = socket.getOutputStream()
                    outputStream.write(TXT_2WIDTH)
                    var text1 = "Cocina" + "\n\n"
                    outputStream.write(text1.toByteArray())

                    outputStream.write(TXT_BOLD_ON)
                    text1 = "Negrita" + "\n\n"
                    outputStream.write(text1.toByteArray())


                    outputStream.close()
                }
            }
            uiThread.start()
        }

    }

}