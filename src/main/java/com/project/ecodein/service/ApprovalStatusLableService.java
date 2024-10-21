package com.project.ecodein.service;

import com.project.ecodein.dto.ApprovalStatusLableDTO;
import com.project.ecodein.entity.ApprovalStatusLable;
import com.project.ecodein.repository.ApprovalStatusLableRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ApprovalStatusLableService {

    private final ApprovalStatusLableRepository APPROVAL_STATUS_LABLE_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;

    public ApprovalStatusLableService (ApprovalStatusLableRepository approvalStatusLableRepository,
                                       ModelMapper modelMapper) {
        this.APPROVAL_STATUS_LABLE_REPOSITORY = approvalStatusLableRepository;
        this.MODEL_MAPPER = modelMapper;
    }

    public Page<ApprovalStatusLableDTO> getApprovalStatusLables (int page, byte status) {
        Pageable pageable = PageRequest.of(page - 1, 10);

        Page<ApprovalStatusLable> approvalStatusLables = APPROVAL_STATUS_LABLE_REPOSITORY.findByStatus(status, pageable);
        return approvalStatusLables.map(approvalStatusLable -> MODEL_MAPPER.map(approvalStatusLable, ApprovalStatusLableDTO.class));
    }
}
