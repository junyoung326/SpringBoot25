package org.mbc.board.service;

import org.mbc.board.dto.BoardDTO;

public interface BoardService {
    // 조장용 코드 -> 시그니쳐만 필요 -> Impl 구현클래스 -> 실행문을 만든다.

    Long register(BoardDTO boardDTO); // 프론트에서 폼에 있는 내용이 dto로 들어온다.\
    // 리턴은 bno가 된다.

    BoardDTO readOne(Long bno); // 프론트에서 bno가 넘어오면 객체가 리턴된다.

    void modify(BoardDTO boardDTO); // 프론트에서 dto가 넘어오면 수정 작업

    void remove(Long bno); // 프론트에서 bno가 넘어오면 삭제 작업 진행

}
