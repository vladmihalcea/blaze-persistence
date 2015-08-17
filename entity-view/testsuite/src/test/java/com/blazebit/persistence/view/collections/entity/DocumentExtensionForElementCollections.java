/*
 * Copyright 2015 Blazebit.
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
package com.blazebit.persistence.view.collections.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Christian Beikov
 * @since 1.0.6
 */
@Embeddable
public class DocumentExtensionForElementCollections implements Serializable {
    
    private DocumentForElementCollections parent;
    private Set<DocumentForElementCollections> childDocuments = new HashSet<DocumentForElementCollections>();

    @ManyToOne
    public DocumentForElementCollections getParent() {
        return parent;
    }

    public void setParent(DocumentForElementCollections parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "extension.parent")
    public Set<DocumentForElementCollections> getChildDocuments() {
        return childDocuments;
    }

    public void setChildDocuments(Set<DocumentForElementCollections> childDocuments) {
        this.childDocuments = childDocuments;
    }
    
}