package org.mbc.board.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mbc.board.dto.ReplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister() {
        // 프론트에서 dto가 넘어오면 댓글 db에 등록 insert

        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("서비스에서 댓글등록테스트")
                .replyer("서비스테스트")
                .bno(98L)   // 98번 게시물에 댓글 등록 연습
                .build();

        log.info("testRegister()메서드 실행....");
        log.info(replyService.register(replyDTO));

        //Hibernate:
        //    insert
        //    into
        //        reply
        //        (board_bno, moddate, regdate, reply_text, replyer)
        //    values
        //        (?, ?, ?, ?, ?)
        //  o.mbc.board.service.ReplyServiceTests    : 3

    }

}
