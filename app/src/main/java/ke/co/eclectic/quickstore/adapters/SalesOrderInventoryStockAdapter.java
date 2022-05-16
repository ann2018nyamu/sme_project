package ke.co.eclectic.quickstore.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.salesorder.fragment.SOrderListFragment;
import ke.co.eclectic.quickstore.models.InventoryStock;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class SalesOrderInventoryStockAdapter extends RecyclerView.Adapter<SalesOrderInventoryStockAdapter.ViewHolder> {

    private List<InventoryStock> inventoryStockList;
    Context context;
    private SoInventoryComm soInventoryComm;
    private Double initialCount= 0.0;



    /**
     * Instantiates a new Employee adapter.
     *
     * @param context   the context
     * @param inventoryStockList the staff list
     * @param fragment  the sales order fragment instance
     */
    public SalesOrderInventoryStockAdapter(Context context, List<InventoryStock> inventoryStockList, SOrderListFragment fragment) {
        this.inventoryStockList = inventoryStockList;
        this.context = context;
        this.soInventoryComm=  fragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sorder_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesOrderInventoryStockAdapter.ViewHolder holder, int position) {

        final InventoryStock inventoryStockItem = inventoryStockList.get(position);

        holder.txtInventoryStockName.setText(inventoryStockItem.getProductname());
        holder.etxtInventoryStockQty.setText(inventoryStockItem.getChoosenquantityStr());
        holder.txtInventoryStockPrice.setText(inventoryStockItem.getChoosenPriceStr());
        holder.etxtInventoryStockQty.setOnClickListener(v -> showAddDialogue(holder));
        holder.imgRemove.setOnClickListener(v -> removeQty(holder));
        holder.imgAdd.setOnClickListener(v -> addQty(holder));
        holder.contentLay.setOnClickListener(view -> {showAddDialogue(holder);});

    }
    private void addQty(ViewHolder holder){
        initialCount = inventoryStockList.get(holder.getAdapterPosition()).getChoosenquantity();
        initialCount =  initialCount +1;
        if(initialCount > inventoryStockList.get(holder.getAdapterPosition()).getQuantity()){
            initialCount =  initialCount -1;
            holder.etxtInventoryStockQty.setError("Stock limit reached");
            return;
        }

        holder.etxtInventoryStockQty.setText(initialCount.toString());
        inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
        soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());
        notifyItemChanged(holder.getAdapterPosition());
    }

    /**
     * Remove 1 qty.
     *
     * @param holder the holder
     */
    private void removeQty(ViewHolder holder){
        initialCount = inventoryStockList.get(holder.getAdapterPosition()).getChoosenquantity();
        initialCount =  initialCount -1;
        if(initialCount == 0){
            soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"remove",holder.getAdapterPosition());
            return;
        }

        holder.etxtInventoryStockQty.setText(initialCount.toString());
        inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
        soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());
        notifyItemChanged(holder.getAdapterPosition());
    }

    /**
     * Show add dialogue.
     *
     * @param holder the viewholder
     */
    private void showAddDialogue(ViewHolder holder) {
      
        Dialog addProdDialog = new Dialog(context);
        addProdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addProdDialog.setContentView(R.layout.dialogue_salesadd_layout);
        addProdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle =  addProdDialog.findViewById(R.id.txtTitle);
        TextView txtInfo =  addProdDialog.findViewById(R.id.txtInfo);
        Button btnMinus =  addProdDialog.findViewById(R.id.btnMinus);
        EditText etxtCount =  addProdDialog.findViewById(R.id.etxtCount);
        Button btnAdd =  addProdDialog.findViewById(R.id.btnAdd);
        Button btnCancel =  addProdDialog.findViewById(R.id.btnCancel);
        Button btnDone =  addProdDialog.findViewById(R.id.btnDone);
        initialCount = inventoryStockList.get(holder.getAdapterPosition()).getChoosenquantity();
        txtTitle.setText("Add Prefered Quantity");
        txtInfo.setText("Product Name: ".concat(inventoryStockList.get(holder.getAdapterPosition()).getProductname()));

        etxtCount.setText( initialCount.toString());
        etxtCount.setSelection(1);
        btnMinus.setOnClickListener(v -> {

            initialCount =  initialCount -1;
            if(initialCount == 0){
                soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"remove",holder.getAdapterPosition());
                addProdDialog.dismiss();
                return;
            }
            etxtCount.setText( initialCount.toString());
            holder.etxtInventoryStockQty.setText(initialCount.toString());
            inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
            soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());

        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initialCount =  initialCount +1;
                if(initialCount > inventoryStockList.get(holder.getAdapterPosition()).getQuantity()){
                    initialCount =  initialCount -1;
                    etxtCount.setError("Stock limit reached");
                    return;
                }
                etxtCount.setText( initialCount.toString());
                holder.etxtInventoryStockQty.setText(initialCount.toString());
                inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
                soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());
            }
        });

        btnDone.setOnClickListener(v -> {
            if(etxtCount.getText().toString().trim().isEmpty()){
                etxtCount.setText("0");
                soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"remove",holder.getAdapterPosition());
                addProdDialog.dismiss();
                return;
            }

            initialCount = Double.parseDouble(etxtCount.getText().toString());
            if(initialCount > inventoryStockList.get(holder.getAdapterPosition()).getQuantity()){
                initialCount =  initialCount -1;
                etxtCount.setError("Stock limit reached");
                return;
            }

            inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
            soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());
            holder.etxtInventoryStockQty.setText(initialCount.toString());

            addProdDialog.dismiss();
        });


        btnCancel.setOnClickListener(v -> {
            if(initialCount > inventoryStockList.get(holder.getAdapterPosition()).getChoosenquantity()){
                initialCount =  initialCount -1;
            }
            if(initialCount < inventoryStockList.get(holder.getAdapterPosition()).getChoosenquantity()){
                initialCount =  initialCount +1;
            }
            inventoryStockList.get(holder.getAdapterPosition()).setChoosenquantity(initialCount);
            soInventoryComm.soInventoryStockMessage(inventoryStockList.get(holder.getAdapterPosition()),"update",holder.getAdapterPosition());
            holder.etxtInventoryStockQty.setText(initialCount.toString());


            addProdDialog.dismiss();
        });

        addProdDialog.setCancelable(true);
        addProdDialog.show();
    }

    /**
     * Refresh inventory list.
     *
     * @param sList the inventory  list
     */
    public void refresh(List<InventoryStock> sList) {
        inventoryStockList = sList;
        notifyDataSetChanged();
    }

    /**
     * The interface Employee comm.
     */
    public interface SoInventoryComm{
        /**
         * send data to implementors
         *
         * @param inventoryStock      the invenotry stock item
         * @param action   the action to be performed
         */
        void soInventoryStockMessage(InventoryStock inventoryStock, String action, Integer position);
    }

    @Override
    public int getItemCount() {
        return inventoryStockList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
         ImageView imgAdd,imgRemove;
        TextView txtInventoryStockPrice,
        txtInventoryStockName;
        EditText etxtInventoryStockQty;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtInventoryStockPrice = itemView.findViewById(R.id.txtOrderStockPrice);
            txtInventoryStockName = itemView.findViewById(R.id.txtOrderStockName);
            etxtInventoryStockQty = itemView.findViewById(R.id.etxtOrderStockQty);
            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgAdd = itemView.findViewById(R.id.imgAdd);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

