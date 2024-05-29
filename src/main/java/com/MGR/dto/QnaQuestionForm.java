package com.MGR.dto;

import com.MGR.entity.QnaQuestion;
import com.MGR.entity.Ticket;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QnaQuestionForm {
    private Long id;

    private List<ImageDto> reviewImgDtoList = new ArrayList<>();

    private List<Long> reviewImgIds = new ArrayList<>();

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    private LocalDateTime modifiedDate;


    private static ModelMapper modelMapper = new ModelMapper();

    public QnaQuestion createQnaQuestion() {
        return modelMapper.map(this, QnaQuestion.class);
    }
    public static QnaQuestionForm of(QnaQuestion qnaQuestion) {
        return modelMapper.map(qnaQuestion, QnaQuestionForm.class);
    }

}
