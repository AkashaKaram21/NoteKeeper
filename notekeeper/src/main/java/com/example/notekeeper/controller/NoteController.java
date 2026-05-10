package com.example.notekeeper.controller;

import com.example.notekeeper.dto.NoteRequestDTO;
import com.example.notekeeper.dto.NoteResponseDTO;
import com.example.notekeeper.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Permite que Android se conecte sin bloqueos
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // GET /notes y GET /notes?category=Simple
    @GetMapping
    public ResponseEntity<List<NoteResponseDTO>> getNotes(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(noteService.findAll(category));
    }

    // GET /notes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNote(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.findById(id));
    }

    // POST /notes
    @PostMapping
    public ResponseEntity<NoteResponseDTO> createNote(@RequestBody NoteRequestDTO dto) {
        return new ResponseEntity<>(noteService.save(dto), HttpStatus.CREATED);
    }

    // PUT /notes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id, @RequestBody NoteRequestDTO dto) {
        return ResponseEntity.ok(noteService.update(id, dto));
    }

    // DELETE /notes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}