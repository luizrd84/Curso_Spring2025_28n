package Luizrd.Curso_Spring2025.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "spring.mail") //referencia ao yml
public class EmailConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
    private boolean ssl;

    public EmailConfig() {

    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmailConfig that = (EmailConfig) o;
        return getPort() == that.getPort() && isSsl() == that.isSsl() && Objects.equals(getHost(), that.getHost()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && Objects.equals(getFrom(), that.getFrom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort(), getUsername(), getPassword(), getFrom(), isSsl());
    }
}
