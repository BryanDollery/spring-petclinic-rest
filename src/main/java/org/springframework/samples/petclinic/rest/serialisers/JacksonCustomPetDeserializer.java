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

package org.springframework.samples.petclinic.rest.serialisers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class JacksonCustomPetDeserializer extends StdDeserializer<Pet> {

    public JacksonCustomPetDeserializer() {
        this(null);
    }

    public JacksonCustomPetDeserializer(Class<Pet> t) {
        super(t);
    }

    @Override
    public Pet deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Pet pet = new Pet();
        ObjectMapper mapper = new ObjectMapper();
        Date birthDate = null;
        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode owner_node = node.get("owner");
        JsonNode type_node = node.get("type");
        Owner owner = mapper.treeToValue(owner_node, Owner.class);
        PetType petType = mapper.treeToValue(type_node, PetType.class);
        int petId = node.get("id").asInt();
        String name = node.get("name").asText(null);
        String birthDateStr = node.get("birthDate").asText(null);

        try {
            birthDate = formatter.parse(birthDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        if (!(petId == 0))
            pet.setId(petId);

        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setOwner(owner);
        pet.setType(petType);
        return pet;
    }

}
