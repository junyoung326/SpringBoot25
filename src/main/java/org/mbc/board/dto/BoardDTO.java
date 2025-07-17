package org.mbc.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder // 빌더패턴은 @AllArgsConstructor, @NoArgsConstructor 필수
@AllArgsConstructor // 모든 필드를 생성자로
@NoArgsConstructor // 기본 생성자
public class BoardDTO {

    private Long bno;

    private String title;

    private String content;

    private String writer;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

}
