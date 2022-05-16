package ke.co.eclectic.quickstore.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import ke.co.eclectic.quickstore.models.AppMenu;
import ke.co.eclectic.quickstore.models.Rule;

/**
 * Created by Manduku O. David on 21/12/2018.
 */
public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private  boolean isCurrentUser;
    List<Rule> singleList;
    Context context;
    RuleComm ruleComm;

    /**
     * Instantiates a new Menu adapter.
     *
     * @param context    the context
     * @param isCurrentUser    flag to indicate if current user is viewing his/her profile
     * @param singleList the single list
     */
    public RuleAdapter(Context context, List<Rule> singleList,boolean isCurrentUser) {
        this.singleList = singleList;
        this.context = context;
        this.isCurrentUser = isCurrentUser;
        this.ruleComm= (RuleComm) context;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleAdapter.ViewHolder holder, int position) {
        final int pos = position;


        holder.txtRuleName.setText(singleList.get(pos).getRulename());
        holder.cbCreate.setChecked(singleList.get(pos).isCancreate());
        holder.cbView.setChecked(singleList.get(pos).isCanview());
        holder.cbDelete.setChecked(singleList.get(pos).isCandelete());

        holder.cbCreate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            singleList.get(pos).setCancreate(isChecked);
            ruleComm.ruleMessage(singleList.get(pos));
        });
        holder.cbView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            singleList.get(pos).setCanview(isChecked);
            ruleComm.ruleMessage(singleList.get(pos));
        });

        holder.cbDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            singleList.get(pos).setCandelete(isChecked);
            ruleComm.ruleMessage(singleList.get(pos));
        });
        if(isCurrentUser){
            holder.cbCreate.setEnabled(false);
            holder.cbView.setEnabled(false);
            holder.cbDelete.setEnabled(false);
        }

    }

    /**
     * The  Menu adapter interface.
     */
    public interface RuleComm{
        /**
         * Str message.
         *
         * @param rule  changed rule
         */
        void ruleMessage(Rule rule);
    }

    @Override
    public int getItemCount() {
        return singleList.size();
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRuleName;
        LinearLayout contentLay;
        CheckBox cbCreate,cbView,cbDelete;

        /**
         * Instantiates a new View holder.
         *
         * @param itemView the item view
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtRuleName = itemView.findViewById(R.id.txtRuleName);
            cbCreate = itemView.findViewById(R.id.cbCreate);
            cbView = itemView.findViewById(R.id.cbView);
            cbDelete = itemView.findViewById(R.id.cbDelete);
            contentLay =  itemView.findViewById(R.id.contentLay);

        }
    }

}

