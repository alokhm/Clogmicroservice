/* 
 * Copyright 2015 Covisint
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.covisint.platform.clog.cloginstance.client;

import com.covisint.core.http.service.client.BaseResourceClientBuilder;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.SupportedMediaTypesE;
import com.google.common.net.MediaType;

/** Default implementation of a {@link ClogClient} builder. */
public class ClogClientBuilder extends BaseResourceClientBuilder<ClogClientBuilder, ClogClientImpl> {

    /** Base path to the resource collection endpoint. */
    private static final String RESOURCE_COLLECTION_PATH = "/cloginstances";

    /** {@inheritDoc} */
    @Nonnull
    @NotEmpty
    protected final String getResourceCollectionPath() {
        return RESOURCE_COLLECTION_PATH;
    }

    /** {@inheritDoc} */
    @Nonnull
    protected final MediaType getResourceRepresentation() {
		return MediaType.parse(SupportedMediaTypesE.CLOG_INSTANCE_V1_MEDIA_TYPE.string());
      
    	
       
    }

    /** {@inheritDoc} */
    @Nonnull
    public final ClogClientImpl build() {
        return populateBaseBuilder(new ClogClientImpl());
    }

}