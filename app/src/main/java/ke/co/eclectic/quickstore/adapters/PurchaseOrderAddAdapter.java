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
import ke.co.eclectic.quickstore.activities.purchaseOrder.fragment.OrderProductFragment;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class PurchaseOrderAddAdapter extends RecyclerView.Adapter<PurchaseOrderAddAdapter.ViewHolder> {

    List<InventoryStock> productList;
    Context context;
    PurchaseOComm purchaseOComm;


    /**
     * Instantiates a new Purchase order add adapter.
     *
     * @param context              the context
     * @param productList          the product list
     * @param orderProductFragment the order product fragment
     */
    public PurchaseOrderAddAdapter(Context context, List<InventoryStock> productList, OrderProductFragment orderProductFragment) {
        this.productList = productList;
        this.context = context;
        this.purchaseOComm=  orderProductFragment;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_purchaseorder_inventorystock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderAddAdapter.ViewHolder holder, int position) {

        final InventoryStock stockItem = productList.get(position);
        holder.txtStockName.setText(stockItem.getProductname());


        if(stockItem.getProductname().toLowerCase().contentEquals("rewards")){
            holder.txtStockPrice.setText("");
            holder.txtTitleCode.setText("");
            holder.txtTitleCode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.reward_black,0,0,0);
        }else{
            String[] names = stockItem.getProductname().replaceAll("\\s{2,}", " ").trim().split(" ");
            Timber.v(stockItem.getProductname().replaceAll("\\s{2,}", " ").trim());
            Character s1 = names[0].charAt(0);
            Character s2 =names[0].charAt( names[0].length()-1);
            Timber.v(names.length+"  names");
            if(names.length > 1){
                s2 = names[1].charAt(0);
            }
            holder.txtTitleCode.setText(s1.toString().concat(s2.toString()).toUpperCase());
            holder.txtStockPrice.setText(stockItem.getPurchasepriceStr());
        }
        holder.contentLay.setOnClickListener(view -> purchaseOComm.posProdMessage(stockItem,"add_qty"));


    }

    /**
     * Refresh inventory stock list.
     *
     * @param sList the inventory stock list
     */
    public void refresh(List<InventoryStock> sList) {
        productList = sList;
        notifyDataSetChanged();
    }


    public interface PurchaseOComm{
        /**
         * Str message.
         *
         * @param inventoryStock      the inventory stock item
         * @param action   the action to be performed
         */
        void posProdMessage(InventoryStock inventoryStock, String action);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStockPrice,
        txtStockName,
        txtTitleCode;
        LinearLayout contentLay;
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtStockPrice = itemView.findViewById(R.id.txtStockPrice);
            txtStockName = itemView.findViewById(R.id.txtStockName);
            txtTitleCode = itemView.findViewById(R.id.txtTitleCode);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

