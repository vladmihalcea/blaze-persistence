/*
 * Copyright 2014 Blazebit.
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
package com.blazebit.persistence;

import com.blazebit.persistence.entity.Document;
import com.blazebit.persistence.entity.Person;
import com.blazebit.persistence.entity.Version;
import com.blazebit.persistence.entity.Workflow;
import java.util.Locale;
import javax.persistence.Tuple;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 * @since 1.0
 */
public class MultipleJoinTest extends AbstractCoreTest {

    @Override
    protected Class<?>[] getEntityClasses() {
        return new Class<?>[]{
            Workflow.class,
            Document.class,
            Version.class,
            Person.class
        };
    }

    @Test
    public void testExcplicitMultipleJoins() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Workflow.class)
                .leftJoin("localized", "l1")
                .leftJoin("localized", "l2")
                .select("id");
        String expectedQuery = "SELECT workflow.id FROM Workflow workflow"
                + " LEFT JOIN workflow.localized l1"
                + " LEFT JOIN workflow.localized l2";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testOneExplicitJoinAndOneExplicitDefaultJoin() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Workflow.class)
                .leftJoinOn("localized", "l1").on("KEY(l1)").eqExpression(":locale").end()
                .leftJoinDefault("localized", "l2")
                .select("localized[:locale].name")
                .select("localized.name");
        String expectedQuery = "SELECT l1.name, l2.name FROM Workflow workflow"
                + " LEFT JOIN workflow.localized l1 " + ON_CLAUSE + " KEY(l1) = :locale"
                + " LEFT JOIN workflow.localized l2";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testOneImplicitJoinAndOneImplicitDefaultJoin() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Workflow.class)
                .select("localized[:locale].name")
                .select("localized.name");
        String expectedQuery = "SELECT localized_locale.name, localized.name FROM Workflow workflow"
                + " LEFT JOIN workflow.localized localized_locale " + ON_CLAUSE + " KEY(localized_locale) = :locale"
                + " LEFT JOIN workflow.localized localized";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testFirstOneImplicitJoinAndOneImplicitDefaultJoinThenExplicit() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Workflow.class)
                .select("localized[:locale].name")
                .select("localized.name")
                .leftJoinOn("localized", "l1").on("KEY(l1)").eqExpression(":locale").end()
                .leftJoinDefault("localized", "l2");
        String expectedQuery = "SELECT l1.name, l2.name FROM Workflow workflow"
                + " LEFT JOIN workflow.localized l1 " + ON_CLAUSE + " KEY(l1) = :locale"
                + " LEFT JOIN workflow.localized l2";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testABC() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Document.class)
                .select("contacts[1].name")
                .select("AVG(LENGTH(contacts[1].localized[2]))")
                .leftJoinOn("contacts", "c").on("KEY(c)").eqExpression("1").end()
                .leftJoinOn("c.localized", "l1").on("KEY(l1)").eqExpression("1").end()
                .leftJoinOn("c.localized", "l3").on("KEY(l3)").eqExpression("3").end()
                .select("l1").select("l2").select("l3")
                .where("contacts[1].localized[1]").eqExpression("'test'")
                .where("c.localized[3]").eqExpression("'test'");
        String expectedQuery = "SELECT l1.name, l2.name FROM Workflow workflow"
                + " LEFT JOIN workflow.localized l1 " + ON_CLAUSE + " KEY(l1) = :locale"
                + " LEFT JOIN workflow.localized l2";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testExcplicitMultipleJoinsWithParameterMatch() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Workflow.class)
                .leftJoinOn("localized", "l1").on("KEY(l1)").eqExpression(":locale").end()
                .leftJoinOn("localized", "l2").on("KEY(l2)").eqExpression("workflow.defaultLanguage").end()
                .select("localized[:locale].name")
                .select("localized[defaultLanguage].name");
        String expectedQuery = "SELECT l1.name, l2.name FROM Workflow workflow"
                + " LEFT JOIN workflow.localized l1 " + ON_CLAUSE + " KEY(l1) = :locale"
                + " LEFT JOIN workflow.localized l2 " + ON_CLAUSE + " KEY(l2) = workflow.defaultLanguage";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testMultipleNestedJoins() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Document.class)
                .leftJoin("partners", "p1")
                .leftJoin("partners", "p2")
                .leftJoin("p1.localized", "l11")
                .leftJoin("p1.localized", "l12")
                .leftJoin("p2.localized", "l21")
                .leftJoin("p2.localized", "l22")
                .select("l11").select("l12")
                .select("l21").select("l22");
        String expectedQuery = "SELECT VALUE(l11), VALUE(l12), VALUE(l21), VALUE(l22) FROM Document document"
                + " LEFT JOIN document.partners p1"
                + " LEFT JOIN document.partners p2"
                + " LEFT JOIN p1.localized l11"
                + " LEFT JOIN p1.localized l12"
                + " LEFT JOIN p2.localized l21"
                + " LEFT JOIN p2.localized l22";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }

    @Test
    public void testMultipleNestedJoinsWithDefault() {
        CriteriaBuilder<Tuple> cb = cbf.from(em, Document.class)
                .leftJoin("partners", "p1")
                .leftJoin("partners.localized", "l")
                .where("p1.partnerDocument.name").eq("doc")
                .select("l");
                
        String expectedQuery = "SELECT VALUE(l) FROM Document document"
                + " LEFT JOIN document.partners p1"
                + " LEFT JOIN document.partners partners"
                + " LEFT JOIN partners.localized l"
                + " LEFT JOIN p1.partnerDocument p1_partnerDocument"
                + " WHERE p1_partnerDocument.name = :param_0";
        assertEquals(expectedQuery, cb.getQueryString());
        cb.setParameter("locale", Locale.GERMAN)
                .getResultList();
    }
}