// Copyright 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.vlib.services;

import org.apache.tapestry.vlib.ejb.IBookQuery;

/**
 * Handles remote lookup to obtain a new instance of the
 * {@link org.apache.tapestry.vlib.ejb.IBookQuery} stateful session EJB.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 * @see org.apache.tapestry.vlib.ejb.IBookQueryHome
 */
public interface BookQuerySource
{
    /**
     * Returns a new instance of a book query, ready to perform various query operations.
     * 
     * @return book query instance
     */
    IBookQuery newQuery();
}
