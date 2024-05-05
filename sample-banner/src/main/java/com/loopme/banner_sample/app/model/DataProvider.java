package com.loopme.banner_sample.app.model;

import com.loopme.banner_sample.R;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {
    public static List<String> getSimpleStringsList() {
        List<String> dataList = new ArrayList<>();
        dataList.add(Constants.SIMPLE);
        dataList.add(Constants.RECYCLERVIEW);
        dataList.add(Constants.RECYCLERVIEW_SHRINK);
        return dataList;
    }


    public static ArrayList<CustomListItem> getCustomListItem() {
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
