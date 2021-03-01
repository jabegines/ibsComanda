package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaArticulosGrupo


class ArticulosGrupoRvAdapter(private var articulos: MutableList<ListaArticulosGrupo>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<ArticulosGrupoRvAdapter.ViewHolder>() {

    var selectedPos: Int = RecyclerView.NO_POSITION


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos[position]

        //if (selectedPos == position) {
        //    holder.itemView.tvCantVend.visibility = View.VISIBLE
        //    holder.itemView.tvCantVend.text = String.format("%.0f", queCantidad)
        //}
        //else holder.itemView.tvCantVend.visibility = View.INVISIBLE

        holder.bind(item, selectedPos, position)

        holder.itemView.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            listener.onClick(it, articulos[position])
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_articulos_list, parent, false))
    }


    override fun getItemCount(): Int {
        return articulos.size
    }

    private fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaArticulosGrupo)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val llLayout = itemView.findViewById(R.id.llArticulosVta) as LinearLayout
        private val descripcion = itemView.findViewById(R.id.tvDescrArt) as TextView
        private val cantidad = itemView.findViewById(R.id.tvCantVend) as TextView

        fun bind(articulo: ListaArticulosGrupo, selectedPos: Int, position: Int) {
            val queTexto = articulo.texto ?: ""
            if (queTexto != "") {
                val pos1 = queTexto.indexOf("BKCOL=", 0, true).plus(6)
                val pos2 = queTexto.indexOf("FONTCOL=", 0, true).plus(9)
                val pos3 = queTexto.indexOf("FONT=", 0, true)

                var colorBack = 0
                var colorTexto = 0
                if (pos1 > 0) {
                    colorBack = queTexto.substring(pos1, pos2 - 10).toInt()
                    colorTexto = queTexto.substring(pos2 - 1, pos3 - 1).toInt()
                }

                var queHex = Integer.toHexString(colorTexto)
                if (queHex.length < 6) queHex = queHex.padEnd(6, '0')

                var queColor = "#$queHex"
                descripcion.setTextColor(Color.parseColor(queColor))

                if (colorBack > 0) {
                    queHex = Integer.toHexString(colorBack)
                    if (queHex.length < 6) queHex = queHex.padEnd(6, '0')

                    queColor = "#$queHex"
                    llLayout.setBackgroundColor(Color.parseColor(queColor))
                }
            }
            descripcion.text = articulo.descripcion

            if (selectedPos != position) cantidad.textSize = 18f
            if (articulo.cantidad != 0.0) cantidad.text = String.format("%.0f", articulo.cantidad)
            else cantidad.text = ""
        }
    }

}