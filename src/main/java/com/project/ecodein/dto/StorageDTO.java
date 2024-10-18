package com.project.ecodein.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class StorageDTO {
	
	private int storageNo;
	private String storageName;
	private String storageStatus;
	private String storageSite;
	private LocalDate storageRegist;

	public StorageDTO (String storageName, String storageStatus, String storageSite) {

		this.storageName = storageName;
		this.storageStatus = storageStatus;
		this.storageSite = storageSite;

	}
	
	
}

