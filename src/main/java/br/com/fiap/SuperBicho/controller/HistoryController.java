package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.HistoryRequestDTO;
import br.com.fiap.SuperBicho.dto.response.HistoryResponseDTO;
import br.com.fiap.SuperBicho.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/history") @RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public Page<HistoryResponseDTO> findAll(Pageable pageable) {
        return historyService.findAll(pageable); }

    @GetMapping("/{id}")
    public HistoryResponseDTO findById(@PathVariable Integer id) {
        return historyService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoryResponseDTO create(@Valid @RequestBody HistoryRequestDTO dto) {
        return historyService.create(dto); }

    @PutMapping("/{id}")
    public HistoryResponseDTO update(@PathVariable Integer id, @Valid @RequestBody HistoryRequestDTO dto) {
        return historyService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        historyService.deleteById(id); }
}