package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import ua.stetsenko.Notepad2.adapters.CustomListViewAdapter;
import ua.stetsenko.Notepad2.adapters.ListItem;
import ua.stetsenko.Notepad2.R;

import java.util.ArrayList;
import java.util.List;


public class NoteTypeDialog extends DialogFragment {


    private onSelectTypeNoteListener onSelectTypeNote;

    public interface onSelectTypeNoteListener {
        public void onSelectTypeNote(int position);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onSelectTypeNote = (onSelectTypeNoteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSelectTypeNoteListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] selectNoteDescription = getResources().getStringArray(R.array.selectNoteArray);
        Integer[] selectNoteImage = new Integer[]{R.drawable.text2, R.drawable.todo_list1, R.drawable.photo1, R.drawable.image1, R.drawable.drawing6, R.drawable.audio};

        AlertDialog.Builder selectNoteType = new AlertDialog.Builder(getActivity());
        selectNoteType.setTitle(R.string.selectNoteType);

        List<ListItem> listItems = new ArrayList<ListItem>();
        for (int i = 0; i < selectNoteDescription.length; i++) {
            ListItem item = new ListItem(selectNoteImage[i], selectNoteDescription[i]);
            listItems.add(item);
        }
        CustomListViewAdapter adapter = new CustomListViewAdapter(getActivity(),R.layout.list_item, listItems);
        selectNoteType.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSelectTypeNote.onSelectTypeNote(which);
            }
        });
        return selectNoteType.create();
    }
}
