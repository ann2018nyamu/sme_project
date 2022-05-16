package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.InventoryStock;

/**
 * Created by Manduku O. David on 21/12/2019.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    List<InventoryStock> inventoryList;
    Context context;
    InventoryComm inventoryComm;

    /**
     * Instantiates a new Employee adapter.
     *
     * @param context   the context
     * @param inventoryList the staff list
     */
    public InventoryAdapter(Context context, List<InventoryStock> inventoryList) {
        this.inventoryList = inventoryList;
        this.context = context;
        inventoryComm = (InventoryComm) context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if(viewType == 0 ){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_header, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {

         if(inventoryList.get(position).getCategoryname().contentEquals(inventoryList.get(position).getProductname())){
             return 0;
         }else{
             return 1;
         }
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.ViewHolder holder, int position) {

        final InventoryStock inventoryStock = inventoryList.get(position);

            holder.txtProductName.setText(inventoryStock.getProductname());
            holder.txtProductUnit.setText(inventoryStock.getUnitname());

        holder.contentLay.setOnClickListener(view -> inventoryComm.inventoryMessage(position,inventoryStock,"view"));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    /**
     * Refresh inventory stock list.
     *
     * @param sList the s list
     */
    public void refresh(List<InventoryStock> sList) {

        inventoryList = sList;
        notifyDataSetChanged();
    }

    /**
     * The interface inventory comm.
     */
    public interface InventoryComm{
        /**
         * send position, inventory object and action to the activity/fragment implementing the interface
         *
         * @param position the position
         * @param inventoryStock      the inventory item
         * @param action   the action
         */
        void inventoryMessage(int position, InventoryStock inventoryStock, String action);
    } 

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }


    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductUnit, txtProductName;

        LinearLayout contentLay;
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtProductUnit = itemView.findViewById(R.id.txtProductUnit);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

