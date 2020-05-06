package optimal_route.contract;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "accounts",uniqueConstraints = @UniqueConstraint(name="uniqueUserCOns" , columnNames = {"username","email"}))
public class Account implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    @Id
    @GeneratedValue(generator = "incrementor")
    @GenericGenerator(name="incrementor",strategy = "increment")
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name="password")
    private String pswd;
    @Column(name="email")
    private String email;
    @Column(name="surname")
    private String surname;
    @Column(name="name")
    private String name;
    @Column(name="role")
    private String role;

    public Account(){

    }

    private Account(AccountBuilder builder){
        this.username=builder.username;
        this.name=builder.name;
        this.surname=builder.surname;
        this.pswd=builder.pswd;
        this.email=builder.email;
        this.id=builder.id;
        this.role=builder.role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String toString(){
        return username+" "+pswd;
    }

    public static class AccountBuilder {
        private int id;
        private String username;
        private String pswd;
        private String email;
        private String name;
        private String surname;
        private String role;

        public AccountBuilder id(int id) {
            this.id = id;
            return this;
        }

        public AccountBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AccountBuilder pswd(String pswd) {
            this.pswd = pswd;
            return this;
        }

        public AccountBuilder email(String email) {
            this.email = email;
            return this;
        }
        public AccountBuilder name(String name) {
            this.name = name;
            return this;
        }
        public AccountBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }
        public AccountBuilder role(String role) {
            this.role = role;
            return this;
        }

        public Account build(){
            return new Account(this);
        }
    }

}

