package com.kjh.unchained.request;

import com.kjh.unchained.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ToString
public class PostCreateDto {

    @NotBlank(message = "타이틀을 입력해주세요.")
    private String title;

    // 이걸 달아주면 검증을해준다 . @RequestBody 옆에 @Valid 선언을 해두면
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public PostCreateDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate(){
        if(title.contains("바보")){
            throw new InvalidRequest("title","제목에 바보를 포함할 수 없습니다");
        }
    }
}
