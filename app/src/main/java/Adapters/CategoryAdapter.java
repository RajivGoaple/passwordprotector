package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.rajiv.passwordprotector.R;

import java.util.ArrayList;

import Viewmodels.CategoryViewModel;

/**
 * Created by prachi on 8/27/2015.
 */
public class CategoryAdapter extends ArrayAdapter<CategoryViewModel> {


    public CategoryAdapter(Context context, int resource,ArrayList<CategoryViewModel> items) {
        super(context, resource,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CategoryViewModel user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passwordlist, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.tvName);
        // Populate the data into the template view using the data object

        name.setText(user.name);
        // Return the completed view to render on screen
        return convertView;
    }

}

