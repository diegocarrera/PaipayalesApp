package ec.edu.espol.cvr.paipayapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import ec.edu.espol.cvr.paipayapp.R;
import ec.edu.espol.cvr.paipayapp.model.DetallePedido;

import java.util.ArrayList;

public class DetallePedidoAdapter extends ArrayAdapter<DetallePedido> {
    private ArrayList<DetallePedido> detallesPedidos;
    private Context context;
    private int contador;

    public DetallePedidoAdapter(Context context, ArrayList<DetallePedido> detallesPedidos){
        super(context,0, detallesPedidos);
        this.context = context;
        this.detallesPedidos = detallesPedidos;
    }

    static class ViewHolder{
        public TextView productoNombre;
        public TextView cantidad;
        public CheckBox check;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.detalle_pedido_item, parent,false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.productoNombre = (TextView) convertView.findViewById(R.id.editproductoNombre);
            viewHolder.cantidad = (TextView) convertView.findViewById(R.id.editproductoCantidad);
            viewHolder.check = (CheckBox) convertView.findViewById(R.id.check_box);
            convertView.setTag(viewHolder);
            viewHolder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(detallesPedidos.get(position).is_selected()){
                        detallesPedidos.get(position).set_selected(false);
                        contador--;
                    }else {
                        detallesPedidos.get(position).set_selected(true);
                        contador++;
                    }
                }
            });
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final DetallePedido currentPedido = this.detallesPedidos.get(position);
        holder.productoNombre.setText(currentPedido.getProducto().getName());
        holder.cantidad.setText("cant: " + Float.toString(currentPedido.getCantidad()) + " " + currentPedido.getProducto().getUnit() );
        holder.check.setChecked(currentPedido.is_selected());
        return convertView;
    }

    public boolean armadoCompleto(){
        return contador == detallesPedidos.size();
    }
}