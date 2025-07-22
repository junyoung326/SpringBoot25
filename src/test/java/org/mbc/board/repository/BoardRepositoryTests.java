package org.mbc.board.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.mbc.board.domain.Board;
import org.mbc.board.dto.BoardListReplyCountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest // 메서드용 테스트 동작
@Log4j2 // 로그용
public class BoardRepositoryTests {
    // 영속성 계층에 테스트용

    @Autowired // 생성자 자동 주입
    private BoardRepository boardRepository;

    @Test
    public void testInsert(){
        // 데이터베이스에 데이터 주입(c) 테스트 코드
        IntStream.rangeClosed(1,100).forEach(i -> {
            // i 변수에 1~100까지 100개의 정수를 반복해서 생성
            Board board = Board.builder()
                    .title("제목..."+i)  // board.setTitle()
                    .content("내용..."+i) // board.setContent()
                    .writer("user"+(i%10))  // board.setWriter()
                    .build(); // @Builder 용 (세터 대신 좀더 간단하고 가독성 좋게 )
                // log.info((board));
            Board result = boardRepository.save(board) ; // 데이터베이스에 기록하는 코드
            //                            .save 메서드는 jpa에서 상속한 메서드로 값을 저장하는 용도
            //                                          이미 값이 있으면 update를 진행한다.
            log.info("게시물 번호 출력 : " + result.getBno() + "게시물의 제목 : " + result.getTitle());

                }// forEach문 종료
        );// IntStream. 종료

    } // testInsert 메서드 종료

    @Test
    public void testSelect(){
        Long bno = 100L; // 게시물 번호가 100인 개체를 확인 해보자.

        Optional<Board> result = boardRepository.findById(bno);
        // Optional 널값이 나올 경우를 대비한 객체
        //                                    .findById(bno) ->  select * from board where bno = bno;
        // Hibernate:
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

        Board board = result.orElseThrow(); // 값이 있으면 넣어라

        log.info(bno + "가 데이터 베이스에 존재합니다. ");
        log.info(board) ; // Board(bno=100, title=제목...100, content=내용...100, writer=user0)
    }  // testSelect 메서드 종료

    @Test
    public void testUpdate(){

        Long bno = 100L; // 100번 게시물을 가져와서 수정후 테스트 종료

        Optional<Board> result = boardRepository.findById(bno); // bno 를 찾아서 result에 넣는다.

        Board board = result.orElseThrow(); // 가져온 값이 있으면 board 타입에 객체에 넣는다.

        board.change("수정테스트 제목", "수정테스트 내용"); // 제목과 내용만 수정할 수 있는 메서드

        boardRepository.save(board); // .save 메서드는 pk값이 없으면 insert, pk 있으면 update 함.

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
        //Hibernate:
        //    update
        //        board
        //    set
        //        content=?,
        //        moddate=?,
        //        title=?,
        //        writer=?
        //    where
        //        bno=?
    }

    @Test
    public void testDelete(){

        Long bno = 1L;

        boardRepository.deleteById(bno);
        //             .deleteById(bno) -> delecte from board where bno = bno

        // Hibernate:
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
        //Hibernate:
        //    delete
        //    from
        //        board
        //    where
        //        bno=?
    }

    @Test
    public void testPaging(){
        // .findAll() 는 모든 리스트를 출력하는 메서드 select * from board ;
        // 전체 리스트에 페이징과 정렬 기법도 추가 해보자.

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());
        //                           시작번호,페이지당 데이터 개수
        //                                       번호를 기준으로 내림차순 정렬!!!
        // Hibernate:
        //    select
        //        b1_0.bno,
        //        b1_0.content,
        //        b1_0.moddate,
        //        b1_0.regdate,
        //        b1_0.title,
        //        b1_0.writer
        //    from
        //        board b1_0
        //    order by
        //        b1_0.bno desc  (bno를 기준으로 내림차순 정렬)
        //    limit
        //        ?, ?         (시작번호, 끝번호)

