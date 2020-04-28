/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * @author Bryan Dollery
 */

@SuppressWarnings("unused")
public class JacksonCustomVisitSerializer extends StdSerializer<Visit> {

    public JacksonCustomVisitSerializer() {
        this(null);
    }

    protected JacksonCustomVisitSerializer(Class<Visit> t) {
        super(t);
    }

    @Override
    public void serialize(Visit visit, JsonGenerator json, SerializerProvider provider) throws IOException {
        if ((visit == null) || (visit.getPet() == null))
            throw new IOException("Cannot serialize Visit object - visit or visit.pet is null");

        Format formatter = new SimpleDateFormat("yyyy/MM/dd");
        json.writeStartObject(); // visit

        writeId(json, visit.getId());

        json.writeStringField("date", formatter.format(visit.getDate()));
        json.writeStringField("description", visit.getDescription());

        Pet pet = visit.getPet();
        json.writeObjectFieldStart("pet");

        writeId(json, pet.getId());

        json.writeStringField("name", pet.getName());
        json.writeStringField("birthDate", formatter.format(pet.getBirthDate()));

        PetType petType = pet.getType();
        json.writeObjectFieldStart("type");

        writeId(json, petType.getId());

        json.writeStringField("name", petType.getName());
        json.writeEndObject(); // type

        Owner owner = pet.getOwner();
        json.writeObjectFieldStart("owner");

        writeId(json, owner.getId());

        json.writeStringField("firstName", owner.getFirstName());
        json.writeStringField("lastName", owner.getLastName());
        json.writeStringField("address", owner.getAddress());
        json.writeStringField("city", owner.getCity());
        json.writeStringField("telephone", owner.getTelephone());
        json.writeEndObject(); // owner
        json.writeEndObject(); // pet
        json.writeEndObject(); // visit
    }

    private void writeId(JsonGenerator json, Integer id) throws IOException {
        if (id == null)
            json.writeNullField("id");
        else
            json.writeNumberField("id", id);
    }

}
