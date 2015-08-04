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

import com.covisint.core.http.service.client.BaseResourceClientFactory;
import com.covisint.core.http.service.core.HttpServiceError;
import com.covisint.core.http.service.core.io.jsonp.HttpServiceErrorReader;
import com.covisint.core.http.service.core.io.jsonp.HttpServiceErrorWriter;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceReader;
import com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceWriter;

/** Factory for {@link ClogClientImpl} objects. */
public class ClogClientFactory extends BaseResourceClientFactory<ClogClientBuilder, ClogClientImpl> {

    /**
     * Constructor.
     *
     * @param serviceUrl the base url.
     */
    public ClogClientFactory(@Nonnull @NotEmpty String serviceUrl) {
        super(serviceUrl);
    }

    /** {@inheritDoc} */
    @Nonnull
    protected final ClogClientBuilder newBuilder() {
        return new ClogClientBuilder();
    }

    /** {@inheritDoc} */
    @Nonnull
    protected final ClogClientImpl buildResourceClient(@Nonnull ClogClientBuilder builder) {
        builder.addEntityReader(new HttpServiceErrorReader<HttpServiceError>()).addEntityReader(new ClogInstanceReader());
        builder.addEntityWriter(new HttpServiceErrorWriter<HttpServiceError>()).addEntityWriter(new ClogInstanceWriter());
        return builder.build();
    }

}
