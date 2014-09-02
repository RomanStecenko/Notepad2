package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.adapters.ListItem;
import ua.stetsenko.Notepad2.adapters.TitlesListViewAdapter;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

import java.util.ArrayList;
import java.util.List;

public class TitleFragment extends ListFragment {
    private onSelectedListener onSelect;
    private Integer[] selectNoteImage;
    private boolean isTablet;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTablet = (getActivity().findViewById(R.id.fragmentContainer) != null);
        selectNoteImage = new Integer[]{R.drawable.text2, R.drawable.todo_list1, R.drawable.photo1, R.drawable.image1, R.drawable.drawing6, R.drawable.audio};
        updateFragment();

//        DatabaseHelper db = new DatabaseHelper(getActivity());
//        List<Note> listItems = db.getAllNotes();
//        if (listItems.size()>0){
//            List<ListItem> adapterListItems = new ArrayList<ListItem>();
//            for (Note note : listItems) {
//                int id = note.getId();
//                String content = note.getContent();
//                int type = note.getType();
//                Log.d(Constants.LOG, "onCreate() ListItem id: " + id + ", content: " + content);
//                ListItem item = new ListItem(id, selectNoteImage[type], content);
//                adapterListItems.add(item);
//            }
//            TitlesListViewAdapter adapter = new TitlesListViewAdapter(getActivity(),R.layout.title_lis_item, adapterListItems);
//            setListAdapter(adapter);
//        }
    }

    public void updateFragment(){
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
        if (isTablet) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListItem listItem = (ListItem) getListView().getItemAtPosition(position);
        if (isTablet) {
            getListView().setItemChecked(position, true);
        }
        Toast.makeText(getActivity(), "_ID " + listItem.getId() + " CONTENT: "+ listItem.getDescription(), Toast.LENGTH_LONG).show();
        onSelect.onSelectNote(listItem.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View titlesView = inflater.inflate(R.layout.title_fragment_layout, container, false);
        Button addButton = (Button) titlesView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).start();
            }
        });
        return titlesView;
    }
}
