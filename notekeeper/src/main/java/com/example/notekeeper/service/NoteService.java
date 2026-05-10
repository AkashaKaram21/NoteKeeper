package com.example.notekeeper.service;

import com.example.notekeeper.dto.NoteRequestDTO;
import com.example.notekeeper.dto.NoteResponseDTO;
import com.example.notekeeper.model.Category;
import com.example.notekeeper.model.Note;
import com.example.notekeeper.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    // Inyección por constructor (Recomendado en el PDF)
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<NoteResponseDTO> findAll(String categoryFilter) {
        Iterable<Note> notes;
        if (categoryFilter != null) {
            notes = noteRepository.findByCategory(Category.valueOf(categoryFilter.toUpperCase()));
        } else {
            notes = noteRepository.findAll();
        }
        
        List<NoteResponseDTO> dtos = new ArrayList<>();
        notes.forEach(n -> dtos.add(mapToDTO(n)));
        return dtos;
    }

    public NoteResponseDTO findById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow();
        return mapToDTO(note);
    }

    public NoteResponseDTO save(NoteRequestDTO dto) {
        Note note = new Note();
        updateEntity(note, dto);
        return mapToDTO(noteRepository.save(note));
    }

    public NoteResponseDTO update(Long id, NoteRequestDTO dto) {
        Note note = noteRepository.findById(id).orElseThrow();
        updateEntity(note, dto);
        return mapToDTO(noteRepository.save(note));
    }

    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    // Mapeo Manual (Sigue la lógica del PDF)
    private NoteResponseDTO mapToDTO(Note note) {
        NoteResponseDTO dto = new NoteResponseDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setSubtitle(note.getSubtitle());
        dto.setText(note.getText());
        dto.setCategory(note.getCategory().name());
        return dto;
    }

    private void updateEntity(Note note, NoteRequestDTO dto) {
        note.setTitle(dto.getTitle());
        note.setSubtitle(dto.getSubtitle());
        note.setText(dto.getText());
        note.setCategory(Category.valueOf(dto.getCategory().toUpperCase()));
    }
}