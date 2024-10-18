package com.project.ecodein.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class LocalDateTimeDTO {

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime localDateTime;

    public LocalDateTimeDTO(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
