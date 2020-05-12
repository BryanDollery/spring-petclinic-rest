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

package org.springframework.samples.petclinic.repository.springdatajpa;

import org.springframework.samples.petclinic.model.Specialty;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SpringDataSpecialtyRepositoryImpl implements SpecialtyRepositoryOverride {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void delete(Specialty specialty) {
        this.em.remove(this.em.contains(specialty) ? specialty : this.em.merge(specialty));
        Integer specId = specialty.getId();
        this.em.createNativeQuery("DELETE FROM vet_specialties WHERE specialty_id=" + specId).executeUpdate();
        this.em.createQuery("DELETE FROM Specialty specialty WHERE id=" + specId).executeUpdate();
    }

}
