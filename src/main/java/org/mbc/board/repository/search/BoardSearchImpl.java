package org.mbc.board.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.mbc.board.domain.Board;
import org.mbc.board.domain.QBoard;
import org.mbc.board.domain.QReply;
import org.mbc.board.domain.Reply;
import org.mbc.board.dto.BoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {  // 생성자
        super(Board.class);
    }
    //                       쿼리DSL용 상속                      구현체 인터페스 지정

    @Override // 인터페이스에서 만든 메서드 -> 실행코드 작성용
    public Page<Board> search1(Pageable pageable) {
        // 쿼리DSL로 다중검색용 코드 추가
        // 쿼리DSL의 목적은 타입 기반으로 코드를 이용함 -> Q도메인 클래스

        QBoard board = QBoard.board; // Q도메인 객체

        JPQLQuery<Board> query = from(board); // select * from board

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        // 다중조건일때 연산자 공식에 의해서 특수기호가 먼저 계산될 때가 있다.
        // ( )를 사용하면 선행 되기 때문에 BooleanBuilder가 이 역할을 한다. 
        
        // query.where(board.title.contains("1")); // where title like 1
        // select * from board where title like 1
        // contains 포함

        booleanBuilder.or(board.title.contains("11")) ; // where title like 11
        booleanBuilder.or(board.content.contains("11")) ; // where content like        
         
        
        query.where(booleanBuilder) ;  // // ( where title like 11 or content like 11)
        query.where(board.bno.gt(0L)) ; // pk를 이용해서 빠른 검색 where이 추가되면 and 조건
        //  where ( title like 11 or content like 11) and bno > 0
        
        // 페이징 처리용 코드
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // 쿼리문 실행해서 리스트에 담아라.

        long count = query.fetchCount(); // 검색후에 게시물 추 파악 용


        return null;
    }


    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // 인터페이스에서 만든 추상메서드를 구현하는 클래스
        QBoard board = QBoard.board; // 쿼리dsl 객체 생성
        JPQLQuery<Board> query = from(board); // select * from board

        //프론트에서 검색폼에 keyword가 비었을 경우도 있고 있을경우도 있다.
        if( (types != null && types.length >0 ) && keyword !=null ){
            // 제목,내용,이름 값이 있고 검색어가 있으면!!!!

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // 선실행용 ()

            for (String type : types){  // 파라미터로 넘어온 값을 String[] types

                switch (type){
                    case "t" :
                        // 제목이면
                        booleanBuilder.or(board.title.contains(keyword));
                        break;

                    case "c" :
                        // 내용이면
                        booleanBuilder.or(board.content.contains(keyword));
                        break;

                    case "w" :
                        // 작성자 이면
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                } // 프론트에서 넘어오는 String[]값을 파악하고 적용
            } // for문 종료
            query.where(booleanBuilder); //위에서 만든 조건을 적용 where title or content or writer
        } // if문 종료
        query.where(board.bno.gt(0L)); // pk를 활용해서 인덱싱 처리용 코드
        // where (title or content or writer) and bno > 0L

        this.getQuerydsl().applyPagination(pageable, query); // 페이징처리용 코드 + 쿼리문

        // Page<t> 클래스는 3가지의 리턴 타입을 만들어 준다.
        
        List<Board> list = query.fetch(); // 쿼리문 실행

        long count = query.fetchCount() ; // 검색된 게시물 수

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
        //            or b1_0.writer like ? escape '!'
        //        )
        //        and b1_0.bno>?
        //    order by
        //        b1_0.bno desc
        //    limit
        //        ?, ?
        
        return new PageImpl<>(list, pageable, count);
        //         리턴      검색된결과 board 
        //                          페이징처리용
        //                                    검색된 개수
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        // 문제점 파악 !!!  -> 위부분은 엔티티를 제네릭으로 처리하는데 지금은 dto로 처리함
        // 이문제를 해결하기 위해서 JPQL의 left 조인, inner join을 사용해야 한다.
        // 게시물과 댓글이 한쪽에만 데이터가 존재하는 상황이 있다.
        // 게시물은 있는대 댓글이 없다.  -> outer join 을 처리
        // 게시물과 댓글이 있었는데 게시물이 삭제됨!!! -> 비활성화, 같이 삭제 하는 방법...

        QBoard board = QBoard.board; // 게시글 객체
        QReply reply = QReply.reply; // 댓글 객체
        // Q가 붙는 도메인은 쿼리DSL로 동적쿼리를 담당한다.

        JPQLQuery<Board> query = from(board); // select * from board
        query.leftJoin(reply).on(reply.board.eq(board)); // fk = pk 연결용
        //    leftJoin(연관테이블).on 조인 조건을 지정

        query.groupBy(board); // board와 연관된 객체를 모아

        //프론트에서 검색폼에 keyword가 비었을 경우도 있고 있을경우도 있다.
        if( (types != null && types.length >0 ) && keyword !=null ){
            // 제목,내용,이름 값이 있고 검색어가 있으면!!!!

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // 선실행용 ()

            for (String type : types){  // 파라미터로 넘어온 값을 String[] types

                switch (type){
                    case "t" :
                        // 제목이면
                        booleanBuilder.or(board.title.contains(keyword));
                        break;

                    case "c" :
                        // 내용이면
                        booleanBuilder.or(board.content.contains(keyword));
                        break;

                    case "w" :
                        // 작성자 이면
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                } // 프론트에서 넘어오는 String[]값을 파악하고 적용
            } // for문 종료
            query.where(booleanBuilder); //위에서 만든 조건을 적용 where title or content or writer
        } // if문 종료
        query.where(board.bno.gt(0L)); // pk를 활용해서 인덱싱 처리용 코드
        // where (title or content or writer) and bno > 0L

        // JPA에서는 프로젝션(Projection) JPQL 결과를 바로 DTO로 처리하는 기술
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(
                Projections.bean(   // 엔티티를 dto객체로 변환하는 기술이 내장되어 있다.
                        BoardListReplyCountDTO.class,   // dto
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,  // entity
                        reply.count().as("replyCount")  // 댓글의 개수를 replyCount 필드에 넣음
                        ));

        // 리턴값을 제공
        this.getQuerydsl().applyPagination(pageable, dtoQuery); // dto로 변환된 코드가 적용
        List<BoardListReplyCountDTO> dtolist = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtolist, pageable, count);
        //                   페이징결과 , 페이징파라미터, 개수

        //Hibernate:
        //    select
        //        b1_0.bno,
        //        b1_0.title,
        //        b1_0.writer,
        //        b1_0.regdate,         board 필드를 출력
        //        count(r1_0.rno)       댓글테이블의 rno 개수
        //    from
        //        board b1_0            board 테이블에
        //    left join                 left 조인
        //        reply r1_0
        //            on r1_0.board_bno=b1_0.bno  on메서드로 조건이 bno와 같은
        //    where
        //        (
        //            b1_0.title like ? escape '!'
        //            or b1_0.content like ? escape '!'
        //            or b1_0.writer like ? escape '!'
        //        )
        //        and b1_0.bno>?                    pk로 빠른 검색(인덱싱)
        //    group by
        //        b1_0.bno                          그룹핑 count 처리
        //    order by
        //        b1_0.bno desc                     내림차순
        //    limit
        //        ?, ?                               페이징 처리
        //Hibernate:
        //    select
        //        count(distinct b1_0.bno)
        //    from
        //        board b1_0
        //    left join
        //        reply r1_0
        //            on r1_0.board_bno=b1_0.bno
        //    where
        //        (
        //            b1_0.title like ? escape '!'
        //            or b1_0.content like ? escape '!'
        //            or b1_0.writer like ? escape '!'
        //        )
        //        and b1_0.bno>?
        
    }


}
