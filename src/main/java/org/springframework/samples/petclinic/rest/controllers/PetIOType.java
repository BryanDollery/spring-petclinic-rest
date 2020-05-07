package org.springframework.samples.petclinic.rest.controllers;

public class PetIOType {
    private int id;

    public PetIOType() {
    }

    public PetIOType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
