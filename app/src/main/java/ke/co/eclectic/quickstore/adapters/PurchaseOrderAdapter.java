package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.purchaseOrder.POrderDetailsActivity;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.PurchaseOrder;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.ViewHolder> {

    List<PurchaseOrder> purchaseOrderList;
    Context context;

    /**
     * Instantiates a new Purchase order adapter.
     *
     * @param context           the context
     * @param purchaseOrderList the purchase order list
     */
    public PurchaseOrderAdapter(Context context, List<PurchaseOrder> purchaseOrderList) {
        this.purchaseOrderList = purchaseOrderList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_purchase_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderAdapter.ViewHolder holder, int position) {
        final PurchaseOrder purchaseOrder = purchaseOrderList.get(position);
        holder.txtStorePurchaseBsId.setText(purchaseOrder.getPurchaseorderBsid());
        holder.txtTotalPrice.setText("Total price: ".concat(purchaseOrder.getTotalcostStr()));
        holder.txtBusinessName.setText(purchaseOrderList.get(position).getSuppliername());
        holder.txtItemCount.setText("Items:".concat(purchaseOrder.getItemcount()));
        holder.txtUpdateDate.setText("Last update on: ".concat(GlobalMethod.getFormatedDateStr(purchaseOrder.getLastedit())  ));

        holder.txtStatus.setBackground(purchaseOrder.getStatusBgColor(context));
        holder.txtStatus.setText(purchaseOrder.getStatusName());
        holder.contentLay.setOnClickListener(view ->{
            HashMap<String,String> data= new HashMap<>();
            data.put("purchaseorder",new Gson().toJson(purchaseOrderList.get(position)));
            GlobalMethod.goToActivity(context,POrderDetailsActivity.class,data);
        });

    }

    /**
     * Refresh purchase order list.
     *
     * @param cList the purchase order list
     */
    public void refresh(List<PurchaseOrder> cList) {
        purchaseOrderList = cList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return purchaseOrderList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStorePurchaseBsId;
        TextView txtTotalPrice;
        TextView txtBusinessName;
        TextView txtItemCount;
        TextView txtUpdateDate;
        TextView txtStatus;
        LinearLayout contentLay;

        public ViewHolder(View itemView) {
            super(itemView);
            txtStorePurchaseBsId = itemView.findViewById(R.id.txtStorePurchaseBsId);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtBusinessName = itemView.findViewById(R.id.txtBusinessName);
            txtItemCount = itemView.findViewById(R.id.txtItemCount);
            txtUpdateDate = itemView.findViewById(R.id.txtUpdateDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }
    }
}
