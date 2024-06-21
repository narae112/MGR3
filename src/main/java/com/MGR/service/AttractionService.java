package com.MGR.service;

import com.MGR.dto.AttractionDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.Image;
import com.MGR.repository.AttractionRepository;
import com.MGR.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public CommandLineRunner initAttraction(AttractionRepository attractionRepository, ImageRepository imageRepository) {
        return args -> {
            String[] names = {"T익스프레스", "회전목마", "콜럼버스 대탐험", "범퍼카", "허리케인", "자이로드롭", "점핑피쉬", "회전바구니"};
            String[] informations = {
                    "시속 104km의 엄청난 속도, 낙하각 77도의 아찔함!\n세계 최고의 우든코스터가 잊지 못할 최고의 기억을 여러분께 선사합니다.",
                    "64필의 아름다운 백마를 타고 떠나는 로맨틱한 여행!",
                    "신대륙을 찾아 떠나는\n콜롬버스호의 대탐험이 시작된다.\n거친 파도에 맞서듯,\n33미터 고공에서 75도 경사로 떨어지는 절대 쾌감!\n대항해탐험을 함께 떠나봐요~",
                    "어트랙션의 원조!\n누구든 멋진 레이서가 될 수 있어\n항상 사랑받는 우리의 범퍼카~\n면허증이 없어도 오케이!\n귀여운 자동차를 타고 요리 쿵 조리 쿵 부딪혀보며\n재미난 경험을 즐겨 보세요.",
                    "미국 서부 시대 한 마을을 덮친 허리케인...\n오늘 초대형 폭풍 허리케인과 함께 하나가 된다!\n19미터 높이에서 회전하는 메가톤급 회오리 속에 몸을 맡겨 보는거야!",
                    "구름이 맞닿을 듯한 높이까지 올라갔다\n한 순간에 떨어지는 스릴만점 어트랙션입니다.",
                    "거대한 조개를 지나 다양한 물고기 친구들과 함께 떠나는 신나는 해저여행. 무지막지한 상어를 피해 점핑 점핑!!!",
                    "빙글~ 빙글~ 위로! 아래로! 한쪽으로 돌고~ 반대쪽으로 돌고~ 돌면서 즐기는 짜릿한 즐거움! 둥근 손잡이를 돌리면 회전속도가 더 빨라집니다.\n"
            };
            int[] closureDays = {21, 21, 22, 21, 22, 22, 22, 23};

            for (int i = 0; i < 8; i++) {
                Attraction attraction = new Attraction(names[i], informations[i], closureDays[i]);
                attractionRepository.save(attraction);

                Image image = new Image();
                image.setAttraction(attraction);
                image.setImgOriName("attraction" + (i + 1) + ".png");
                image.setImgUrl("/images/attraction/attraction" + (i + 1) + ".png");
                imageService.save(image);
            }
        };
    }
}
