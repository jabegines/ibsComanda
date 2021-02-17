package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaGruposModif
import kotlinx.android.synthetic.main.item_gruposvta_list.view.*


class GruposModifRvAdapter(var grupos: List<ListaGruposModif>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<GruposModifRvAdapter.ViewHolder>() {

    var selectedPos: Int = RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = grupos[position]

        if (selectedPos == position) holder.itemView.tvDescrGrupoVta.setTextColor(Color.RED)
        else holder.itemView.tvDescrGrupoVta.setTextColor(Color.BLACK)

        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            selectedPos = position
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
        fun onClick(view: View, data: ListaGruposModif)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val descripcion = itemView.findViewById(R.id.tvDescrGrupoVta) as TextView

        fun bind(grupo: ListaGruposModif, context: Context) {
            descripcion.text = grupo.descripcion
        }
    }
}