package br.com.fiap.SuperBicho.controller;

import br.com.fiap.SuperBicho.dto.request.CheckUpRequestDTO;
import br.com.fiap.SuperBicho.dto.response.CheckUpResponseDTO;
import br.com.fiap.SuperBicho.service.CheckUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/check-ups") @RequiredArgsConstructor
public class CheckUpController {

    private final CheckUpService checkUpService;

    @GetMapping
    public Page<CheckUpResponseDTO> findAll(Pageable pageable) {
        return checkUpService.findAll(pageable); }

    @GetMapping("/{id}")
    public CheckUpResponseDTO findById(@PathVariable Integer id) {
        return checkUpService.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckUpResponseDTO create(@Valid @RequestBody CheckUpRequestDTO dto) {
        return checkUpService.create(dto); }

    @PutMapping("/{id}")
    public CheckUpResponseDTO update(@PathVariable Integer id, @Valid @RequestBody CheckUpRequestDTO dto) {
        return checkUpService.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        checkUpService.deleteById(id); }
}