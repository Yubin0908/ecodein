package com.project.ecodein.dto;

import java.sql.Date;
import com.project.ecodein.entity.Buyer;
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
public class UserDTO {

private String userId;
	
	private Buyer buyerCode;
	private String userPassword;
	private String userName;
	private Date userDate;
	private String userEmail;
}
