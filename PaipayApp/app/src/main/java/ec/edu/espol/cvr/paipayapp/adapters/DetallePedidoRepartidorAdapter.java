package ec.edu.espol.cvr.paipayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ec.edu.espol.cvr.paipayapp.R;
import ec.edu.espol.cvr.paipayapp.model.DetallePedido;

public class DetallePedidoRepartidorAdapter extends ArrayAdapter<DetallePedido> {
    private ArrayList<DetallePedido> detallesPedidos;
    private Context context;

    public DetallePedidoRepartidorAdapter(Context context, ArrayList<DetallePedido> detallesPedidos){
        super(context,0, detallesPedidos);
        this.context = context;
        this.detallesPedidos = detallesPedidos;
    }

    static class ViewHolder{
        public TextView productoNombre;
        public TextView cantidad;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.detalle_pedido_item_repartidor, parent,false);
            final DetallePedidoAdapter.ViewHolder viewHolder = new DetallePedidoAdapter.ViewHolder();
            viewHolder.productoNombre = (TextView) convertView.findViewById(R.id.editproductoNombre);
            viewHolder.cantidad = (TextView) convertView.findViewById(R.id.editproductoCantidad);
            convertView.setTag(viewHolder);
        }
        DetallePedidoAdapter.ViewHolder holder = (DetallePedidoAdapter.ViewHolder) convertView.getTag();
        final DetallePedido currentPedido = this.detallesPedidos.get(position);
        holder.productoNombre.setText(currentPedido.getProducto().getName());
        holder.cantidad.setText("cant: " + Float.toString(currentPedido.getCantidad()) + " " + currentPedido.getProducto().getUnit() );
        return convertView;
    }

}
