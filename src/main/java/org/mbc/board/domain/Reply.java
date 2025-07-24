package org.mbc.board.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity     // 테이블 관리용 객체
@Getter     // 게터용
@Builder    // 세터 대신 빌더패턴 필수로 @AllArgsConstructor @NoArgsConstructor
@AllArgsConstructor // 모든 필드를 생성자 파라미터로 처리
@NoArgsConstructor  // 기본생성자용
@ToString  // board 제외하고 toString 처리 (객체로 이미 되어 있음) (exclude = "board")
@Table(name ="Reply", indexes = {@Index(name="idx_reply_board_bno", columnList = "board_bno")})
// 테이블명을 Reply    빠른처리(검색)를 위한 인덱싱 처리 fk로 지정된 필드를 사용
public class Reply extends BaseEntity { // extends BaseEntity 등록일, 수정일 처리용 객체
    
    @Id // pk로 선언
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동번호생성
    private Long rno; //게시물 번호
    
    @ManyToOne(fetch = FetchType.LAZY) //지연로딩 천천히하는 로딩
            // 추천! LAZY 로딩에는 no session이라는 예외가 발생한다. -> @Transactional 코드 필수
            // EAGER 로딩은 연결된 모든 테이블에 값을 가져옴 -> db가 힘들어함!
    private Board board; // 게시글 fk처리해야함.    -> board_bno bigint
    // Reply 테이블을 생성하면서 Board에 id값을 확인하여 fk로 선언함
    
    private String replyText;   // 댓글내용
        
    private String replyer;     // 댓글 작성자
    
    // 등록날짜와 수정날짜는 상속받아 처리

    //Hibernate:
    //    create table reply (
    //        rno bigint not null auto_increment,       -> mariadb에서 자동번호 생성
    //        moddate datetime(6),
    //        regdate datetime(6),
    //        reply_text varchar(255),
    //        replyer varchar(255),
    //        board_bno bigint,
    //        primary key (rno)     -> 기본키로 생성
    //    ) engine=InnoDB
    //Hibernate:
    //    alter table if exists reply   -> reply 테이블에 fk 선언됨
    //       add constraint FKr1bmblqir7dalmh47ngwo7mcs
    //       foreign key (board_bno)
    //       references board (bno)

    // 세터 대신 변경시 활용되는 메서드 (댓글 수정은 text만 가능)
    public void changeText(String text){
        this.replyText = text;
    }
}
