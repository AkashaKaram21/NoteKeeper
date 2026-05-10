package com.example.notekeeper.repository;

import com.example.notekeeper.model.Category;
import com.example.notekeeper.model.Note;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NoteRepository extends CrudRepository<Note, Long> {
    // Busca automáticamente por el campo category de la entidad
    List<Note> findByCategory(Category category);
}