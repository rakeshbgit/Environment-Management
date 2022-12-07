package com.apimanagement.Environment.exception;

public class ErrorObject {
    private  String errorMessage;
    private String errorCode= "501";
    private String status ="Application Error";

    public ErrorObject(String errorMessage, String errorCode, String status) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.status = status;
    }

    public ErrorObject(String errorMessage) {
        this.errorMessage = errorMessage;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Response{" +
                "Message='" + errorMessage + '\'' +
                ", statusCode='" + errorCode + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
