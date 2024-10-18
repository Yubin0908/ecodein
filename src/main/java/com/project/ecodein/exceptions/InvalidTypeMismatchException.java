package com.project.ecodein.exceptions;

public class InvalidTypeMismatchException extends Throwable {

    public InvalidTypeMismatchException() {
        super("잘못된 확장자의 파일이 업로드되었습니다.");
    }

}
