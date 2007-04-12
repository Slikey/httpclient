/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.cookie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.params.CookieSpecParams;
import org.apache.http.params.HttpParams;

/**
 * Cookie management policy class. The cookie policy provides corresponding
 * cookie management interfrace for a given type or version of cookie. 
 * 
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 *
 * @since 4.0
 */
public final class CookiePolicy {

    public final static CookiePolicy DEFAULT = new CookiePolicy();
    
    private final Map registeredSpecs;
    
    public CookiePolicy() {
        super();
        this.registeredSpecs = new LinkedHashMap();
    }
    
    /**
     * Registers a {@link CookieSpecFactory} with the given identifier. 
     * If a specification with the given name already exists it will be overridden.  
     * This nameis the same one used to retrieve the {@link CookieSpecFactory} 
     * from {@link #getCookieSpec(String)}.
     * 
     * @param name the identifier for this specification
     * @param factory the {@link CookieSpecFactory} class to register
     * 
     * @see #getCookieSpec(String)
     */
    public synchronized void register(final String name, final CookieSpecFactory factory) {
         if (name == null) {
             throw new IllegalArgumentException("Name may not be null");
         }
        if (factory == null) {
            throw new IllegalArgumentException("Cookie spec factory may not be null");
        }
        registeredSpecs.put(name.toLowerCase(), factory);
    }

    /**
     * Unregisters the {@link CookieSpecFactory} with the given ID.
     * 
     * @param name the identifier of the {@link CookieSpec cookie specification} to unregister
     */
    public synchronized void unregister(final String id) {
         if (id == null) {
             throw new IllegalArgumentException("Id may not be null");
         }
         registeredSpecs.remove(id.toLowerCase());
    }

    /**
     * Gets the {@link CookieSpec cookie specification} with the given ID.
     * 
     * @param name the {@link CookieSpec cookie specification} identifier
     * @param params the {@link HttpParams HTTP parameters} for the cookie
     *  specification. 
     * 
     * @return {@link CookieSpec cookie specification}
     * 
     * @throws IllegalStateException if a policy with the given name cannot be found
     */
    public synchronized CookieSpec getCookieSpec(final String name, final HttpParams params) 
        throws IllegalStateException {

        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        CookieSpecFactory factory = (CookieSpecFactory) registeredSpecs.get(name.toLowerCase());
        if (factory != null) {
            return factory.newInstance(params);
        } else {
            throw new IllegalStateException("Unsupported cookie spec: " + name);
        }
    } 

    /**
     * Gets the {@link CookieSpec cookie specification} based on the given
     * HTTP parameters. The cookie specification name will be obtained from
     * the HTTP parameters.
     * 
     * @param params the {@link HttpParams HTTP parameters} for the cookie
     *  specification. 
     * 
     * @return {@link CookieSpec cookie specification}
     * 
     * @throws IllegalStateException if a policy with the given name cannot be found
     * 
     * @see CookieSpecParams#getCookiePolicy(HttpParams)
     */
    public CookieSpec getCookieSpec(final HttpParams params) 
        throws IllegalStateException {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        return getCookieSpec(CookieSpecParams.getCookiePolicy(params), params);
    } 

    /**
     * Gets the {@link CookieSpec cookie specification} with the given name.
     * 
     * @param name the {@link CookieSpec cookie specification} identifier
     * 
     * @return {@link CookieSpec cookie specification}
     * 
     * @throws IllegalStateException if a policy with the given name cannot be found
     */
    public synchronized CookieSpec getCookieSpec(final String name) 
        throws IllegalStateException {
        return getCookieSpec(name, null);
    } 

    /**
     * Obtains a list containing names of all registered {@link CookieSpec cookie 
     * specs} in their default order.
     * 
     * Note that the DEFAULT policy (if present) is likely to be the same
     * as one of the other policies, but does not have to be.
     * 
     * @return list of registered cookie spec names
     */
    public synchronized List getSpecNames(){
        return new ArrayList(registeredSpecs.keySet()); 
    }
    
}
