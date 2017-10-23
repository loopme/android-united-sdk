package com.loopme.tester.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.MainActivity;
import com.loopme.tester.ui.dialog.RemoveAdSpotDialogFragment;
import com.loopme.tester.ui.fragment.screen.EditAdSpotFragment;
import com.loopme.tester.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 2/12/17.
 */

public class AdSpotAdapter extends RecyclerView.Adapter<AdSpotAdapter.ViewHolder> {

    private static final String REMOVE_ADSPOT_FRAGMENT = "REMOVE_ADSPOT_FRAGMENT";
    private final List<AdSpot> mAdSpotList = new ArrayList<>();
    private final AdAdapterCallback mCallback;
    private Context mContext;

    public AdSpotAdapter(Context context, List<AdSpot> adSpotList, AdAdapterCallback callback) {
        this.mCallback = callback;
        this.mContext = context;
        if (adSpotList != null) {
            this.mAdSpotList.addAll(adSpotList);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_ad_spot_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final AdSpot adSpot = mAdSpotList.get(position);

        viewHolder.name.setText(adSpot.getName() + " " + adSpot.getType().toString().toLowerCase());
        viewHolder.appKey.setText(adSpot.getAppKey());
        viewHolder.icon.setImageResource(UiUtils.getSdkTypeIcon(adSpot.getSdk()));
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askRemoveItem(adSpot);
            }
        });
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelected(adSpot);
            }
        });
    }

    private void onItemSelected(AdSpot adSpot) {
        if (mCallback != null) {
            mCallback.onItemSelected(adSpot);
        }
    }

    private void onDelete(AdSpot adSpot) {
        if (mCallback != null) {
            mCallback.onDelete(adSpot);
        }
    }

    private void askRemoveItem(final AdSpot adSpot) {
        Bundle args = new Bundle();
        args.putParcelable(EditAdSpotFragment.ARG_AD_SPOT, adSpot);

        RemoveAdSpotDialogFragment removeFragment = RemoveAdSpotDialogFragment.newInstance(args);
        if (mContext instanceof MainActivity) {
            removeFragment.show(((MainActivity) mContext).getSupportFragmentManager(), REMOVE_ADSPOT_FRAGMENT);
            removeFragment.setListener(new RemoveAdSpotDialogFragment.RemoveAdSpotDialogFragmentListener() {
                @Override
                public void onRemoveAdSpot(AdSpot adSpot) {
                    removeAdSpot(adSpot);
                }
            });
        }
    }

    private void removeAdSpot(AdSpot adSpot) {
        onDelete(adSpot);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mAdSpotList != null) {
            return mAdSpotList.size();
        } else {
            return 0;
        }
    }

    public void setList(List<AdSpot> adSpotList) {
        if (adSpotList != null) {
            mAdSpotList.clear();
            mAdSpotList.addAll(adSpotList);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item;
        public TextView name;
        public TextView appKey;
        public ImageView icon;
        public ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (RelativeLayout) itemView.findViewById(R.id.ad_spot_item);
            name = (TextView) itemView.findViewById(R.id.ad_spot_name);
            appKey = (TextView) itemView.findViewById(R.id.ad_spot_appkey);
            icon = (ImageView) itemView.findViewById(R.id.fragment_adspot_card_sdk_icon);
            delete = (ImageView) itemView.findViewById(R.id.fragment_adspot_card_sdk_icon_delete_button);

        }
    }

    public interface AdAdapterCallback {
        void onDelete(AdSpot adSpot);

        void onItemSelected(AdSpot adSpot);
    }
}
