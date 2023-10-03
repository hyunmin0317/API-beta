package com.smunity.api.domain.petition.controller;

import com.smunity.api.domain.account.dto.UserDto;
import com.smunity.api.domain.petition.dto.CommentDto;
import com.smunity.api.domain.petition.dto.PetitionDto;
import com.smunity.api.domain.petition.service.AgreementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/petitions/{petitionId}/agreements")
public class AgreementController {
    private final AgreementService agreementService;

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getAgreementUsersByPetitionId(@PathVariable Long petitionId) {
        List<UserDto> commentDtoList = agreementService.getAgreementUsersByPetitionId(petitionId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtoList);
    }
}
