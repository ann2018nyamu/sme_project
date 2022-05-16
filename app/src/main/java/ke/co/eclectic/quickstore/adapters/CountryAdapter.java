package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.inc.AppConfig;
import ke.co.eclectic.quickstore.models.Country;
import ke.co.eclectic.quickstore.viewModel.CountryViewModel;
import timber.log.Timber;

/**
 * Created by Manduku O. David on 21/12/2019.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    private  String action="";
    List<Country> countryList;
    Context context;
    CountryComm countryComm;
    private CountryViewModel mCountryViewModel;



    /**
     * Instantiates a new Country adapter.
     *
     * @param context     the context
     * @param countryList the country list
     * @param action      the action
     */
    public CountryAdapter(Context context, List<Country> countryList, String action,CountryViewModel mCoViewModel) {
        this.countryList = countryList;
        this.context = context;
        this.action = action;
        this.mCountryViewModel = mCoViewModel;
       this.countryComm = (CountryComm) context;

    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_country, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Refresh country list
     *
     * @param cList the country  list
     */
    public void refresh(List<Country> cList){
        countryList = cList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.ViewHolder holder, int position) {
        final int pos = position;
        final Country country = countryList.get(pos);

        holder.txtName.setTypeface(GlobalVariable.getMontserratMedium(context));
        if(action.contentEquals("showcode")){
            holder.txtName.setText(countryList.get(pos).getName().concat("(").concat(countryList.get(pos).getDialCode()).concat(")") );
        }else{
            holder.txtName.setText(countryList.get(pos).getName());
        }
        if(countryList.get(pos).getImgBitmap() != null){
            holder.imgFlag.setImageBitmap(countryList.get(pos).getImgBitmap());
        }else {
            Picasso.get()
                    .load(AppConfig.SERVER_URL.concat("api/countries?flag=").concat(country.getCode()))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.imgFlag,new Callback() {
                        @Override
                        public void onSuccess() {
                            BitmapDrawable drawable = (BitmapDrawable) holder.imgFlag.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            mCountryViewModel.updateImage(GlobalMethod.bitMapToString(bitmap), country.getCode());
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
        holder.contentLay.setOnClickListener(view -> {

            GlobalVariable.choosenCountry = countryList.get(pos);
            countryComm.countryData(countryList.get(pos),"");

        });
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * The  Country adapter interface
     */
    public interface CountryComm{
        /**
         * Country data.
         * @param Country the country
         * @param action  the action
         */
        void countryData(Country Country, String action);
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }


    /**
     * The type View holder class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
          ImageView imgFlag;
        TextView txtName;
        LinearLayout contentLay;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            contentLay =  itemView.findViewById(R.id.contentLay);
            imgFlag =  itemView.findViewById(R.id.imgFlag);

        }
    }

}

