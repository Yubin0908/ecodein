package com.project.ecodein.entity;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table (name = "admin")
public class Admin {

	@Id
	@Column (name = "admin_id", nullable = false)
	private String adminId;

	@Column (name = "admin_password", nullable = false)
	private String adminPassword;

	@Column (name = "admin_name", nullable = false)
	private String adminName;

	@Column (name = "admin_date", nullable = false, insertable = false, updatable = false)
	private Date adminDate;

	@Column (name = "admin_recognize", nullable = false, insertable = false)
	private boolean adminRecognize;

}
