package com.hnote.listeners;

import com.hnote.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
