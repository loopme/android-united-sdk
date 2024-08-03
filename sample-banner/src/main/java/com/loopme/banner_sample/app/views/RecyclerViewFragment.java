package com.loopme.banner_sample.app.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.LoopMeBanner;
import com.loopme.NativeVideoBinder;
import com.loopme.NativeVideoRecyclerAdapter;
import com.loopme.banner_sample.R;
import com.loopme.banner_sample.app.model.CustomListItem;
import com.loopme.banner_sample.app.model.CustomRecyclerViewAdapter;
import com.loopme.common.LoopMeError;

import java.util.ArrayList;

public class RecyclerViewFragment extends Fragment {
    private NativeVideoRecyclerAdapter fragmentAdapter;

    public static RecyclerViewFragment newInstance() { return new RecyclerViewFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview_fragment_layout, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(getContext(), getCustomListItem());
        fragmentAdapter = new NativeVideoRecyclerAdapter(adapter, getActivity(), recyclerView);
        fragmentAdapter.putAdWithAppKeyToPosition("f5826542ae", 2);
        NativeVideoBinder binder = new NativeVideoBinder
            .Builder(R.layout.ad_banner_view)
            .setLoopMeBannerViewId(R.id.loop_me_banner_view)
            .build();
        fragmentAdapter.setViewBinder(binder);
        recyclerView.setAdapter(fragmentAdapter);
        fragmentAdapter.setListener(new LoopMeBanner.Listener() {
            @Override
            public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                Toast.makeText(getContext(), "Ad Loaded", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLoopMeBannerShow(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerHide(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerClicked(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerLeaveApp(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerExpired(LoopMeBanner banner) { }
        });
        fragmentAdapter.loadAds();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (fragmentAdapter != null) {
            fragmentAdapter.onPause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (fragmentAdapter != null) {
            fragmentAdapter.onResume();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragmentAdapter != null) {
            fragmentAdapter.destroy();
        }
    }
    private ArrayList<CustomListItem> getCustomListItem() {
        ArrayList<CustomListItem> listItems = new ArrayList<>();
        listItems.add(new CustomListItem("THE GREY (2012)", "Liam Neeson", R.drawable.poster1));
        listItems.add(new CustomListItem("MAN OF STEEL (2013)", "Henry Cavill, Amy Adams", R.drawable.poster2));
        listItems.add(new CustomListItem("AVATAR (2009)", "Sam Worthington", R.drawable.poster3));
        listItems.add(new CustomListItem("SHERLOCK HOLMES (2009)", "Robert Downey, Jr.", R.drawable.poster4));
        listItems.add(new CustomListItem("ALICE IN WONDERLAND (2010)", "Johnny Depp", R.drawable.poster5));
        listItems.add(new CustomListItem("INCEPTION (2010)", "Leonardo DiCaprio", R.drawable.poster6));
        listItems.add(new CustomListItem("THE SILENCE OF THE LAMBS (1991)", "Jodie Foster", R.drawable.poster7));
        listItems.add(new CustomListItem("MR. POPPER'S PENGUINS (2011)", "Jim Carrey", R.drawable.poster8));
        listItems.add(new CustomListItem("LAST EXORCISM (2010)", "Patrick Fabian", R.drawable.poster9));
        listItems.add(new CustomListItem("BRAVE (2012)", "Kelly Macdonald", R.drawable.poster10));
        listItems.add(new CustomListItem("THOR (2011)", "Chris Hemsworth", R.drawable.poster11));
        listItems.add(new CustomListItem("PAIN & GAIN (2013)", "Mark Wahlberg", R.drawable.poster12));
        listItems.add(new CustomListItem("127 HOURS (2010)", "James Franco", R.drawable.poster13));
        listItems.add(new CustomListItem("PAUL (2011)", "Seth Rogens", R.drawable.poster14));
        return listItems;
    }
}
