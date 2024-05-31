package com.MGR.service;

import com.MGR.dto.AttractionDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.Image;
import com.MGR.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final ImageService imageService;

    public void create(AttractionDto attractionDto, MultipartFile imgFile) throws Exception {
        Attraction attraction = Attraction.create(attractionDto);
        attractionRepository.save(attraction);

        Image image = new Image();
        image.setAttraction(attraction);
//        imageService.saveAttractionImage(image,imgFile);
    }

    public List<Attraction> findAll() {
        return attractionRepository.findAll();
    }
}
