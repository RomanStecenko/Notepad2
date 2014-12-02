package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.adapters.ListItem;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

import java.util.ArrayList;
import java.util.List;

public class TitleFragment extends ListFragment {
    private onSelectedListener onSelect;
    private Integer[] selectNoteImage;
    private boolean isTablet, firstTimeStartup = true;
    private View currentSelectedView;
    private int positionToHighlight=0;

//    public void setPositionToHighlight(int positionToHighlight) {
//        this.positionToHighlight = positionToHighlight;
//    }


    public interface onSelectedListener {
        public void onSelectNote(int noteId);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onSelect = (onSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSelectedListener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View titlesView = inflater.inflate(R.layout.title_fragment_layout, container, false);
        (titlesView.findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).start();
            }
        });
        return titlesView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTablet = (getActivity().findViewById(R.id.fragmentContainer) != null);
        selectNoteImage = new Integer[]{R.drawable.text2, R.drawable.todo_list1, R.drawable.photo1, R.drawable.image1, R.drawable.drawing6, R.drawable.audio};
        updateFragment();

//        if (savedInstanceState != null){
//            firstTimeStartup = savedInstanceState.getBoolean("firstTimeStartup");
//            positionToHighlight = savedInstanceState.getInt("positionToHighlight");
//            Log.d(Constants.LOG, "onActivityCreated() firstTimeStartup: " +firstTimeStartup + " positionToHighlight: " + positionToHighlight );
//        }

//        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        String[] values = {};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, values);


//        highlightRow(getListView().getChildAt(positionToHighlight));
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setSelector(getResources().getDrawable(R.drawable.selected_item));
    }

    public void updateFragment(){
        Log.d(Constants.LOG, "UPDATE TITLES");
        DatabaseHelper db = new DatabaseHelper(getActivity());
        List<Note> listItems = db.getAllNotes();
        TitlesListViewAdapter adapter = null;
        if (listItems.size()>0){
            List<ListItem> adapterListItems = new ArrayList<ListItem>();
            for (Note note : listItems) {
                Log.d(Constants.LOG, "updateTitleFragment() ListItem id: " + note.getId() + ", content: " + note.getContent() + " type: "+ note.getType());
                ListItem item = new ListItem(note.getId(), selectNoteImage[note.getType()], note.getContent(), note.getDateTime());
                adapterListItems.add(item);
            }
            adapter = new TitlesListViewAdapter(getActivity(),R.layout.title_lis_item, adapterListItems);
        }
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!firstTimeStartup){
//            unhighlightCurrentRow();
//            View firstAdapterView = getAdapter().getView(0, null, getListView());
//            View adapterView = getAdapter().getView(positionToHighlight, null, getListView());
//            highlightRow(adapterView);
//            Log.d(Constants.LOG, "onActivityCreated() firstTimeStartup false, try to highlight view in position: " + positionToHighlight );
//        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
//        if (isTablet){
//            if (firstTimeStartup) {
//                currentSelectedView = l.getChildAt(0);
//            }
//            Log.d(Constants.LOG, "onActivityCreated() firstTimeStartup: was " +firstTimeStartup + " now, it is false" );
//            firstTimeStartup = false;
//            if (currentSelectedView != null && currentSelectedView != v) {
//                unhighlightRow(currentSelectedView);
//            }
//            currentSelectedView = v;
//            highlightRow(currentSelectedView);
//            positionToHighlight = position;
//        }

        ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
        Toast.makeText(getActivity(), "_ID " + listItem.getId() + " CONTENT: "+ listItem.getDescription().substring(0, Math.min(listItem.getDescription().length(), 20)) + "    count: " +getListView().getCount() + " child count: " +getListView().getChildCount(), Toast.LENGTH_LONG).show();
        onSelect.onSelectNote(listItem.getId());
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("firstTimeStartup", firstTimeStartup);
//        outState.putInt("positionToHighlight", positionToHighlight);
//        Log.d(Constants.LOG, "titleFragment onSaveInstanceState() firstTimeStartup: " +firstTimeStartup + " positionToHighlight: " + positionToHighlight );
//    }

//    public void unhighlightCurrentRow() {
//        if (currentSelectedView != null) {
//            currentSelectedView.setBackgroundColor(Color.TRANSPARENT);
//        }
//    }

//    private void unhighlightRow(View rowView) {
//        rowView.setBackgroundColor(Color.TRANSPARENT);
//    }
//
//    private void highlightRow(View rowView) {
//        rowView.setBackgroundColor(getResources().getColor(R.color.dark_orange));
//    }

//    public void highlightLastRow(){
//        int myChildCount = getListView().getChildCount();
//        int myCount = getListView().getCount()-1;
//        View v1 = getListView().getChildAt(0);
//        highlightRow(v2);
//    }

//    public void highlightFirstRow(){
//        highlightRow(getListView().getChildAt(0));
//    }


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
            ViewHolder holder;
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

//            if (firstTimeStartup && position == 0) {
//                highlightRow(convertView);
//            } else {
//                unhighlightRow(convertView);
//            }
//
//            if (positionToHighlight == position){
//                highlightRow(convertView);
//            }

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
                }
            });

            return convertView;
        }

    }

}
