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
import ke.co.eclectic.quickstore.activities.salesorder.SOrderDetailsActivity;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.SalesOrder;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.ViewHolder> {

    List<SalesOrder> salesOrderList;
    Context context;

    /**
     * Instantiates a new Sales order adapter.
     *
     * @param context        the context
     * @param salesOrderList the sales order list
     */
    public SalesOrderAdapter(Context context, List<SalesOrder> salesOrderList) {
        this.salesOrderList = salesOrderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sales_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesOrderAdapter.ViewHolder holder, int position) {
        final SalesOrder salesOrder = salesOrderList.get(position);
        holder.txtStoreSalesBsId.setText(salesOrder.getSalesorderBsid());
        holder.txtTotalPrice.setText("Total price: ".concat(salesOrder.getTotalcostStr()));
        holder.txtBusinessName.setText(salesOrderList.get(position).getCustomername());
        holder.txtItemCount.setText("Items:".concat(salesOrder.getItemcount()));
        holder.txtUpdateDate.setText("Created on: ".concat(GlobalMethod.getFormatedDateStr(salesOrder.getCreatedon())  ));

        holder.txtStatus.setBackground(salesOrder.getStatusBgColor(context));
        holder.txtStatus.setText(salesOrder.getStatusname());

        holder.contentLay.setOnClickListener(view ->{
            HashMap<String,String> data= new HashMap<>();
            data.put("salesorder",new Gson().toJson(salesOrderList.get(position)));
            GlobalMethod.goToActivity(context,SOrderDetailsActivity.class,data);
        });

    }

    /**
     * Refresh sales order list.
     *
     * @param cList the sales order list
     */
    public void refresh(List<SalesOrder> cList) {
        salesOrderList = cList;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return salesOrderList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStoreSalesBsId;
        TextView txtTotalPrice;
        TextView txtBusinessName;
        TextView txtItemCount;
        TextView txtUpdateDate;
        TextView txtStatus;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtStoreSalesBsId = itemView.findViewById(R.id.txtStoreSalesBsId);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtBusinessName = itemView.findViewById(R.id.txtBusinessName);
            txtItemCount = itemView.findViewById(R.id.txtItemCount);
            txtUpdateDate = itemView.findViewById(R.id.txtUpdateDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }
    }
}
