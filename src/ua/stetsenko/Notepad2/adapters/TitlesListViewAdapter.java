package ua.stetsenko.Notepad2.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;

import java.util.List;

public class TitlesListViewAdapter extends ArrayAdapter<ListItem> {
    private Context context;

    public TitlesListViewAdapter(Context context, int resourceId, List<ListItem> list) {
        super(context, resourceId, list);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtDesc;
        Button deleteButton;
        TextView dateTimeTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ListItem listItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.title_lis_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.titleListItemImageView);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.titleListItemTextView);
            holder.deleteButton = (Button) convertView.findViewById(R.id.deleteListItemButton);
            holder.dateTimeTextView = (TextView) convertView.findViewById(R.id.dateTimeTextView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDesc.setText(listItem.getDescription());
        holder.imageView.setImageResource(listItem.getImageId());
        holder.dateTimeTextView.setText(listItem.getDateTime());
        final ViewHolder finalHolder = holder;
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(context);
                int result = db.deleteNote(listItem.getId());
                Toast.makeText(context, "Remove note, id: " + listItem.getId()+ " delete result: "+ result, Toast.LENGTH_SHORT).show();
                finalHolder.txtDesc.setVisibility(View.GONE);
                finalHolder.dateTimeTextView.setVisibility(View.GONE);
                finalHolder.imageView.setVisibility(View.GONE);
                finalHolder.deleteButton.setVisibility(View.GONE);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.updateDetailFragment();
//                mainActivity.highlightFirstRowInTitlesFragment();
            }
        });

        return convertView;
    }

}
