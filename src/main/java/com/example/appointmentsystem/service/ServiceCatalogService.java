package com.example.appointmentsystem.service;

import com.example.appointmentsystem.dto.CreateServiceRequest;
import com.example.appointmentsystem.entity.Role;
import com.example.appointmentsystem.exception.BusinessException;
import com.example.appointmentsystem.repository.ServiceRepository;
import com.example.appointmentsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public List<com.example.appointmentsystem.entity.Service> listAll() {
        return serviceRepository.findAll();
    }

    @Transactional
    public com.example.appointmentsystem.entity.Service create(CreateServiceRequest request) {
        var staff = resolveStaff(request.staffId());
        com.example.appointmentsystem.entity.Service service = com.example.appointmentsystem.entity.Service.builder()
                .name(request.name())
                .durationMinutes(request.durationMinutes())
                .price(request.price())
                .staff(staff)
                .build();
        return serviceRepository.save(service);
    }

    @Transactional
    public com.example.appointmentsystem.entity.Service update(Long id, CreateServiceRequest request) {
        var existing = serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service not found"));
        var staff = resolveStaff(request.staffId());
        existing.setName(request.name());
        existing.setDurationMinutes(request.durationMinutes());
        existing.setPrice(request.price());
        existing.setStaff(staff);
        return serviceRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new BusinessException("Service not found");
        }
        serviceRepository.deleteById(id);
    }

    private com.example.appointmentsystem.entity.User resolveStaff(Long staffId) {
        if (staffId == null) {
            throw new BusinessException("Staff is required");
        }
        var staff = userRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("Staff not found"));
        if (staff.getRole() != Role.STAFF) {
            throw new BusinessException("Assigned staff must have STAFF role");
        }
        return staff;
    }
}
