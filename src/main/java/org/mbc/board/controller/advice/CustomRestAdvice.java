package org.mbc.board.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice // 검증을 지원하는 컨트롤러
@Log4j2
public class CustomRestAdvice {
    // @RestController는 json으로 처리되기 때문에 문제가 발생하면 찾기 힘듬
    // Rest방식의 @Valid 과정에서 문제가 발생하면 처리를 지원하는 컨트롤러를 만듬.
    // 200 ok 외에 다른 오류가 발생하면 이곳에서 처리를 함.

    @ExceptionHandler({BindException.class}) // 이곳에서 Rest방식의 예외처리 담당
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) // 응답 실패시 처리용
    public ResponseEntity<Map<String,String>> handelBindException(BindException e){
        //                         import org.springframework.validation.BindException;
        log.error(e); // 에러에 대한 로그 출력
        Map<String,String > errorMap = new HashMap<>(); // 에러코드와 로그 출력

        if(e.hasErrors()){
            BindingResult bindingResult = e.getBindingResult();
            // 에러가 있으면 bindingResult에 넣어라

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(),fieldError.getCode());
                //                  key                 value
            });
        } // if문 종료
        return ResponseEntity.badRequest().body(errorMap);
        // 에러종류를 파악하고 for문으로 만든 에러를 응답객체에 key,value로 넘긴다.
        // 200외적으로 400 에러등을 처리하는 지원용 컨트롤러 임.
    }

    // 500에러 등에 대한 처리 p559
    @ExceptionHandler({DataIntegrityViolationException.class})  // fk 예외상황 처리하는 핸들러
    // DataIntegrityViolationException sql문이 잘못 되었거나 data가 잘못 들어온 경우
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String >> handleFKExeption(Exception e){
        log.error(e);

        Map<String, String > errorMap = new HashMap<>();
        // 에러 메세지를 담는다.

        errorMap.put("에러발생시간 time : ", "" + System.currentTimeMillis());
        errorMap.put("에러메시지 : ", "sql문이나 data가 잘못 들어왔습니다.");
        return ResponseEntity.badRequest().body(errorMap);
    }

    // 댓글 번호가 없을 때 처리되는 예외
    @ExceptionHandler({NoSuchElementException.class, // 찾는 요소가 없을 때 발생
                       EmptyResultDataAccessException.class  // 해당데이터가 없을 경우 처리 (삭제시 객체가 없을 때)
                      })
    // Optional.orElseThrow()
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String >> handleNoSuchElement(Exception e){ // Exception e(공통처리용)

        log.error(e);

        Map<String, String > errorMap = new HashMap<>();
        // 에러 메세지를 담는다.

        errorMap.put("에러발생시간 time : ", "" + System.currentTimeMillis());
        errorMap.put("에러메시지1 : ", "찾는 rno가 없습니다..");
        errorMap.put("에러메시지2 : ", "찾는 객체가 없습니다..");
        return ResponseEntity.badRequest().body(errorMap);

    }




}
