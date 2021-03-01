package es.albaibs.ibscomanda.varios

const val VERSION_PROGRAMA = "1.00"
const val COMPILACION_PROGRAMA = ".1"

const val VIENDO_GRUPOS = 1
const val VIENDO_ARTICULOS = 2



// Flag1 artículos
const val FLAGARTICULO_USARFORMATOS = 4096

// Flag2 artículos
const val FLAGARTICULO_ES_MENU = 8192

// Flag1 linea hostelería
const val FLAGLINEAHOSTELERIA_ES_MODIFICADOR = 4
const val FLAGLINEAHOSTELERIA_IMPRESA = 8
const val FLAGLINEAHOSTELERIA_ES_MENU = 32
const val FLAGLINEAHOSTELERIA_ES_ARTICULO_DE_MENU = 64


// Flag Estado mesa
const val FLAGESTADOMESA_OCUPADA = 1
const val FLAGESTADOMESA_ENESPERA = 8


class ListaSalas {
    var salaId: Short = 0
    var nombre: String = ""
}

class ListaMesas {
    var mesaId: Short = 0
    var ocupada: Boolean = false
}

class ListaGruposVta {
    var grupoId: Int = 0
    var descripcion: String = ""
    var texto: String? = null
}


class ListaGruposModif {
    var grupoId: Short = 0
    var descripcion: String = ""
}

class ListaModificadores {
    var numeroModif: Int = 0
    var grupoModif: Short = 0
    var modificador: String = ""
    var esArticulo: String = ""
    var esArticuloDeMenu: String = ""
    var dosis: String = ""
    var incrPrecio: String = ""
    var codigo: String = ""
    var descripcion: String = ""
}

class ListaArticulosGrupo {
    var articuloId: Int = 0
    var codigo: String = ""
    var descripcion: String = ""
    var descrTicket: String = ""
    var codigoIva: Short = 0
    var flag1: Int = 0
    var flag2: Int = 0
    var texto: String? = null
    var cantidad: Double = 0.0
}

class ListaLineasCuenta {
    var linea: Short = 0
    var orden: Int = 0
    var cantidad: String = ""
    var descripcion: String = ""
    var importe: String = ""
}

class ListaFormatos {
    var formatoId: Short = 0
    var descripcion: String = ""
}


// Registros para trabajar con las tablas
class DatosCabecera {
    var sala: Short = 0
    var mesa: Short = 0
    var fraccion: Short = 0
    var cliente: Int = 0
    var tarifa: Short = 0
    var comensales: Short = 0
    var horaInicio: String = ""
    var usuario: Short = 0
    var flag: Short = 0
    var tipoIva: Short = 0
    var concepto: String = ""

}


class DatosLinea {
    var sala: Short = 0
    var mesa: Short = 0
    var fraccion: Short = 0
    var linea: Int = 0
    var orden: Int = 0
    var articuloId: Int = 0
    var codigoArt: String = ""
    var descripcion: String = ""
    var descrTicket: String = ""
    var cantidad: String = ""
    var piezas: String = ""
    var precio: String = ""
    var codigoDeIva: Short = 101
    var importe: String = ""
    var usuario: Short = 0
    var formatoId: Short = 0
    var flag: Int = 0
    var flag2: Int = 0
}


class DatosCocina {
    var articuloId: Int = 0
    var linea: Int = 0
    var descripcion: String = ""
    var descrTicket: String = ""
    var cantidad: Double = 0.0
    var piezas: String = ""
    var flag: Short = 0
    var situacion: Short = 0
    var descrSituacion: String = ""
    var orden: Int = 0
}


class DatosConfImpresora {
    var ip: String = ""
    var puerto: Int = 0
}