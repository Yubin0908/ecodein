package com.project.ecodein.entity;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User {
	
	@Id
	@Column(name="user_id", nullable=false)
	private String userId;
	
	@ManyToOne
	@JoinColumn(name = "buyer_code", referencedColumnName="buyer_code", nullable = false)
	private Buyer buyerCode;

	@Column(name="user_password", nullable = false)
	private String userPassword;
	
	@Column(name="user_name", nullable = false)
	private String userName;

	@Column(name="user_date", nullable = false, insertable = false, updatable = false)
	private Date userDate;

	@Column(name = "user_email", nullable = false)
	private String userEmail;

    public User(String userId) {
		this.userId = userId;
    }
}

