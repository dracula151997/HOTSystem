package arduino.semicolon.com.arduino.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import arduino.semicolon.com.arduino.R;
import arduino.semicolon.com.arduino.database.DatabaseClient;
import arduino.semicolon.com.arduino.database.DatabaseEntity;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {
    private Context context;
    private List<DatabaseEntity> dataSet;
    private Activity activity;

    private OnItemClickedListener onItemClickedListener;

    public PatientAdapter(Context context, Activity activity, List<DatabaseEntity> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
        this.activity = activity;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DatabaseEntity entity = dataSet.get(position);
        holder.name.setText(entity.getName());
        holder.age.setText(entity.getAge());


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.remove(entity);
                notifyItemRemoved(position);

                new DeletePatient(entity)
                        .execute();
            }
        });



    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, age;
        ImageButton remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.patient_name_list_item);
            age = itemView.findViewById(R.id.patient_age_list_item);
            remove = itemView.findViewById(R.id.remove_button);


        }
    }

    public class DeletePatient extends AsyncTask<Void, Void, Void> {
        private DatabaseEntity entity;

        public DeletePatient(DatabaseEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseClient.getInstance(context)
                    .getAppDatabase().databaseDao().delete(entity);
            return null;
        }
    }

    public void clear() {
        dataSet.clear();
        notifyDataSetChanged();
    }


}
