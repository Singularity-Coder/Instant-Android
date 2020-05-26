package com.singularitycoder.firebasestorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FileItem> fileList;
    private Context context;
    private OnFileItemClick onFileItemClick;

    public FilesAdapter(List<FileItem> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FileItem fileItem = fileList.get(position);
        if (holder instanceof FileViewHolder) {
            FileViewHolder fileHolder = (FileViewHolder) holder;
            fileHolder.tvFileType.setText(fileItem.getFileType());
            fileHolder.tvFileName.setText(fileItem.getFileName());
            fileHolder.tvTimeAdded.setText(fileItem.getTimeAdded());
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    interface OnFileItemClick {
        void onItemClick(String url);
    }

    public void setOnFileItemClick(OnFileItemClick onFileItemClick) {
        this.onFileItemClick = onFileItemClick;
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileType, tvFileName, tvTimeAdded;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileType = itemView.findViewById(R.id.tv_file_type);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvTimeAdded = itemView.findViewById(R.id.tv_time_added);

            itemView.setOnClickListener(view -> onFileItemClick.onItemClick(fileList.get(getAdapterPosition()).getFileUrl()));
        }
    }
}