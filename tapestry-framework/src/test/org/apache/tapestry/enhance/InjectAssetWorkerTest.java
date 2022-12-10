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

package org.apache.tapestry.enhance;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.lang.reflect.Modifier;
import java.util.Collections;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.service.MethodSignature;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.spec.AssetSpecification;
import org.apache.tapestry.spec.IAssetSpecification;
import org.apache.tapestry.spec.IComponentSpecification;
import org.testng.annotations.Test;

/**
 * Tests for {@link org.apache.tapestry.enhance.InjectAssetWorker}.
 * 
 * @author Howard M. Lewis Ship
 * @since 4.0
 */
@Test
public class InjectAssetWorkerTest extends BaseEnhancementTestCase
{
    private IComponentSpecification newSpec(String assetName, String propertyName, Location location)
    {
        IAssetSpecification as = new AssetSpecification();
        as.setPropertyName(propertyName);
        as.setLocation(location);

        IComponentSpecification spec = newSpec();

        expect(spec.getAssetNames()).andReturn(Collections.singletonList(assetName));

        expect(spec.getAsset(assetName)).andReturn(as);

        return spec;
    }

    public void testNoWork()
    {
        IComponentSpecification spec = newSpec("fred", null, null);
        EnhancementOperation op = newEnhancementOp();

        replay();

        new InjectAssetWorker().performEnhancement(op, spec);

        verify();
    }

    public void testSuccess()
    {
        Location l = newLocation();
        IComponentSpecification spec = newSpec("fred", "barney", l);
        EnhancementOperation op = newEnhancementOp();

        trainGetPropertyType(op, "barney", IAsset.class);

        op.claimReadonlyProperty("barney");

        trainGetAccessorMethodName(op, "barney", "getBarney");

        op.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(IAsset.class, "getBarney", null, null),
                "return getAsset(\"fred\");",
                l);

        replay();

        new InjectAssetWorker().performEnhancement(op, spec);

        verify();
    }

    public void testFailure()
    {
        Location l = newLocation();
        Throwable ex = new ApplicationRuntimeException(EnhanceMessages.claimedProperty("barney"));
        EnhancementOperation op = newEnhancementOp();

        IComponentSpecification spec = newSpec("fred", "barney", l);

        ErrorLog log = newMock(ErrorLog.class);

        trainGetPropertyType(op, "barney", IComponent.class);

        op.claimReadonlyProperty("barney");
        expectLastCall().andThrow(ex);

        trainGetBaseClass(op, BaseComponent.class);

        log.error(EnhanceMessages.errorAddingProperty("barney", BaseComponent.class, ex), l, ex);

        replay();

        InjectAssetWorker w = new InjectAssetWorker();

        w.setErrorLog(log);

        w.performEnhancement(op, spec);

        verify();
    }

    public void testWrongPropertyType()
    {
        EnhancementOperation op = newEnhancementOp();

        trainGetPropertyType(op, "barney", IComponent.class);

        op.claimReadonlyProperty("barney");

        replay();

        InjectAssetWorker w = new InjectAssetWorker();
        try
        {
            w.injectAsset(op, "fred", "barney", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Property barney is type org.apache.tapestry.IComponent, which is not compatible with the expected type, org.apache.tapestry.IAsset.",
                    ex.getMessage());
        }

        verify();

    }
}
