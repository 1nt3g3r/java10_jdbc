package entity;

import javax.persistence.*;

@Entity
@Table(name = "PersonInfo")
public class PersonInfo {
    @Id
    @Column(name = "person_id", insertable = false, updatable = false)
    private long personId;

    @Column(name = "passport_number")
    private String passportNumber;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Person person;

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
