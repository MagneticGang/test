package test.team.com.normallogintest1.DTO;

public class MemberDTO {

    String useremail, userpsd, username;

    public MemberDTO(){}

    public MemberDTO(String useremail, String userpsd, String username) {
        this.useremail = useremail;
        this.userpsd = userpsd;
        this.username = username;
    }

    public MemberDTO(String useremail, String userpsd) {
        this.useremail = useremail;
        this.userpsd = userpsd;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpsd() {
        return userpsd;
    }

    public void setUserpsd(String userpsd) {
        this.userpsd = userpsd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
