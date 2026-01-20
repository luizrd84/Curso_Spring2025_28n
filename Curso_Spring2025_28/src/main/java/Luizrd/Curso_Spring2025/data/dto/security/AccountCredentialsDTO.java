package Luizrd.Curso_Spring2025.data.dto.security;

import java.io.Serializable;
import java.util.Objects;

public class AccountCredentialsDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String fullname;

    public AccountCredentialsDTO() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        AccountCredentialsDTO that = (AccountCredentialsDTO) object;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getFullname(), that.getFullname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getFullname());
    }

    public AccountCredentialsDTO(String username, String password, String fullname) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
    }
}
