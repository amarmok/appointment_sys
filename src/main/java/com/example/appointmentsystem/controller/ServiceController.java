package com.example.appointmentsystem.controller;

import com.example.appointmentsystem.dto.CreateServiceRequest;
import com.example.appointmentsystem.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public List<com.example.appointmentsystem.entity.Service> list() {
        return serviceCatalogService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public com.example.appointmentsystem.entity.Service create(
            @Valid @RequestBody CreateServiceRequest request
    ) {
        return serviceCatalogService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public com.example.appointmentsystem.entity.Service update(
            @PathVariable Long id,
            @Valid @RequestBody CreateServiceRequest request
    ) {
        return serviceCatalogService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        serviceCatalogService.delete(id);
    }
}
