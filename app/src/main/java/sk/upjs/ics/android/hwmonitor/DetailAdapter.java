package sk.upjs.ics.android.hwmonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chras on 20.06.2016.
 * https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
 */
public class DetailAdapter extends ArrayAdapter<DoubleListItem>{
    public DetailAdapter(Context context, ArrayList<DoubleListItem> doubleListItems) {
        super(context, 0, doubleListItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DoubleListItem doubleListItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.details, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.detailMeno);
        TextView tvHome = (TextView) convertView.findViewById(R.id.detailHodnota);
        // Populate the data into the template view using the data object
        tvName.setText(doubleListItem.getMeno());
        tvHome.setText(doubleListItem.getHodnota().replaceAll("PSH ", ""));
        // Return the completed view to render on screen
        return convertView;
    }

}
