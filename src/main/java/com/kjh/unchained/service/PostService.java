package com.kjh.unchained.service;

import com.kjh.unchained.domain.PostEditor;
import com.kjh.unchained.exception.PostNotFound;
import com.kjh.unchained.repository.PostRepository;
import com.kjh.unchained.domain.Post;
import com.kjh.unchained.request.PostCreateDto;
import com.kjh.unchained.request.PostEditDto;
import com.kjh.unchained.request.PostSearch;
import com.kjh.unchained.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional //Transactional이 있어야 업데이트가 된다...
    public PostResponse edit(Long id , PostEditDto postEditDto){

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        //post.change(postEditDto.getTitle(), postEditDto.getContent());
        //builder 자체를 넘긴다.
        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        //검증을 해야된다..
        //1. if문으로 있는지업는지 검사해서 타게한다.
        //2. 그냥 넘길때 기존업데이트 안되는 정보를 넘기게한다 (요걸 선호)
        PostEditor postEditor = editorBuilder
                .title(postEditDto.getTitle())
                .content(postEditDto.getContent())
                .build();

        post.edit(postEditor);

        return new PostResponse(post);
    }

    public Long write(PostCreateDto postCreateDto){
        // postCreate -> Entity
        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .build();
        //클라이언트 측에서 데이터관리가 잘 안될 경우는 다시 데이터를 돌려달라고 하는 경우 도있다...
        return postRepository.save(post).getId();
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() ->  new PostNotFound());

        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        /**
         * 서비스의 크기가 커지면
         * controller -> WebService -(내부적 파싱 )> Repository
         *               PostService (뭔가 외부와연동)
         */

        return response;
    }

    // 글이 너무 많은 경우 -> 비용이 많이든다.
    // 글이 -> 1억개 -> DB가 뻗음
    // DB -> 애플리케이션 서버를 전달하는 시간  , 트래픽 비용등이 많이 발생할 수 있다.
    // 그러므로 전체 페이지에서 해당 원하는 페이지 값 리턴하도록 변경
    public List<PostResponse> getList(PostSearch postSearch) {


        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        // -> 존재하는 경우
        postRepository.delete(post);

    }
}
