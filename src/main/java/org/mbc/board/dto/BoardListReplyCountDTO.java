package org.mbc.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {
    // 댓글의 개수를 파악하여 리스트에 표시해주는 용도

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount; // 댓글 수!!!!
}
