package com.MGR.service;

import com.MGR.dto.AttractionDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.Image;
import com.MGR.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final ImageService imageService;

    public Attraction saveAttraction(AttractionDto attractionDto, MultipartFile imgFile) throws Exception {
        Attraction attraction = Attraction.create(attractionDto);
        attractionRepository.save(attraction);

        Image image = new Image();
        image.setAttraction(attraction);
        imageService.saveAttractionImage(image,imgFile);

        return attraction;
    }

    public List<Attraction> findAll() {
        return attractionRepository.findAll();
    }

    public Page<Attraction> getAttractionList(int page) {
        //페이징 처리
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));

        return attractionRepository.findAll(pageable);
    }

    public Optional<Attraction> findById(Long id) {
        return attractionRepository.findById(id);
    }

    public void delete(Attraction attraction) {
        imageService.deleteImage(attraction);
        attractionRepository.delete(attraction);
    }

    public void update(Long id, AttractionDto attractionDto, MultipartFile imgFile) throws Exception {
        Attraction update = attractionRepository.findById(id).orElseThrow();
        update.setClosureDay(attractionDto.getClosureDay());
        update.setName(attractionDto.getName());
        update.setInformation(attractionDto.getInformation());
        attractionRepository.save(update);

        Image findImage = imageService.findByAttraction(update);
        imageService.saveAttractionImage(findImage,imgFile);

    }
}
