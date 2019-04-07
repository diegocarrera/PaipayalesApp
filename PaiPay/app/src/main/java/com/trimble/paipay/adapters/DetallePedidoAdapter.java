package com.trimble.paipay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.trimble.paipay.R;
import com.trimble.paipay.model.DetallePedido;

import java.util.ArrayList;

public class DetallePedidoAdapter extends ArrayAdapter<DetallePedido> {
    private ArrayList<DetallePedido> detallesPedidos;
    private Context context;

    public DetallePedidoAdapter(Context context, ArrayList<DetallePedido> detallesPedidos){
        super(context,0, detallesPedidos);
        this.context = context;
        this.detallesPedidos = detallesPedidos;
    }

    static class ViewHolder{
        public CheckedTextView producto;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.detalle_pedido_item, parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.producto = (CheckedTextView) convertView.findViewById(R.id.editproducto);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final DetallePedido currentPedido = this.detallesPedidos.get(position);
        holder.producto.setText(currentPedido.getProducto().getName() + " \n cantidad: " + Float.toString(currentPedido.getCantidad()) );
        holder.producto.setChecked(false);
        return convertView;
    }
}