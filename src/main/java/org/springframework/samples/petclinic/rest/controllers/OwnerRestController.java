/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.rest.errors.BindingErrorsResponse;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("owners")
public class OwnerRestController {

    @Autowired
    private ClinicService clinicService;

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @GetMapping
    public ResponseEntity<Collection<Owner>> getOwners() {
        Collection<Owner> owners = this.clinicService.findAllOwners();

        if (owners.isEmpty())
            return new ResponseEntity<>(NOT_FOUND);

        return new ResponseEntity<>(owners, OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @GetMapping(value = "/*/lastname/{lastName}")
    public ResponseEntity<Collection<Owner>> getOwnersList(@PathVariable("lastName") String ownerLastName) {
        if (ownerLastName == null)
            ownerLastName = "";

        Collection<Owner> owners = this.clinicService.findOwnerByLastName(ownerLastName);

        if (owners.isEmpty())
            return new ResponseEntity<>(NOT_FOUND);

        return new ResponseEntity<>(owners, OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @GetMapping(value = "/{ownerId}")
    public ResponseEntity<Owner> getOwner(@PathVariable("ownerId") int ownerId) {
        Owner owner = this.clinicService.findOwnerById(ownerId);
        if (owner == null)
            return new ResponseEntity<>(NOT_FOUND);

        return new ResponseEntity<>(owner, OK);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @PostMapping
    public ResponseEntity<Owner> addOwner(@RequestBody @Valid Owner owner, BindingResult bindingResult, UriComponentsBuilder ucBuilder) {

        final Integer id = owner.getId();
        HttpHeaders headers = new HttpHeaders();

        if (bindingResult.hasErrors() || id != null) {
            BindingErrorsResponse errors = new BindingErrorsResponse(id);
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }

        this.clinicService.saveOwner(owner);
        headers.setLocation(ucBuilder.path("/api/owners/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<>(owner, headers, CREATED);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @PutMapping(value = "/{ownerId}")
    @Transactional
    public ResponseEntity<Owner> updateOwner(@PathVariable("ownerId") int ownerId,
                                             @RequestBody @Valid Owner owner,
                                             BindingResult bindingResult,
                                             UriComponentsBuilder ucBuilder) {

        boolean bodyIdMatchesPathId = owner.getId() == null || ownerId == owner.getId();
        HttpHeaders headers = new HttpHeaders();

        if (bindingResult.hasErrors() || !bodyIdMatchesPathId) {
            BindingErrorsResponse errors = new BindingErrorsResponse(ownerId, owner.getId());
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
            return new ResponseEntity<>(headers, BAD_REQUEST);
        }

        Owner currentOwner = this.clinicService.findOwnerById(ownerId);

        if (currentOwner == null)
            return new ResponseEntity<>(NOT_FOUND);

        currentOwner.setAddress(owner.getAddress());
        currentOwner.setCity(owner.getCity());
        currentOwner.setFirstName(owner.getFirstName());
        currentOwner.setLastName(owner.getLastName());
        currentOwner.setTelephone(owner.getTelephone());
        this.clinicService.saveOwner(currentOwner);

        return new ResponseEntity<>(currentOwner, NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.OWNER_ADMIN)")
    @DeleteMapping(value = "/{ownerId}")
    @Transactional
    public ResponseEntity<Void> deleteOwner(@PathVariable("ownerId") int ownerId) {
        Owner owner = this.clinicService.findOwnerById(ownerId);

        if (owner == null)
            return new ResponseEntity<>(NOT_FOUND);

        this.clinicService.deleteOwner(owner);

        return new ResponseEntity<>(NO_CONTENT);
    }

}
