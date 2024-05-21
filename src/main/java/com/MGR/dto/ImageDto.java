package com.MGR.dto;

import com.MGR.entity.Image;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
@Getter
@Setter
public class ImageDto {

    private Long id;
    private String imgTitle;
    private String imgName;

    private String imgOriName;

    private String imgUrl;

    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();

    public static ImageDto of(Image image){
        return modelMapper.map(image, ImageDto.class);
    }

}
