package io.textback.azure.storage.blob.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorResponse {

    @XmlElement(name = "Code", required = true)
    private String code;

    @XmlElement(name = "Message", required = true)
    private String message;

    @XmlElement(name = "AuthenticationErrorDetail", required = false)
    private String authenticationErrorDetail;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthenticationErrorDetail() {
        return authenticationErrorDetail;
    }

    public void setAuthenticationErrorDetail(String authenticationErrorDetail) {
        this.authenticationErrorDetail = authenticationErrorDetail;
    }
}
