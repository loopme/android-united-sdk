package com.loopme.tester.ui.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.model.FileModel;
import com.loopme.tester.utils.UiUtils;

import java.util.ArrayList;


public class FileModelListAdapter extends RecyclerView.Adapter<FileModelListAdapter.ViewHolder> {

    private static final String LOG_TAG = FileModelListAdapter.class.getSimpleName();
    private final ArrayList<FileModel> mFileModelArrayList = new ArrayList<>();
    private final FileModelListAdapterCallback mCallback;
    private int mSelectedPosition = -1;

    public FileModelListAdapter(ArrayList<FileModel> fileModelsList, FileModelListAdapterCallback callback) {
        this.mCallback = callback;
        if (fileModelsList != null) {
            mFileModelArrayList.addAll(fileModelsList);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_file_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final FileModel fileModel = mFileModelArrayList.get(position);
        viewHolder.fileImage.setImageResource(UiUtils.getSpecificFileImageResource(fileModel.getFileType()));
        viewHolder.fileNameText.setText(fileModel.getFileName());

        setSelected(viewHolder.getAdapterPosition(), viewHolder.fileNameText);
        removeDelimiter(viewHolder.getAdapterPosition(), viewHolder.lineDelimiter);

        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
                onItemClicked(fileModel);
            }
        });
    }

    private void removeDelimiter(int position, View delimiter) {
        if (position == 0) {
            delimiter.setVisibility(View.GONE);
        }
    }

    private void setSelected(int position, TextView textView) {
        if (position == mSelectedPosition) {
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        } else {
            textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
        }
    }

    private void onItemClicked(FileModel fileModel) {
        if (mCallback != null) {
            mCallback.onFileItemSelected(fileModel);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mFileModelArrayList != null) {
            return mFileModelArrayList.size();
        } else {
            return 0;
        }
    }

    public void setList(ArrayList<FileModel> mSubDirectories) {
        if (mFileModelArrayList != null) {
            mFileModelArrayList.clear();
            mFileModelArrayList.addAll(mSubDirectories);
        }
        mSelectedPosition = -1;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public ImageView fileImage;
        public TextView fileNameText;
        public View lineDelimiter;

        public ViewHolder(View itemView) {
            super(itemView);
            lineDelimiter = itemView.findViewById(R.id.line_view);
            root = (CardView) itemView.findViewById(R.id.fragment_file_list_item);
            fileImage = (ImageView) itemView.findViewById(R.id.fragment_file_list_item_image);
            fileNameText = (TextView) itemView.findViewById(R.id.fragment_file_list_item_text);
        }
    }

    public interface FileModelListAdapterCallback {
        void onFileItemSelected(FileModel file);
    }
}
