package hocc.fun.forget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import kotlinx.coroutines.scheduling.Task;

public class TaskViewAdapter extends RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder> {

    private final List<TaskItem> dataList;

    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        // We pass the item and return a boolean (true means the event is consumed)
        boolean onItemLongClick(View view, TaskItem item, int position);
    }

    public TaskViewAdapter(List<TaskItem> dataList, OnItemLongClickListener listener) {
        this.dataList = dataList;
        this.longClickListener = listener;
    }

    // --- 1. The ViewHolder: Holds the view references and click logic ---
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView TaskTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            TaskTextView = itemView.findViewById(R.id.TaskString);

            // Set the Long Click listener on the entire item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Delegate the long click to the interface method
                        return longClickListener.onItemLongClick(v, dataList.get(position), position);
                    }
                    // Return false if position is invalid
                    return false;
                }
            });
        }
    }

    // --- 2. onCreateViewHolder: Called when RecyclerView needs a new ViewHolder ---
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (list_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    // --- 3. onBindViewHolder: Binds the data to the views in the ViewHolder ---
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItem currentItem = dataList.get(position);
        holder.TaskTextView.setText(currentItem.getTaskString());
    }

    // --- 4. getItemCount: Returns the total number of items ---
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}