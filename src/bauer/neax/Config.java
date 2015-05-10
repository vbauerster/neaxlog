package bauer.neax;

/**
 * Created by vbauer on 06/08/14.
 */
public class Config {

    private String port;
    private String messageTemplate;
    private String adminMail;
    private int mailerPool;


    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getAdminMail() {
        return adminMail;
    }

    public void setAdminMail(String adminMail) {
        this.adminMail = adminMail;
    }

    public int getMailerPool() {
        return mailerPool;
    }

    public void setMailerPool(int mailerPool) {
        this.mailerPool = mailerPool;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Config{");
        sb.append("port='").append(port).append('\'');
        sb.append(", messageTemplate='").append(messageTemplate).append('\'');
        sb.append(", adminMail='").append(adminMail).append('\'');
        sb.append(", mailerPool=").append(mailerPool);
        sb.append('}');
        return sb.toString();
    }
}
