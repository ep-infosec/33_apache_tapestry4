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

package org.apache.tapestry.form;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.tapestry.*;
import org.apache.tapestry.engine.DirectServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.valid.IValidationDelegate;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link org.apache.tapestry.form.LinkSubmit} 
 */
@Test
public class LinkSubmitTest extends BaseComponentTestCase
{
    private class ScriptFixture implements IScript
    {
        public void execute(IRequestCycle cycle, IScriptProcessor processor, Map symbols)
        {
            assertNotNull(cycle);
            assertNotNull(processor);
            assertNotNull(symbols);

            symbols.put("href", "HREF");
        }

        public void execute(IComponent target, IRequestCycle cycle, IScriptProcessor processor, Map symbols)
        {
            assertNotNull(cycle);
            assertNotNull(processor);
            assertNotNull(symbols);

            symbols.put("href", "HREF");
        }
        
        public Resource getScriptResource()
        {
            return null;
        }

    }

    public void test_Render_Normal()
    {
        IMarkupWriter writer = newBufferWriter();
        IRequestCycle cycle = newCycle();
        IForm form = newForm();

        LinkSubmit linkSubmit = newInstance(LinkSubmit.class,
                                            "form", form,
                                            "name", "fred_1",
                                            "id", "fred_id",
                                            "clientId", "fred_1", 
                                            "submitType", FormConstants.SUBMIT_NORMAL);

        expect(form.getClientId()).andReturn("form");
        trainResponseBuilder(cycle, writer);        
        linkSubmit.addBody(newBody());

        replay();

        linkSubmit.renderFormComponent(writer, cycle);

        verify();

        assertBuffer("<a href=\"javascript:tapestry.form.submit('form', 'fred_1');\" id=\"fred_1\">BODY</a>");
    }

    public void test_Render_Disabled()
    {
        IMarkupWriter writer = newBufferWriter();
        IRequestCycle cycle = newCycle();

        IForm form = newForm();

        LinkSubmit linkSubmit = newInstance(LinkSubmit.class,
                                            "disabled", Boolean.TRUE,
                                            "form", form,
                                            "name", "fred_1",
                                            "id", "fred_id",
                                            "submitType", FormConstants.SUBMIT_NORMAL);
        linkSubmit.addBody(newBody());

        trainResponseBuilder(cycle, writer);
        
        replay();

        linkSubmit.renderFormComponent(writer, cycle);

        verify();

        assertBuffer("BODY");
    }

    public void test_Render_Submit_Bindings_True()
    {
        IMarkupWriter writer = newBufferWriter();
        IRequestCycle cycle = newCycle();

        IEngineService engine = newMock(IEngineService.class);
        ILink link = newMock(ILink.class);
        
        IForm form = newForm();
        List updates = Arrays.asList("comp1", "comp2");

        LinkSubmit linkSubmit = newInstance(LinkSubmit.class,
                                            "updateComponents", updates,
                                            "form", form,
                                            "name", "submitMe",
                                            "clientId", "submitMe",
                                            "submitType", FormConstants.SUBMIT_NORMAL,
                                            "directService", engine);

        expect(form.getClientId()).andReturn("form");
        linkSubmit.addBody(newBody());

        expect(engine.getLink(eq(true), isA(DirectServiceParameter.class))).andReturn(link);
        expect(link.getURL()).andReturn("http://submit");

        trainResponseBuilder(cycle, writer);

        replay();

        linkSubmit.renderFormComponent(writer, cycle);

        verify();

        assertBuffer("<a href=\"http://submit\" " +
                     "onclick=\"tapestry.form.submit('form', 'submitMe'," +
                     "{async:true,json:false,url:this.href}); return false;\" id=\"submitMe\">BODY</a>");
    }

    public void test_Prepare_Normal()
    {
        IRequestCycle cycle = newCycle();

        trainGetAttribute(cycle, LinkSubmit.ATTRIBUTE_NAME, null);

        LinkSubmit linkSubmit = (LinkSubmit) newInstance(LinkSubmit.class);

        cycle.setAttribute(LinkSubmit.ATTRIBUTE_NAME, linkSubmit);

        replay();

        linkSubmit.prepareForRender(cycle);

        verify();
    }

