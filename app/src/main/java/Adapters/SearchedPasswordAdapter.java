package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projects.rajiv.passwordprotector.R;

import java.util.ArrayList;

import Viewmodels.PasswordViewModel;

/**
 * Created by prachi on 8/31/2015.
 */
public class SearchedPasswordAdapter extends ArrayAdapter<PasswordViewModel> {
    public SearchedPasswordAdapter(Context context, int resource, ArrayList<PasswordViewModel> items) {
        super(context, resource,items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PasswordViewModel user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.searched_password_list, parent, false);
        }
        // Lookup view for data population
        TextView password = (TextView) convertView.findViewById(R.id.Password);
        // Populate the data into the template view using the data object
        password.setText(user.Description);

        TextView category = (TextView) convertView.findViewById(R.id.Cateogry);
        // Populate the data into the template view using the data object
        category.setText(user.CategoryName);



        // Return the completed view to render on screen
        return convertView;
    }
}
