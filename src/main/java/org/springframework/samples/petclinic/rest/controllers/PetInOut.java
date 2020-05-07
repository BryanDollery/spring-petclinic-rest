package org.springframework.samples.petclinic.rest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PetInOut {

    class Owner {
        private int id;

        public Owner() {
        }

        public Owner(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    class Type {
        private int id;

        public Type() {
        }

        public Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @JsonProperty
    private Date birthDate;
    @JsonProperty
    private String name;
    @JsonProperty
    private Owner owner;
    @JsonProperty
    private Type type;

    public PetInOut() {
    }

    public PetInOut(Date birthDate, String name, Owner owner, Type type) {
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

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
