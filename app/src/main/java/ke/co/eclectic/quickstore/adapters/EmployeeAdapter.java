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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.activities.UserProfileActivity;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.models.User;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2019.
 */
public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    List<User> staffList;
    Context context;


    /**
     * Instantiates a new Employee adapter.
     *
     * @param context   the context
     * @param staffList the staff list
     */
    public EmployeeAdapter(Context context, List<User> staffList) {
        this.staffList = staffList;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_employee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {

        holder.txtUsername.setText(staffList.get(position).getFirstname().concat(" ").concat(staffList.get(position).getOthernames()));
        holder.txtUserType.setText(staffList.get(position).getRolename());
        holder.txtStoreInfo.setText(context.getString(R.string.storename).concat(staffList.get(position).getStorename()));
        holder.contentLay.setOnClickListener(view -> {
            Timber.v("staffData  "+new Gson().toJson(staffList.get(position)));
            HashMap<String ,String> data = new HashMap<>();
            data.put("staffData", new Gson().toJson(staffList.get(position)));
            GlobalMethod.goToActivity(context,UserProfileActivity.class,data);
        });
        if(staffList.get(position).getUserimg() != null && staffList.get(position).getUserimg().length() > 5){
            Timber.v(staffList.get(position).getUserimg());
            Picasso.get()
                    .load(staffList.get(position).getUserimg())
                    .fit()
                    .placeholder(R.drawable.placeholder_profile)
                    .error(R.drawable.placeholder_profile)
                    .into(holder.imgUserimg, new Callback() {
                        @Override
                        public void onSuccess() {
                            Timber.v("onSuccess");
                        }

                        @Override
                        public void onError(Exception e) {
                            Timber.v("Exception  "+e.getMessage());
                        }
                    });
        }

    }

    /**
     * Refresh employee list
     *
     * @param sList the employee list
     */
    public void refresh(List<User> sList) {
        staffList = sList;
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return staffList.size();
    }


    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername,
        txtStoreInfo,
        txtUserType;
        LinearLayout contentLay;
        CircleImageView imgUserimg;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtStoreInfo = itemView.findViewById(R.id.txtStoreInfo);
            txtUserType = itemView.findViewById(R.id.txtUserType);
            imgUserimg =  itemView.findViewById(R.id.imgUserimg);
            contentLay =  itemView.findViewById(R.id.contentLay);
            
        }
    }

}

