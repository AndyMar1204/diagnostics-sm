package com.andy.gstockapi.controller;

import com.andy.gstockapi.dto.ReturnRequest;
import com.andy.gstockapi.dto.ReturnResponse;
import com.andy.gstockapi.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ResponseEntity<ReturnResponse> createReturn(@Valid @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    @GetMapping
    public ResponseEntity<List<ReturnResponse>> getAllReturns() {
        return ResponseEntity.ok(returnService.getAllReturns());
    }
}
