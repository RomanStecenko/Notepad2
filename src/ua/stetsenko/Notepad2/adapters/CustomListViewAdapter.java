package ua.stetsenko.Notepad2.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ua.stetsenko.Notepad2.R;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter<ListItem> {

    private Context context;

    public CustomListViewAdapter(Context context, int resourceId, List<ListItem> list) {
        super(context, resourceId, list);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtDesc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ListItem listItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.listItemImageView);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.listItemTextView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(listItem.getDescription());
        holder.imageView.setImageResource(listItem.getImageId());

        return convertView;
    }
}
