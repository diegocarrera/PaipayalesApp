package ec.edu.espol.cvr.paipayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ec.edu.espol.cvr.paipayapp.R;
import ec.edu.espol.cvr.paipayapp.model.Pedido;

import java.util.ArrayList;

public class PedidoAdapter extends ArrayAdapter<Pedido> {
    private ArrayList<Pedido> pedidos;
    private Context context;

    public PedidoAdapter(Context context, ArrayList<Pedido> pedidos){
        super(context,0, pedidos);
        this.context = context;
        this.pedidos = pedidos;
    }

    static class ViewHolder{
        public TextView codigo;
        public TextView fecha;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.pedido_item, parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.codigo = (TextView) convertView.findViewById(R.id.editcodigo);
            viewHolder.fecha = (TextView) convertView.findViewById(R.id.editfecha);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Pedido currentPedido = this.pedidos.get(position);
        holder.codigo.setText("Pedido #" + String.valueOf(currentPedido.getCodigo()));
        holder.fecha.setText("Fecha : " + currentPedido.getFecha().toString());
        return convertView;
    }
}
