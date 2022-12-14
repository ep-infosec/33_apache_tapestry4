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

import org.apache.tapestry.IMarkupWriter;

/**
 * Implementation of service tapestry.describe.RootDescriptionReceiverFactory; this represents a
 * central location to configure each created description receiver with the correct
 * {@link org.apache.tapestry.describe.DescribableStrategy} and set of
 * {@link org.apache.tapestry.describe.HTMLDescriptionReceiverStyles}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
public class RootDescriptionReceiverFactoryImpl implements RootDescriptionReceiverFactory
{
    private DescribableStrategy _strategy;

    private HTMLDescriptionReceiverStyles _styles = new HTMLDescriptionReceiverStyles();

    public RootDescriptionReciever newRootDescriptionReceiver(IMarkupWriter writer)
    {
        return new HTMLDescriptionReceiver(writer, _strategy, _styles);
    }

    public void setStrategy(DescribableStrategy strategy)
    {
        _strategy = strategy;
    }

    public void setTableClass(String tableClass)
    {
        _styles.setTableClass(tableClass);
    }

    public void setHeaderClass(String headerClass)
    {
        _styles.setHeaderClass(headerClass);
    }

    public void setSubheaderClass(String subheaderClass)
    {
        _styles.setSubheaderClass(subheaderClass);
    }

}
