package es.albaibs.ibscomanda.Ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.Varios.ListaGruposVta


class GruposVtaRvAdapter(var grupos: MutableList<ListaGruposVta>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<GruposVtaRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = grupos[position]
        holder.bind(item, context)
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
        private val descripcion = itemView.findViewById(R.id.tvDescrGrupoVta) as TextView

        fun bind(grupo: ListaGruposVta, context: Context) {
            descripcion.text = grupo.descripcion
        }
    }
}