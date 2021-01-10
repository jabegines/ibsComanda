package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaArticulosGrupo
import es.albaibs.ibscomanda.varios.ListaFormatos
import kotlinx.android.synthetic.main.item_articulos_list.view.*


class FormatosRvAdapter(var formatos: List<ListaFormatos>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<FormatosRvAdapter.ViewHolder>() {

    var selectedPos: Int = RecyclerView.NO_POSITION

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = formatos[position]

        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            listener.onClick(it, formatos[position])
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_formatos_list, parent, false))
    }


    override fun getItemCount(): Int {
        return formatos.size
    }

    private fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaFormatos)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val descripcion = itemView.findViewById(R.id.tvDescrFto) as TextView

        fun bind(formato: ListaFormatos, context: Context) {
            descripcion.text = formato.descripcion
        }
    }


}