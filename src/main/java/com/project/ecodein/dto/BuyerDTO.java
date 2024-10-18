package com.project.ecodein.dto;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDTO {

	private Long buyerCode;

	private String buyerName;

	private String buyerNumber;

	private String buyerAgent;

	private String buyerTel;

	private String buyerAddress;

	private LocalDateTime buyerResistDate;

	private boolean buyerStatus;

	private String buyerSecureCode;

}
