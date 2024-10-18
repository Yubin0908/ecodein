package com.project.ecodein.dto;

import java.time.LocalDateTime;
import com.project.ecodein.entity.Admin;
import com.project.ecodein.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@Builder
@AllArgsConstructor 
@NoArgsConstructor 
@ToString
public class CommentDTO {

    private Long id;
    private String comment;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Board boardNo;
    private Admin admin;

}
