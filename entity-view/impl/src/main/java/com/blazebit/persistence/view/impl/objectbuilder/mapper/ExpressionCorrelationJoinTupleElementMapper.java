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

package com.blazebit.persistence.view.impl.objectbuilder.mapper;

import com.blazebit.persistence.CommonQueryBuilder;
import com.blazebit.persistence.FullQueryBuilder;
import com.blazebit.persistence.SelectBuilder;
import com.blazebit.persistence.view.CorrelationBuilder;
import com.blazebit.persistence.view.CorrelationProvider;
import com.blazebit.persistence.view.impl.objectbuilder.transformer.correlation.JoinCorrelationBuilder;

import java.util.Map;

/**
 *
 * @author Christian Beikov
 * @since 1.2.0
 */
public class ExpressionCorrelationJoinTupleElementMapper implements TupleElementMapper {

    private final CorrelationProvider provider;
    private final String correlationBasis;
    private final String correlationResult;
    private final String alias;

    public ExpressionCorrelationJoinTupleElementMapper(CorrelationProvider provider, String correlationBasis, String correlationResult, String alias) {
        this.provider = provider;
        this.correlationBasis = correlationBasis;
        this.correlationResult = correlationResult;
        this.alias = alias;
    }

    @Override
    public void applyMapping(SelectBuilder<?> queryBuilder, CommonQueryBuilder<?> parameterSource, Map<String, Object> optionalParameters) {
        CorrelationBuilder correlationBuilder = new JoinCorrelationBuilder((FullQueryBuilder<?, ?>) queryBuilder, optionalParameters, correlationBasis, correlationResult, alias);
        provider.applyCorrelation(correlationBuilder, correlationBasis);
    }

}