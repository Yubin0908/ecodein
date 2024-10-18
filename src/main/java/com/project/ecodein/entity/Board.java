package com.project.ecodein.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "board")
public class Board {

	@Id
	@Column (name = "board_no", nullable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int boardNo;

	@Column (name = "board_title", nullable = false)
	private String boardTitle;

	@Column (name = "board_content", nullable = false)
	private String boardContent;

	@Column (name = "board_date", nullable = true)
	private LocalDateTime boardDate;

	@ManyToOne
	@JoinColumn (name = "user_id", referencedColumnName = "user_id", nullable = false)
	private User user;

}
