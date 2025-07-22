package org.mbc.board.repository.search;

import org.mbc.board.domain.Board;
import org.mbc.board.dto.BoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    // 게시물의 검색 조건을 다중화하는 기능을 가진다.

    Page<Board> search1(Pageable pageable);
    // 인터페이스에 구현체로 실제 동작은 구현 클래스에서 작성한다. (조장용)

    //실제로 구현할 코드 (다중 조건으로 제목,내용,작성자를 키워드와 매칭하여 select 처리함
    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);
    //                 제목t, 내용c, 작성자w      like      페이징 처리용(정렬,검색,페이지번호)
    
    // 게시판 리스트 /board/list 화면 출력용 메서드 (페이징, 정렬, 댓글수)
    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);
    //                                                 제목,내용,작성자    찾는단어       페이징처리,정렬
}
