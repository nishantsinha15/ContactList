package in.nishant.contactlist;

public class User {
    public String name, phone, email, picture;
    User()
    {}

    User( String name, String phone, String email, String picture)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
    }
}
