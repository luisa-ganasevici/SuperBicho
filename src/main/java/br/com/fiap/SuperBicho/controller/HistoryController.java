package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.entity.History;
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
    public Page<History> findAll(Pageable pageable) {
        return historyService.findAll(pageable); }

    @GetMapping("/{id}")
    public History findById(@PathVariable Integer id) {
        return historyService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) public History create(@Valid @RequestBody History history) {
        return historyService.create(history); }

    @PutMapping("/{id}")
    public History update(@PathVariable Integer id, @Valid @RequestBody History history) {
        return historyService.update(id, history); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteById(@PathVariable Integer id) {
        historyService.deleteById(id); }
}
