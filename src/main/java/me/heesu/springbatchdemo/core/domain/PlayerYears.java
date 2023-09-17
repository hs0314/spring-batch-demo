package me.heesu.springbatchdemo.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerYears implements Serializable {

    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int experincedYear;

    public String toString() {
        return "PLAYER:ID=" + ID + ",Last Name=" + lastName +
                ",First Name=" + firstName + ",Position=" + position +
                ",Birth Year=" + birthYear + ",DebutYear=" + debutYear +
                ",Exp Year=" + experincedYear;
    }

    public PlayerYears(Player p){
        this.ID = p.getID();
        this.lastName = p.getLastName();
        this.firstName = p.getFirstName();
        this.position = p.getPosition();
        this.birthYear = p.getBirthYear();
        this.debutYear = p.getDebutYear();
        this.experincedYear = Year.now().getValue() - p.getDebutYear();

    }
}
