package com.project.ecodein.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

	private String adminId;
	private String adminPassword;
	private String adminName;
	private Date adminDate;
	private boolean adminRecognize;

}
