package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaGruposVta


class GruposVtaRvAdapter(var grupos: MutableList<ListaGruposVta>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<GruposVtaRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = grupos[position]
        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            notifyDataSetChanged()
            listener.onClick(it, grupos[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_gruposvta_list, parent, false))
    }

    override fun getItemCount(): Int {
        return grupos.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaGruposVta)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val clLayout = itemView.findViewById(R.id.clGruposVta) as ConstraintLayout
        private val descripcion = itemView.findViewById(R.id.tvDescrGrupoVta) as TextView

        fun bind(grupo: ListaGruposVta, context: Context) {
            val queTexto = grupo.texto ?: ""
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
                    clLayout.setBackgroundColor(Color.parseColor(queColor))
                }
            }
            descripcion.text = grupo.descripcion
        }
    }
}