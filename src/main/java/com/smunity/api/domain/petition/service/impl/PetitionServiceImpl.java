package com.smunity.api.domain.petition.service.impl;

import com.smunity.api.domain.account.domain.User;
import com.smunity.api.domain.account.repository.UserRepository;
import com.smunity.api.domain.petition.domain.Petition;
import com.smunity.api.domain.petition.dto.PetitionDto;
import com.smunity.api.domain.petition.repository.PetitionRepository;
import com.smunity.api.domain.petition.service.PetitionService;
import com.smunity.api.global.config.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PetitionServiceImpl implements PetitionService {
    public JwtTokenProvider jwtTokenProvider;
    public UserRepository userRepository;
    private PetitionRepository petitionRepository;

    @Autowired
    public PetitionServiceImpl(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PetitionRepository petitionRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.petitionRepository = petitionRepository;
    }

    @Override
    public List<PetitionDto> findAllPetitions() {
        List<PetitionDto> petitionResponseDtoList = new ArrayList<>();
        List<Petition> petitionList = petitionRepository.findAll();
        for (Petition petition: petitionList) {
            PetitionDto petitionResponseDto = PetitionDto.builder()
                    .id(petition.getId())
                    .subject(petition.getSubject())
                    .content(petition.getContent())
                    .category(petition.getCategory())
                    .anonymous(petition.getAnonymous())
                    .create_date(petition.getCreate_date())
                    .end_date(petition.getEnd_date())
                    .modify_date(petition.getModify_date())
                    .status(petition.getStatus())
                    .author_id(petition.getAuthor().getId())
                    .build();
            petitionResponseDtoList.add(petitionResponseDto);
        }
        return petitionResponseDtoList;
    }

    @Override
    public PetitionDto getPetition(Long id) {
        Petition petition = petitionRepository.findById(id).get();
        PetitionDto petitionResponseDto = PetitionDto.builder()
                .id(petition.getId())
                .subject(petition.getSubject())
                .content(petition.getContent())
                .category(petition.getCategory())
                .anonymous(petition.getAnonymous())
                .create_date(petition.getCreate_date())
                .modify_date(petition.getModify_date())
                .end_date(petition.getEnd_date())
                .status(petition.getStatus())
                .author_id(petition.getAuthor().getId())
                .build();
        return petitionResponseDto;
    }

    @Override
    public PetitionDto savePetition(PetitionDto petitionDto, String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            User user = userRepository.getByUsername(username);
            Petition petition = Petition.builder()
                    .subject(petitionDto.getSubject())
                    .content(petitionDto.getContent())
                    .category(petitionDto.getCategory())
                    .anonymous(petitionDto.getAnonymous())
                    .create_date(petitionDto.getCreate_date())
                    .modify_date(petitionDto.getModify_date())
                    .end_date(petitionDto.getEnd_date())
                    .status(petitionDto.getStatus())
                    .author(user)
                    .build();
            Petition savePetition = petitionRepository.save(petition);
            PetitionDto petitionResponseDto = PetitionDto.builder()
                    .id(savePetition.getId())
                    .subject(savePetition.getSubject())
                    .content(savePetition.getContent())
                    .category(savePetition.getCategory())
                    .anonymous(savePetition.getAnonymous())
                    .create_date(savePetition.getCreate_date())
                    .modify_date(savePetition.getModify_date())
                    .end_date(savePetition.getEnd_date())
                    .status(savePetition.getStatus())
                    .author_id(savePetition.getAuthor().getId())
                    .build();
            return petitionResponseDto;
        }
        return null;
    }

    @Override
    public PetitionDto changePetition(Long id, PetitionDto petitionDto) {
        Petition petition = petitionRepository.findById(id).get();
        petition.setSubject(petitionDto.getSubject());
        petition.setContent(petitionDto.getContent());
        petition.setCategory(petitionDto.getCategory());
        petition.setAnonymous(petitionDto.getAnonymous());

        Petition changedPetition = petitionRepository.save(petition);
        PetitionDto petitionResponseDto = PetitionDto.builder()
                .id(changedPetition.getId())
                .subject(changedPetition.getSubject())
                .content(changedPetition.getContent())
                .category(changedPetition.getCategory())
                .anonymous(changedPetition.getAnonymous())
                .create_date(changedPetition.getCreate_date())
                .modify_date(changedPetition.getModify_date())
                .end_date(changedPetition.getEnd_date())
                .status(changedPetition.getStatus())
                .author_id(changedPetition.getAuthor().getId())
                .build();
        return petitionResponseDto;
    }

    @Override
    public void deletePetition(Long id) {
        petitionRepository.deleteById(id);
    }
}
