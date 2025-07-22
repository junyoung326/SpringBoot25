package org.mbc.board.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mbc.board.domain.Board;
import org.mbc.board.domain.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {

    @Autowired  // 필드 선언
    private ReplyRepository replyRepository;

    @Test
    public void testInsert(){
        // 댓글 등록용 테스트
        Long bno = 100L; // db에 있는지 확인필요

        // 100번 게시물에 댓글을 넣어보자.
        Board board = Board.builder().bno(bno).build();
        // Board 게시물에 100번을 가져옴.

        Reply reply = Reply.builder()
                .board(board)   // bno
                .replyText("리포지토리에서 테스트")
                .replyer("리포지토리")
                .build();
        // 100번게시물에 댓글 객체 생성

        replyRepository.save(reply); // insert into

        //Hibernate:
        //    insert
        //    into
        //        reply
        //        (board_bno, moddate, regdate, reply_text, replyer)
        //    values
        //        (?, ?, ?, ?, ?)
    }


    @Test
    @Transactional  //import org.springframework.transaction.annotation.Transactional;
    public void testBoardReplies(){
        Long bno = 100L;  // 100게시물에 댓글을 가져와!!!

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());
        //                  페이징처리요청용 0페이지, 10개 리스트 , rno 내림차순정렬

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        // JPQL를 이용한 selecte 처리용 코드
        // @Query("select r from Reply r where r.board.bno = :bno ")

        result.getContent().forEach(reply -> {
            log.info(reply);
        });

        //Hibernate:
        //    select
        //        r1_0.rno,
        //        r1_0.board_bno,
        //        r1_0.moddate,
        //        r1_0.regdate,
        //        r1_0.reply_text,
        //        r1_0.replyer          // 조건에 맞는 데이터 가져와
        //    from
        //        reply r1_0            // reply 테이블
        //    where
        //        r1_0.board_bno=?      // bno가 100L
        //    order by               // 정렬기법
        //        r1_0.rno desc
        //    limit                 // 페이징 처리
        //        ?

        // Reply(rno=2, replyText=리포지토리에서 테스트, replyer=리포지토리)

        // 엔티티에서@ToString/*(exclude = "board")  */ 처리 후 실행
        // 모든 연결된 테이블 조회

        // Could not initialize proxy [org.mbc.board.domain.Board#100] - no session
        // 실행 메서드 위에 @Transactional

        //Hibernate:
        //    select
        //        r1_0.rno,
        //        r1_0.board_bno,
        //        r1_0.moddate,
        //        r1_0.regdate,
        //        r1_0.reply_text,
        //        r1_0.replyer
        //    from
        //        reply r1_0
        //    where
        //        r1_0.board_bno=?
        //    order by
        //        r1_0.rno desc
        //    limit
        //        ?
        //Hibernate:
        //    select
        //        b1_0.bno,
        //        b1_0.content,
        //        b1_0.moddate,
        //        b1_0.regdate,
        //        b1_0.title,
        //        b1_0.writer
        //    from
        //        board b1_0
        //    where
        //        b1_0.bno=?

        // 댓글 안쪽에 게시글의 객체 내용일 전달 됨!
        // Reply(rno=2, board=Board(bno=100, title=제목...100(수정테스트), content=내용...100(수정됩니까?), writer=user0), replyText=리포지토리에서 테스트, replyer=리포지토리)
    }
}
