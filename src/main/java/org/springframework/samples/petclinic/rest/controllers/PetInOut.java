package org.springframework.samples.petclinic.rest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PetInOut {

    @JsonProperty
    private Date birthDate;
    @JsonProperty
    private String name;
    @JsonProperty
    private int owner;
    @JsonProperty
    private int type;

    public PetInOut() {
    }

    public PetInOut(Date birthDate, String name, int owner, int type) {
        this.birthDate = birthDate;
        this.name = name;
        this.owner = owner;
        this.type = type;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
