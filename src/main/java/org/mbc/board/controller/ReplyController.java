package org.mbc.board.controller;

import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.mbc.board.dto.ReplyDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController // 경로와 리턴이 다름
@RequestMapping("/replies") // http://localhost:80/replies
@Log4j2 //import lombok.extern.log4j.Log4j2;
public class ReplyController {
    // 댓글용 컨틀롤러로 Rest방식으로 처리
    // 일반적으로 Contoller와 혼용해서 사용 됨

    // C
//    p528에 CustomRestAdvice용으로 변경
//    @PostMapping(value="/" , consumes = MediaType.APPLICATION_JSON_VALUE)
//    //  post메서드로                                   json으로 처리용
//    public ResponseEntity<Map<String,Long>> register(@RequestBody ReplyDTO replyDTO){
//        // 리턴 타입이 응답용 객체에 key, value 로 전송하게 셋팅
//        //                                           프론트에서 ReplyDTO가 넘어오면 db에 전송
//            log.info(replyDTO); // 프론트에서 넘어온 객체를 콘솔에 출력
//            Map<String, Long> resultMap = Map.of("rno",111L);
//            //                                    key ,value
//
//        return ResponseEntity.ok(resultMap);  // 리턴타입이 프론트와 연결되지 않음.
//        //                   정상처리됨 (200) -> { "rno": 111 }
//    }

    // C
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(
            @Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult)throws BindException {
        // 검증용 코드   요청에 들어온 객게               리턴할 객체                   예외처리용

        log.info(replyDTO);

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
            // 에러가 발생하면 처리하는 용도
        } // 예외처리 종료 if문

        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",111L); // 리턴 테스트용 코드
        //             key  value
        // Map에 객체 추가할 때는 put 메서드를 사용함

        return resultMap;
        
        // keypoint : ReplyDTO를 프론트에서 수집할 때 @Valid를 적용 (null이나 ""등을 검증)
        //            null이나 ""가 들어오면 BindExceptin을 throw함
        //            메서드에 리턴값이 문제가 있으면 @RestControllerAdvice가 처리 함
        //            정상값만 리턴하게 셋팅
    }


    // R -> 전체


    // R -> 한개


    // U



    // D

}
