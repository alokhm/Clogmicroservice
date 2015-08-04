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

import org.apache.http.protocol.HttpContext;

import com.covisint.core.http.service.client.BaseResourceClient;
import com.covisint.core.http.service.core.ServiceException;
import com.covisint.core.support.constraint.Nonnull;
import com.covisint.core.support.constraint.NotEmpty;
import com.covisint.platform.clog.core.cloginstance.ClogInstance;
import com.google.common.util.concurrent.CheckedFuture;

/** Resource client implementation that operates on {@link clogInstance}. */
public class ClogClientImpl extends BaseResourceClient<ClogInstance> implements
		ClogClient {

	/** {@inheritDoc} */
	@Nonnull
	public CheckedFuture<ClogInstance, ServiceException> update(
			@Nonnull @NotEmpty String clogInstanceId,
			@Nonnull HttpContext httpContext) {
		throw new UnsupportedOperationException();
	}

}