    public void test_Prepare_Conflict()
    {
        IRequestCycle cycle = newCycle();
        IPage page = newPage("MyPage");
        Location bloc = newLocation();
        Location floc = newLocation();
        IComponent existing = newComponent("MyPage/barney", bloc);

        trainGetAttribute(cycle, LinkSubmit.ATTRIBUTE_NAME, existing);

        trainGetIdPath(page, null);

        LinkSubmit linkSubmit = newInstance(LinkSubmit.class, "id", "fred", "page", page, "container", page, "location", floc);

        replay();

        try
        {
            linkSubmit.prepareForRender(cycle);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assert ex.getMessage()
            .indexOf("LinkSubmit MyPage/fred may not be enclosed by another LinkSubmit ") > -1;
            
            assertSame(linkSubmit, ex.getComponent());
            assertSame(floc, ex.getLocation());
        }

        verify();
    }

    public void test_Cleanup_After_Render()
    {
        IRequestCycle cycle = newCycle();

        cycle.removeAttribute(LinkSubmit.ATTRIBUTE_NAME);

        replay();

        LinkSubmit linkSubmit = (LinkSubmit) newInstance(LinkSubmit.class);

        linkSubmit.cleanupAfterRender(cycle);

        verify();
    }

    public void test_Is_Clicked()
    {
        IRequestCycle cycle = newCycle();

        trainGetParameter(cycle, FormConstants.SUBMIT_NAME_PARAMETER, "fred");

        replay();

        LinkSubmit linkSubmit = (LinkSubmit) newInstance(LinkSubmit.class);

        assertEquals(true, linkSubmit.isClicked(cycle, "fred"));

        verify();
    }

    public void test_Is_Not_Clicked()
    {
        IRequestCycle cycle = newCycle();

        trainGetParameter(cycle, FormConstants.SUBMIT_NAME_PARAMETER, null);

        replay();

        LinkSubmit linkSubmit = (LinkSubmit) newInstance(LinkSubmit.class);

        assertEquals(false, linkSubmit.isClicked(cycle, "fred"));

        verify();
    }

    public void test_Rewind()
    {
        IMarkupWriter writer = newWriter();
        IRequestCycle cycle = newCycle();
        IRender body = newRender();
        IForm form = newForm();
        IValidationDelegate delegate = newDelegate();

        LinkSubmit linkSubmit = newInstance(LinkSubmit.class, "name", "fred", "form", form);
        linkSubmit.addBody(body);

        trainGetForm(cycle, form);

        trainWasPrerendered(form, writer, linkSubmit, false);

        trainGetDelegate(form, delegate);

        delegate.setFormComponent(linkSubmit);
        
        trainGetElementId(form, linkSubmit, "fred");
        
        trainIsRewinding(form, true);
        
        // Finally, code inside LinkSubmit ...

        trainGetParameter(cycle, FormConstants.SUBMIT_NAME_PARAMETER, null);

        // ... and back to AbstractFormComponent
        
        trainResponseBuilder(cycle, writer);
        
        body.render(writer, cycle);

        replay();

        linkSubmit.renderComponent(writer, cycle);

        verify();
    }

    private void trainIsRewinding(IForm form, boolean isRewindind)
    {
        expect(form.isRewinding()).andReturn(isRewindind);
    }

    protected void trainGetElementId(IForm form, IFormComponent field, String name)
    {
        expect(form.getElementId(field)).andReturn(name);
    }

    protected void trainGetDelegate(IForm form, IValidationDelegate delegate)
    {
        expect(form.getDelegate()).andReturn(delegate);
    }

    protected void trainWasPrerendered(IForm form, IMarkupWriter writer, IFormComponent field,
            boolean wasPrendered)
    {
        expect(form.wasPrerendered(writer, field)).andReturn(wasPrendered);
    }

    protected void trainGetForm(IRequestCycle cycle, IForm form)
    {
        trainGetAttribute(cycle, TapestryUtils.FORM_ATTRIBUTE, form);
    }

    protected IValidationDelegate newDelegate()
    {
        return newMock(IValidationDelegate.class);
    }

}
