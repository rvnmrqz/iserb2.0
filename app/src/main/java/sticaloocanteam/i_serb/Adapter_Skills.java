package sticaloocanteam.i_serb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;



public class Adapter_Skills extends RecyclerView.Adapter<Adapter_Skills.ViewHolder> {

    Context context;
    List<Object_Skills> object_skills_list;

    public Adapter_Skills(Context context, List<Object_Skills> object_skills_list) {
        this.context = context;
        this.object_skills_list = object_skills_list;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_worker_skills, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object_Skills object_skills = object_skills_list.get(position);
        holder.txtSkillId.setText(object_skills.getSkill_id());
        holder.txtSkillName.setText(object_skills.getSkill_name());
        holder.txtSkillServiceFee.setText("P"+object_skills.getService_fee()+"/service");


        Glide.with(context)
                .load(object_skills.getIconURL())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return object_skills_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtSkillId;
        public ImageView iconImageView;
        public TextView txtSkillName;
        public TextView txtSkillServiceFee;

        public ViewHolder(View itemView) {
            super(itemView);

            txtSkillId = itemView.findViewById(R.id.txtskill_id);
            iconImageView = itemView.findViewById(R.id.img_skill_icon);
            txtSkillName = itemView.findViewById(R.id.txtSkill_name);
            txtSkillServiceFee = itemView.findViewById(R.id.txtSkill_service_fee);
        }
    }


}
