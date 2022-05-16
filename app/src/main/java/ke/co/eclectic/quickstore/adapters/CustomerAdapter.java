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

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.models.Customer;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2019.
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    List<Customer> customerList;
    Context context;
    CustomerComm customerComm;

    /**
     * Instantiates a new Customer adapter.
     *
     * @param context      the context
     * @param customerList the customer list
     */
    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.customerList = customerList;
        this.context = context;
        this.customerComm= (CustomerComm) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        final Customer customer = customerList.get(position);
        holder.txtCompanyName.setText(customer.getCompanyname());
        holder.txtContactName.setText(customer.getContactname());
        holder.contentLay.setOnClickListener(view -> {
            customerComm.customerMessage(position,customerList.get(position),"view");
        });
    }

    /**
     * Refresh customer list.
     *
     * @param sList the customer  list
     */
    public void refresh(List<Customer> sList) {
        customerList = sList;
        notifyDataSetChanged();
    }

    public interface CustomerComm{
        void customerMessage(int position, Customer customer, String action);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCompanyName;
        TextView txtContactName;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtCompanyName = itemView.findViewById(R.id.txtCompanyName);
            txtContactName = itemView.findViewById(R.id.txtContactName);
            contentLay =  itemView.findViewById(R.id.contentLay);
        }

    }
}