        //Hibernate:
        //    select
        //        count(b1_0.bno)   board 전체 리스트 수를 알아옴.
        //    from
        //        board b1_0
        Page<Board> result = boardRepository.findAll(pageable);
        // 1장에 종이에 Board 객체를 가지고 있는 결과는 result 에 담긴다.
        // Page 클래스는 다음페이지 존재 여부, 이전페이지 존재 여부, 전체 데이터 개수, 등등.... 계산을 한다.

        log.info("전체 게시물 수 : " + result.getTotalElements());  // 99
        log.info("총 페이지 수 : " + result.getTotalPages());       // 10
        log.info("현재 페이지 번호 : " + result.getNumber());       // 0
        log.info("페이지당 데이터 개수 : " + result.getSize() );     // 10
        log.info("다음페이지 여부 : " + result.hasNext());          // true
        log.info("시작페이지 여부 : " + result.isFirst());         // true

        // 콘솔에 결과를 출력해보자.
        List<Board> boardList = result.getContent(); // 페이징처리된 내용을 가져와라

        boardList.forEach(board -> log.info(board));
        //   forEach는 인덱스를 사용하지 않고 앞에서부터 객체를 리턴함
        //                board -> log.info(board)
        //                      람다식 1개의 명령어가 있을 때 활용

    }

    // 쿼리dsl 테스트 진행
    @Test
    public void testSearch1(){

        Pageable pageable = PageRequest.of(1,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.search1(pageable); //페이징 기법을 사용해서 title = 1 값을 찾아 오나?

        result.getContent().forEach(board -> log.info(board));

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
        //        b1_0.title like ? escape '!'  -> like 1  -> 조건이 1개일 경우

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
        //        (
        //            b1_0.title like ? escape '!'
        //            or b1_0.content like ? escape '!'   -> 조건이 2개 title, content (booleanBuilder)
        //        )
        //        and b1_0.bno>?  -> query.where(board.bno.gt(0L))
        //    order by
        //        b1_0.bno desc
        //    limit
        //        ?, ?   -> this.getQuerydsl().applyPagination(pageable, query);
        //  PageRequest.of(1,10, Sort.by("bno").descending());

    }

    @Test
    public void testSearchAll(){
        // 프론트에서 t가 선택되면 title, c가 선택되면 content, w가 선택되면 writer가 조건으로 제시됨

        String[] types = {"t", "w"};  // 검색 조건

        String keyword = "10";  // 검색 단어

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

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
        //        (
        //            b1_0.title like ? escape '!'
        //            or b1_0.content like ? escape '!'
        //            or b1_0.writer like ? escape '!'      //   if( (types != null && types.length >0 ) && keyword !=null ){
        //        )
        //        and b1_0.bno>?
        //    order by
        //        b1_0.bno desc    // PageRequest.of(0,10, Sort.by("bno").descending());
        //    limit
        //        ?, ?


        log.info("전체 게시물 수 : " + result.getTotalElements());  // 99
        log.info("총 페이지 수 : " + result.getTotalPages());       // 10
        log.info("현재 페이지 번호 : " + result.getNumber());       // 0
        log.info("페이지당 데이터 개수 : " + result.getSize() );     // 10
        log.info("다음페이지 여부 : " + result.hasNext());          // true
        log.info("시작페이지 여부 : " + result.isFirst());         // true

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    public void testSearchReplyCount(){

        String[] types= {"t","c","w"};  // 제목, 내용, 작성자
        String keyword = "1";           // 제목이나 내용이나 작성자에 1값을 찾는다.

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        log.info("전체 게시물 수 : " + result.getTotalElements());  // 20
        log.info("총 페이지 수 : " + result.getTotalPages());       // 2
        log.info("현재 페이지 번호 : " + result.getNumber());       // 0
        log.info("페이지당 데이터 개수 : " + result.getSize() );     // 10
        log.info("다음페이지 여부 : " + result.hasNext());          // true
        log.info("시작페이지 여부 : " + result.isFirst());         // true

        result.getContent().forEach(board -> log.info(board));
        // BoardListReplyCountDTO(bno=100, title=제목...100(수정테스트), writer=user0, regDate=2025-07-22T11:11:46.002548, replyCount=2)
    }

} // 클래스 종료
