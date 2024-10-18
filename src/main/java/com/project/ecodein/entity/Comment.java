package com.project.ecodein.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter 
@Builder
@AllArgsConstructor 
@NoArgsConstructor 
@Getter @Table(name="comment")
@Entity
@ToString
public class Comment {

	@Id
    @Column(name="id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name="comment", columnDefinition = "TEXT", nullable = false)
    private String comment;

	@Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

	@Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

	@ManyToOne
    @JoinColumn(name = "board_no", referencedColumnName = "board_no", nullable = false)
    private Board boardNo;

	@ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "admin_id", nullable = false)
    private Admin admin; // 작성자

}
