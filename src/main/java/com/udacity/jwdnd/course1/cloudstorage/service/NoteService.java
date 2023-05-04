package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public Note getNoteById(int noteId){
        return noteMapper.getNoteById(noteId);
    }

    public List<Note> getAllNotesByUserId(int userId){
        return noteMapper.getAllNotesByUserId(userId);
    }

    public Note createNote(User user,String title, String description){
        Note note = null;
        note = new Note(
                null,
                title,
                description,
                user.getUserId()
        );
        return note;
    }

    public Note createNote(User user,Integer id, String title, String description){
        Note note = null;
        note = new Note(
                id,
                title,
                description,
                user.getUserId()
        );
        return note;
    }

    public boolean insertNote(Note note){
        int rowsInserted = noteMapper.insert(note);
        return (rowsInserted > 0);
    }

    public boolean updateNote(Note note){
        int rowsUpdated = noteMapper.update(note);
        return (rowsUpdated > 0);
    }

    public boolean deleteNote(int noteId){
        int rowsDeleted = noteMapper.delete(noteId);
        return (rowsDeleted > 0);
    }



}
