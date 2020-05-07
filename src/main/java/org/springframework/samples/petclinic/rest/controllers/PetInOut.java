package org.springframework.samples.petclinic.rest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PetInOut {

    @JsonProperty
    private Date birthDate;
    @JsonProperty
    private String name;
    @JsonProperty
    private PetIOOwner owner;
    @JsonProperty
    private PetIOType type;

    public PetInOut() {
    }

    public PetInOut(Date birthDate, String name, PetIOOwner owner, PetIOType type) {
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

    public PetIOOwner getOwner() {
        return owner;
    }

    public void setOwner(PetIOOwner owner) {
        this.owner = owner;
    }

    public PetIOType getType() {
        return type;
    }

    public void setType(PetIOType type) {
        this.type = type;
    }
}
