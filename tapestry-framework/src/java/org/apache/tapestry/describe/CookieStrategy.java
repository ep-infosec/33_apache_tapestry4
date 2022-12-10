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

package org.apache.tapestry.describe;

import javax.servlet.http.Cookie;

/**
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class CookieStrategy implements DescribableStrategy
{

    public void describeObject(Object object, DescriptionReceiver receiver)
    {
        Cookie cookie = (Cookie) object;

        // All other cookie information besides name and value
        // is null/default when the cookie is obtained from the
        // HttpServletRequest. The values are set when writing
        // a new cookie to the client.

        receiver.title(cookie.getName() + "=" + cookie.getValue());
    }

}
