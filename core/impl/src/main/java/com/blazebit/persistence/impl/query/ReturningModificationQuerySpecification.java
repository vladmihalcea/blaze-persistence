/*
 * Copyright 2014 - 2016 Blazebit.
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

package com.blazebit.persistence.impl.query;

import com.blazebit.persistence.ReturningObjectBuilder;
import com.blazebit.persistence.impl.AbstractCommonQueryBuilder;
import com.blazebit.persistence.impl.plan.CustomReturningModificationQueryPlan;
import com.blazebit.persistence.impl.plan.ModificationQueryPlan;
import com.blazebit.persistence.impl.plan.SelectQueryPlan;

import javax.persistence.Query;
import java.util.*;

/**
 *
 * @author Christian Beikov
 * @since 1.2.0
 */
public class ReturningModificationQuerySpecification<T> extends CustomQuerySpecification<T> {

    private final Query exampleQuery;
    private final String[] returningColumns;
    private final ReturningObjectBuilder<T> objectBuilder;

    public ReturningModificationQuerySpecification(AbstractCommonQueryBuilder<?, ?, ?, ?, ?> commonQueryBuilder, Query baseQuery, Query exampleQuery, Set<String> parameterListNames, boolean recursive, List<CTENode> ctes, boolean shouldRenderCteNodes,
                                                   String[] returningColumns, ReturningObjectBuilder<T> objectBuilder) {
        super(commonQueryBuilder, baseQuery, parameterListNames, null, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, recursive, ctes, shouldRenderCteNodes);
        this.exampleQuery = exampleQuery;
        this.returningColumns = returningColumns;
        this.objectBuilder = objectBuilder;
    }

    @Override
    public ModificationQueryPlan createModificationPlan(int firstResult, int maxResults) {
        final String sql = getSql();
        return new CustomReturningModificationQueryPlan<T>(extendedQuerySupport, serviceProvider, exampleQuery, objectBuilder, participatingQueries, sql, firstResult, maxResults);
    }

    @Override
    public SelectQueryPlan createSelectPlan(int firstResult, int maxResults) {
        final String sql = getSql();
        return new CustomReturningModificationQueryPlan<T>(extendedQuerySupport, serviceProvider, exampleQuery, objectBuilder, participatingQueries, sql, firstResult, maxResults);
    }

    @Override
    public Query getBaseQuery() {
        return baseQuery;
    }

    @Override
    protected void initialize() {
        List<Query> participatingQueries = new ArrayList<Query>();

        StringBuilder sqlSb = new StringBuilder(extendedQuerySupport.getSql(em, baseQuery));
        StringBuilder withClause = applyCtes(sqlSb, baseQuery, participatingQueries);
        // NOTE: CTEs will only be added, if this is a subquery
        Map<String, String> addedCtes = applyExtendedSql(sqlSb, false, false, withClause, returningColumns, null);
        participatingQueries.add(baseQuery);

        this.sql = sqlSb.toString();
        this.participatingQueries = participatingQueries;
        this.addedCtes = addedCtes;
        this.dirty = false;
    }
}